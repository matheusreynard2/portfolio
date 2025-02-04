import { Injectable } from '@angular/core';
import {Usuario} from '../../model/usuario';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {BehaviorSubject, catchError, throwError} from 'rxjs';7

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private usuariosUrl: string;
  private credenciaisInvalidas = new BehaviorSubject<boolean>(false);
  credenciaisObservable = this.credenciaisInvalidas.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // URL DO REST CONTROLLER
    this.usuariosUrl = 'http://localhost:8080/api/usuarios';
  }

  // Método para verificar se o token de autenticação está presente
  existeToken(): boolean {
    const token = localStorage.getItem('bearerToken');
    return !!token;  // Retorna true se o token existir, caso contrário false
  }

  realizarLogin(usuario: Usuario) {
    return this.http.post<{ token: string }>(this.usuariosUrl + "/realizarLogin", usuario)
      .pipe(
        // Aqui fazemos o tratamento do erro 401 para credenciais inválidas
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401 && error.error.message === 'Credenciais inválidas.') {
            this.credenciaisInvalidas.next(true);  // Notifica o componente
          }
          return throwError(error);
        })
      )
      .subscribe(response => {
        const token = response.token;
        localStorage.setItem('bearerToken', token); // Armazena o token no localStorage

        if (this.existeToken()) {
          this.router.navigate(['/produtos']);
        }
      });
  }

}
