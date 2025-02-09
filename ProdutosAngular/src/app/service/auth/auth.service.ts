import { Injectable } from '@angular/core';
import {Usuario} from '../../model/usuario';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';7

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private usuariosUrl: string;

  usuarioLogado!: Usuario;

  constructor(private http: HttpClient) {
    // URL DO REST CONTROLLER
    this.usuariosUrl = 'http://localhost:8080/api/usuarios';
  }

  adicionarUsuarioLogado(usuario: Usuario) {
    this.usuarioLogado = usuario;
  }

  existeTokenExpirado(): boolean {
    const estaExpirado = localStorage.getItem('tokenExpirado')
    return !!estaExpirado
  }

  adicionarTokenExpirado(valor: string) {
    localStorage.setItem('tokenExpirado', valor)
  }

  removerTokenExpirado() {
    localStorage.removeItem('tokenExpirado');
  }

  // Método para verificar se o token de autenticação está presente
  existeToken(): boolean {
    const token = localStorage.getItem('bearerToken');
    return !!token;  // Retorna true se o token existir, caso contrário false
  }

  removerToken() {
    localStorage.removeItem('bearerToken');
  }

  adicionarToken(token: string) {
    localStorage.setItem('bearerToken', token);
  }

  getUsuarioLogado() {
    return this.usuarioLogado
  }

  realizarLogin(usuario: Usuario): Observable<Map<string, any>> {
    return this.http.post<Map<string, any>>(this.usuariosUrl + "/realizarLogin", usuario).pipe(
      map(response => {
        return new Map<string, any>((Object.entries(response)))
      })
    )
  }
}
