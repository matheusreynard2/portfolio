import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import {environment} from '../../../environments/environment';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';
import { HttpBaseService } from '../base/http-base.service';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService extends HttpBaseService {
  private usuariosUrl: string;
  private apiUrl = environment.API_URL;


  constructor(
    private http: HttpClient,
    authService: AuthService,
    router: Router
  ) {
    super(authService, router);
    this.usuariosUrl = this.apiUrl + '/usuarios';
  }

  adicionarUsuario(usuario: UsuarioDTO, imagem: File): Observable<UsuarioDTO> {
    const formData = this.createUsuarioFormData(usuario, imagem);
    return this.http.post<UsuarioDTO>(this.usuariosUrl + "/adicionarUsuario", formData)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  public addNovoAcessoIp(): Observable<boolean> {
    return this.http.post<boolean>(this.usuariosUrl + "/addNovoAcessoIp", {})
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  public getAllAcessosIp(): Observable<number> {
    return this.http.get<number>(this.usuariosUrl + "/getAllAcessosIp", {})
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }
}


