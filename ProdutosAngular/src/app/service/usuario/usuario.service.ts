import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {Usuario} from '../../model/usuario';
import {BehaviorSubject, map, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private usuariosUrl: string;
  private loginExistente = new BehaviorSubject<boolean>(false);
  loginObservable = this.loginExistente.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // URL DO REST CONTROLLER
    this.usuariosUrl = 'http://localhost:8080/api/usuarios';
  }

  adicionarUsuario(usuario: Usuario): Observable<Map<string, any>> {
    return this.http.post<Map<string, any>>(this.usuariosUrl + "/adicionarUsuario", usuario).pipe(
      map(response => {
        return new Map<string, any>((Object.entries(response)))
      })
    )
  }
}
