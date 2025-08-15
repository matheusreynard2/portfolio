import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { StorageService } from '../storage/storage.service';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessToken$ = new BehaviorSubject<string | null>(null);

  private readonly USUARIO_KEY = 'usuarioLogado';
  private readonly TOKEN_EXPIRADO_KEY = 'tokenExpirado';
  private readonly LOGOUT_EM_ANDAMENTO_KEY = 'logoutEmAndamento';
  private readonly authUrl = `${environment.API_URL}/auth`;

  // Controle de atividade e refresh proativo
  private lastActivityAtMs = Date.now();
  private proactiveTimer: any = null;
  private accessTokenExpMs: number | null = null;

  constructor(
    private http: HttpClient,
    private router: Router,
    private storage: StorageService
  ) {}

  // ===== Novo fluxo com Access Token em memória =====
  getAccessToken(): string | null {
    return this.accessToken$.value;
  }

  setAccessToken(token: string | null): void {
    this.accessToken$.next(token);
    // Atualiza expiração do token e agenda refresh proativo
    this.accessTokenExpMs = this.extractExpMs(token);
    this.ensureProactiveRefreshTimer();
  }

  realizarLogin(usuario: UsuarioDTO): Observable<{ accessToken: string; usuario: UsuarioDTO }> {
    return this.http.post<{ accessToken: string; usuario: UsuarioDTO }>(
      `${this.authUrl}/realizarLogin`,
      usuario,
      { withCredentials: true }
    );
  }

  logout(): void {
    this.http.post(`${this.authUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.setAccessToken(null);
        this.storage.removeItem(this.USUARIO_KEY);
        this.storage.removeItem(this.TOKEN_EXPIRADO_KEY);
        this.router.navigate(['/login']);
      },
      error: () => {
        this.setAccessToken(null);
        this.storage.removeItem(this.USUARIO_KEY);
        this.storage.removeItem(this.TOKEN_EXPIRADO_KEY);
        this.router.navigate(['/login']);
      }
    });
  }

  // ===== Compatibilidade com código existente =====
  adicionarUsuarioLogado(usuario: UsuarioDTO): void {
    this.storage.setItem(this.USUARIO_KEY, usuario);
  }

  getUsuarioLogado(): UsuarioDTO {
    return this.storage.getItem<UsuarioDTO>(this.USUARIO_KEY, {} as UsuarioDTO);
  }

  // getToken/Adicionar/Remover/Existe usam accessToken em memória agora
  getToken(): string {
    return this.getAccessToken() ?? '';
  }

  adicionarToken(token: string): void {
    this.setAccessToken(token);
  }

  removerToken(): void {
    this.setAccessToken(null);
  }

  existeToken(): boolean {
    return !!this.getAccessToken();
  }

  adicionarTokenExpirado(valor: string): void {
    this.storage.setItem(this.TOKEN_EXPIRADO_KEY, valor);
  }

  removerTokenExpirado(): void {
    this.storage.removeItem(this.TOKEN_EXPIRADO_KEY);
  }

  existeTokenExpirado(): boolean {
    return this.storage.exists(this.TOKEN_EXPIRADO_KEY);
  }

  // Tenta obter um novo access token usando o refresh cookie (HttpOnly)
  async trySilentRefresh(): Promise<boolean> {
    try {
      const res = await firstValueFrom(
        this.http.post<{ accessToken: string }>(`${this.authUrl}/refresh`, {}, { withCredentials: true })
      );
      if (res && res.accessToken) {
        this.setAccessToken(res.accessToken);
        return true;
      }
      return false;
    } catch (_) {
      return false;
    }
  }

  // ================== Atividade e Refresh Proativo ==================
  initActivityMonitor(): void {
    const mark = () => {
      this.lastActivityAtMs = Date.now();
    };
    window.addEventListener('click', mark);
    window.addEventListener('keydown', mark);
    window.addEventListener('mousemove', mark);
    window.addEventListener('touchstart', mark);
  }

  isUserActiveWithin(ms: number): boolean {
    return Date.now() - this.lastActivityAtMs <= ms;
  }

  private ensureProactiveRefreshTimer(): void {
    if (this.proactiveTimer) {
      clearInterval(this.proactiveTimer);
      this.proactiveTimer = null;
    }
    // Checa a cada 60s
    this.proactiveTimer = setInterval(async () => {
      const token = this.getAccessToken();
      if (!token || !this.accessTokenExpMs) return;
      const msLeft = this.accessTokenExpMs - Date.now();
      // Se faltam < 2min e houve atividade no último 1min, tenta refresh silencioso
      if (msLeft > 0 && msLeft < 120_000 && this.isUserActiveWithin(60_000)) {
        await this.trySilentRefresh();
      }
    }, 60_000);
  }

  // EXTRAIR TEMPO DE EXPIRAÇÃO DO TOKEN
  private extractExpMs(token: string | null): number | null {
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = JSON.parse(atob(base64));
      if (json && typeof json.exp === 'number') {
        return json.exp * 1000;
      }
      return null;
    } catch {
      return null;
    }
  }
}