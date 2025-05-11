import { Injectable } from '@angular/core';
import {Produto} from '../../model/produto';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {PaginatedResponse} from '../../paginated-response';
import {Fornecedor} from '../../model/fornecedor';
import {environment} from '../../../environments/environment';
import {AuthService} from '../auth/auth.service';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class FornecedorService {

  private fornecedorUrl: string;
  private apiUrl = environment.API_URL;

  constructor(private http: HttpClient, private authService: AuthService, private router: Router) { this.fornecedorUrl = this.apiUrl + '/fornecedores'; }

  // Se o token expirou, remove o token
  public verificarRedirecionar() {
    if (this.authService.existeToken()) {
      this.authService.removerToken()
      this.authService.adicionarTokenExpirado('true')
      this.router.navigate(['/login']);
    }
  }

  public adicionarFornecedor(fornecedor: Fornecedor) {
    return this.http.post<Fornecedor>(this.fornecedorUrl + "/adicionarFornecedor", fornecedor).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    );
  }

  public acessarPaginaFornecedor() {
    return this.http.get<any>(this.fornecedorUrl + "/acessarPaginaFornecedor").pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    )
  }


   public listarFornecedores(page: number, size: number): Observable<PaginatedResponse<Fornecedor>> {

     const params = new HttpParams()
       .set('page', page.toString())
       .set('size', size.toString());

     return this.http.get<PaginatedResponse<Fornecedor>>(this.fornecedorUrl + "/listarFornecedores", {params}).pipe(
       // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
       catchError((error: HttpErrorResponse) => {
         if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
           this.verificarRedirecionar()
         return throwError(error);
       })
     );
  }

}
