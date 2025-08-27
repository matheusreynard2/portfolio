import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject, firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { StorageService } from '../storage/storage.service';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // ===== Config =====
  private readonly INACTIVITY_WINDOW_MS = environment.INACTIVITY_WINDOW_MS

  // ===== Token em memória =====
  private accessToken$ = new BehaviorSubject<string | null>(null);

  // ===== Chaves e URLs =====
  private readonly USUARIO_KEY = 'usuarioLogado';
  private readonly TOKEN_EXPIRADO_KEY = 'tokenExpirado';
  private readonly LAST_ACTIVITY_KEY = 'lastActivityAtMs';
  private readonly LAST_NAV_KEY = 'lastNavigationAtMs';
  private readonly INACTIVITY_LOGOUT_KEY = 'inactivityLogoutTs'; // sessionStorage + timestamp
  private readonly authUrl = `${environment.API_URL}/auth`;

  // ===== Estado de atividade =====
  private lastActivityAtMs = 0;
  private lastNavigationAtMs = 0;
  private idleExpired = false;
  private activity$ = new Subject<number>();

  // ===== Auxiliares =====
  private proactiveTimer: any = null;
  private accessTokenExpMs: number | null = null;
  private refreshing = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private storage: StorageService
  ) {
    const savedAct = Number(localStorage.getItem(this.LAST_ACTIVITY_KEY));
    this.lastActivityAtMs = Number.isFinite(savedAct) ? savedAct : 0;

    const savedNav = Number(localStorage.getItem(this.LAST_NAV_KEY));
    this.lastNavigationAtMs = Number.isFinite(savedNav) ? savedNav : 0;

    const last = Math.max(this.lastActivityAtMs, this.lastNavigationAtMs);
    if (!(Number.isFinite(last) && last > 0) || (Date.now() - last) > this.INACTIVITY_WINDOW_MS) {
      this.idleExpired = true;
    }
  }

  // ================== Token (memória) ==================
  getAccessToken(): string | null { return this.accessToken$.value; }
  onAccessToken$(): Observable<string | null> { return this.accessToken$.asObservable(); }

  setAccessToken(token: string | null): void {
    this.accessToken$.next(token);
    this.accessTokenExpMs = this.extractExpMs(token);
    // não marcar atividade aqui
    this.ensureProactiveRefreshTimer();
  }

  /** iniciar sessão ativa pós-login */
  startActiveSession(): void {
    const now = Date.now();
    this.idleExpired = false;
    this.lastActivityAtMs = now;
    this.lastNavigationAtMs = now;
    localStorage.setItem(this.LAST_ACTIVITY_KEY, String(now));
    localStorage.setItem(this.LAST_NAV_KEY, String(now));
    this.activity$.next(now);
  }

  // ================== Sessão (boot) ==================
  async initSessionFromRefresh(): Promise<boolean> {
    this.refreshing = true;
    try {
      return await this.refreshToken(); // seta token, mas NÃO marca atividade
    } finally {
      this.refreshing = false;
    }
  }
  isRefreshing(): boolean { return this.refreshing; }

  // ================== Fluxo de autenticação ==================
  realizarLogin(usuario: UsuarioDTO) {
    return this.http.post<{ accessToken: string; usuario: UsuarioDTO }>(
      `${this.authUrl}/realizarLogin`,
      usuario,
      { withCredentials: true }
    );
  }

  /** Logout normal (menu "sair") — limpa a flag de inatividade */
  logout(): void {
    this.http.post(`${this.authUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => this.performLocalLogout(false),
      error: () => this.performLocalLogout(false)
    });
  }

  /** Logout por inatividade — preserva a flag para o Login ler */
  logoutPorInatividade(): void {
    sessionStorage.setItem(this.INACTIVITY_LOGOUT_KEY, String(Date.now())); // marca motivo + timestamp
    this.http.post(`${this.authUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => this.performLocalLogout(true),
      error: () => this.performLocalLogout(true)
    });
  }

  /** Apaga chaves locais e navega ao login. preserveInactivityFlag: true para manter modal. */
  private performLocalLogout(preserveInactivityFlag: boolean) {
    this.setAccessToken(null);
    this.storage.removeItem(this.USUARIO_KEY);
    this.storage.removeItem(this.TOKEN_EXPIRADO_KEY);
    localStorage.removeItem(this.LAST_ACTIVITY_KEY);
    localStorage.removeItem(this.LAST_NAV_KEY);
    this.lastActivityAtMs = 0;
    this.lastNavigationAtMs = 0;
    this.idleExpired = false;

    if (!preserveInactivityFlag) {
      sessionStorage.removeItem(this.INACTIVITY_LOGOUT_KEY);
    }

    this.router.navigate(['/login']);
  }

  removerChaves() {
    // Mantido para compatibilidade: trata como logout normal
    this.performLocalLogout(false);
  }

  adicionarUsuarioLogado(usuario: UsuarioDTO): void {
    this.storage.setItem(this.USUARIO_KEY, usuario);
  }
  getUsuarioLogado(): UsuarioDTO {
    return this.storage.getItem<UsuarioDTO>(this.USUARIO_KEY, {} as UsuarioDTO);
  }

  adicionarToken(token: string): void { this.setAccessToken(token); }
  removerToken(): void { this.setAccessToken(null); }
  existeToken(): boolean { return !!this.getAccessToken(); }

  adicionarTokenExpirado(v: string): void { this.storage.setItem(this.TOKEN_EXPIRADO_KEY, v); }
  removerTokenExpirado(): void { this.storage.removeItem(this.TOKEN_EXPIRADO_KEY); }
  existeTokenExpirado(): boolean { return this.storage.exists(this.TOKEN_EXPIRADO_KEY); }

  async refreshToken(): Promise<boolean> {
    try {
      const res = await firstValueFrom(
        this.http.post<{ accessToken: string }>(
          `${this.authUrl}/refresh`, {}, { withCredentials: true }
        )
      );
      if (res?.accessToken) {
        this.setAccessToken(res.accessToken); // não marca atividade
        return true;
      }
      return false;
    } catch {
      return false;
    }
  }

  // ================== Atividade ==================
  initActivityMonitor(): void {
    const mark = () => this.markUserActivity();
    window.addEventListener('click', mark, { passive: true });
    window.addEventListener('keydown', mark, { passive: true });
    window.addEventListener('touchstart', mark, { passive: true });
    document.addEventListener('visibilitychange', () => {
      if (!document.hidden) this.markUserActivity();
    }, { passive: true });
  }

  markUserActivity(): void {
    const now = Date.now();
    const last = Math.max(this.lastActivityAtMs, this.lastNavigationAtMs);

    if (!Number.isFinite(last) || last <= 0 || (now - last) > this.INACTIVITY_WINDOW_MS) {
      // atividade tardia: sessão segue expirada
      this.idleExpired = true;
      this.activity$.next(now);
      return;
    }
    this.lastActivityAtMs = now;
    localStorage.setItem(this.LAST_ACTIVITY_KEY, String(now));
    this.activity$.next(now);
  }

  markNavigation(): void {
    const now = Date.now();
    const last = Math.max(this.lastActivityAtMs, this.lastNavigationAtMs);

    if (!Number.isFinite(last) || last <= 0 || (now - last) > this.INACTIVITY_WINDOW_MS) {
      this.idleExpired = true;
      return;
    }
    this.lastNavigationAtMs = now;
    localStorage.setItem(this.LAST_NAV_KEY, String(now));
    this.activity$.next(now);
  }

  onActivity$(): Observable<number> { return this.activity$.asObservable(); }

  isActiveWithin(windowMs: number): boolean {
    if (this.idleExpired) return false;
    const lastAct = this.lastActivityAtMs || Number(localStorage.getItem(this.LAST_ACTIVITY_KEY)) || 0;
    const lastNav = this.lastNavigationAtMs || Number(localStorage.getItem(this.LAST_NAV_KEY)) || 0;
    const last = Math.max(lastAct, lastNav);
    return Number.isFinite(last) && last > 0 && (Date.now() - last) <= windowMs;
  }

  // ================== Expiração do token ==================
  isTokenExpiringWithin(ms: number): boolean {
    if (!this.accessTokenExpMs) return false;
    return (this.accessTokenExpMs - Date.now()) <= ms;
  }

  private ensureProactiveRefreshTimer(): void {
    if (this.proactiveTimer) { clearInterval(this.proactiveTimer); this.proactiveTimer = null; }
    this.proactiveTimer = setInterval(async () => {
      const token = this.getAccessToken();
      if (!token || !this.accessTokenExpMs) return;
      const msLeft = this.accessTokenExpMs - Date.now();
      if (msLeft > 0 && msLeft < 120_000) {
        await this.refreshToken();
      }
    }, 60_000);
  }

  private extractExpMs(token: string | null): number | null {
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = JSON.parse(atob(base64));
      return (json && typeof json.exp === 'number') ? json.exp * 1000 : null;
    } catch { return null; }
  }
}
