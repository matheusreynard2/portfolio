import { Injectable } from '@angular/core';
import {Usuario} from '../../model/usuario';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {BehaviorSubject, catchError, map, Observable, throwError} from 'rxjs';7

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private usuariosUrl: string;

  constructor(private http: HttpClient, private router: Router) {
    // URL DO REST CONTROLLER
    this.usuariosUrl = 'http://localhost:8080/api/usuarios';
  }

  // Método para verificar se o token de autenticação está presente
  existeToken(): boolean {
    const token = localStorage.getItem('bearerToken');
    return !!token;  // Retorna true se o token existir, caso contrário false
  }

  realizarLogin(usuario: Usuario): Observable<Map<string, any>> {
    return this.http.post<Map<string, string>>(this.usuariosUrl + "/realizarLogin", usuario).pipe(
      map(response => {
        return new Map<string, string>((Object.entries(response)))
      })
    )
  }
}
