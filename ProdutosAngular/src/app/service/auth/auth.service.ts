import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { StorageService } from '../storage/storage.service';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly USUARIO_KEY = 'usuarioLogado';
  private readonly TOKEN_KEY = 'bearerToken';
  private readonly TOKEN_EXPIRADO_KEY = 'tokenExpirado';
  private readonly usuariosUrl: string;

  constructor(
    private http: HttpClient,
    private router: Router,
    private storage: StorageService
  ) {
    this.usuariosUrl = environment.API_URL + '/usuarios';
  }

  adicionarUsuarioLogado(usuario: UsuarioDTO): void {
    this.storage.setItem(this.USUARIO_KEY, usuario);
  }

  getUsuarioLogado(): UsuarioDTO {
    return this.storage.getItem<UsuarioDTO>(this.USUARIO_KEY, {} as UsuarioDTO);
  }

  adicionarToken(token: string): void {
    this.storage.setItem(this.TOKEN_KEY, token);
  }

  removerToken(): void {
    this.storage.removeItem(this.TOKEN_KEY);
  }

  existeToken(): boolean {
    return this.storage.exists(this.TOKEN_KEY);
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

  getToken(): string {
    return this.storage.getItem<string>(this.TOKEN_KEY, '');
  }

  realizarLogin(usuario: UsuarioDTO): Observable<UsuarioDTO> {
    return this.http.post<UsuarioDTO>(this.usuariosUrl + "/realizarLogin", usuario);
  }

  logout(): void {
    this.storage.removeItem(this.USUARIO_KEY);
    this.storage.removeItem(this.TOKEN_KEY);
    this.storage.removeItem(this.TOKEN_EXPIRADO_KEY);
    this.storage.clear();
    this.router.navigate(['/login']);
  }
}