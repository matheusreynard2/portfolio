import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
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

  public addNovoAcessoIp(): Observable<boolean> {
    return this.http.post<boolean>(this.usuariosUrl + "/addNovoAcessoIp", {})
  }

  public getAllAcessosIp(): Observable<number> {
    return this.http.get<number>(this.usuariosUrl + "/getAllAcessosIp", {})
  }
}


