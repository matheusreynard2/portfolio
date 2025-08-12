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
}