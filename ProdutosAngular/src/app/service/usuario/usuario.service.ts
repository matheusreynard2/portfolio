import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import {Usuario} from '../../model/usuario';
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

  adicionarUsuario(usuario: UsuarioDTO, imagem: File): Observable<Map<string, any>> {
    const formData = this.createUsuarioFormData(usuario, imagem);
    return this.http.post<any>(this.usuariosUrl + "/adicionarUsuario", formData);
  }

  public addNovoAcessoIp(): Observable<boolean> {
    return this.http.post<boolean>(this.usuariosUrl + "/addNovoAcessoIp", {})
  }

  public getAllAcessosIp(): Observable<number> {
    return this.http.get<number>(this.usuariosUrl + "/getAllAcessosIp", {})
  }
}


