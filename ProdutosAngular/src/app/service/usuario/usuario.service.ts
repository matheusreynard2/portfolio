import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {catchError, Observable, of, throwError} from 'rxjs';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import {Usuario} from '../../model/usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private usuariosUrl: string;
  private loginExistente = new BehaviorSubject<boolean>(false);
  private numeroAcesso: number = 0
  loginObservable = this.loginExistente.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // URL DO REST CONTROLLER
    this.usuariosUrl = 'http://localhost:8080/api/usuarios';
  }

  adicionarUsuario(usuario: Usuario, imagem: File): Observable<Map<string, any>> {
    const formData = new FormData();
    const headers = new HttpHeaders();

    // Adiciona o objeto usuario como JSON no FormData
    formData.append('usuarioJSON', JSON.stringify(usuario));
    // Adiciona a imagem ao FormData
    formData.append('imagemFile', imagem);

    // Envia a requisição POST com o FormData
    return this.http.post<Map<string, any>>(this.usuariosUrl + "/adicionarUsuario", formData, {headers}).pipe(
      map(response => {
        // Converte a resposta em um Map
        return new Map<string, any>(Object.entries(response));
      })
    );
  }

  public addNovoAcessoIp(): Observable<number> {
    return this.http.get<number>(this.usuariosUrl + "/addNovoAcessoIp")
      .pipe(
        map((valorRetornado: number) => {
          this.numeroAcesso = valorRetornado;
          return this.numeroAcesso;
        }),
        catchError((error) => {
          console.error("Erro ao obter número:", error);
          return throwError(error);
        })
      );
  }

}
