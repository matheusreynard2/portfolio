import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Router} from '@angular/router';
import {environment} from '../../../environments/environment';
import { Usuario } from '../../model/usuario';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private usuariosUrl: string;
  private apiUrl = environment.API_URL;

  constructor(private http: HttpClient, private router: Router) {
    // URL DO REST CONTROLLER
    this.usuariosUrl = this.apiUrl + '/usuarios';
  }

  adicionarUsuarioLogado(usuario: Usuario) {
    localStorage.setItem('usuarioLogado', JSON.stringify(usuario))
  }

  removerUsuarioLogado() {
    localStorage.removeItem('usuarioLogado');
  }

  getUsuarioLogado(): Usuario {
    return JSON.parse(localStorage.getItem('usuarioLogado') || '{}');
  }

  adicionarTokenExpirado(valor: string) {
    localStorage.setItem('tokenExpirado', valor)
  }

  removerTokenExpirado() {
    localStorage.removeItem('tokenExpirado');
  }

  existeTokenExpirado(): boolean {
    const estaExpirado = localStorage.getItem('tokenExpirado')
    return !!estaExpirado
  }

  adicionarToken(token: string) {
    localStorage.setItem('bearerToken', token);
  }

  removerToken() {
    localStorage.removeItem('bearerToken');
  }

  // Método para verificar se o token de autenticação está presente
  existeToken(): boolean {
    const token = localStorage.getItem('bearerToken');
    return !!token;  // Retorna true se o token existir, caso contrário false
  }

  realizarLogin(usuario: Usuario): Observable<Map<string, any>> {
    return this.http.post<Map<string, any>>(this.usuariosUrl + "/realizarLogin", usuario).pipe(
      map(response => {
        return new Map<string, any>((Object.entries(response)))
      })
    )
  }

  logout() {
    this.removerToken()
    this.removerTokenExpirado()
    this.removerUsuarioLogado()
    this.router.navigate(['/login']);
  }

}
