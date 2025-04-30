import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {catchError, Observable, of, switchMap, throwError} from 'rxjs';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import {Usuario} from '../../model/usuario';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private usuariosUrl: string;
  private apiUrl = environment.API_URL;
  private numeroAcesso: number = 0

  constructor(private http: HttpClient, private router: Router) {
    // URL DO REST CONTROLLER
    this.usuariosUrl = this.apiUrl + '/usuarios';
  }

  adicionarUsuario(usuario: Usuario, imagem: File): Observable<Map<string, any>> {
    const formData = new FormData();
    const headers = new HttpHeaders();

    formData.append('usuarioJSON', JSON.stringify(usuario));
    formData.append('imagemFile', imagem);

    return this.http.post<any>(this.usuariosUrl + "/adicionarUsuario", formData, {headers});
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
