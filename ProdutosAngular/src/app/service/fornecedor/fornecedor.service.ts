import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { FornecedorDTO } from '../../model/dto/FornecedorDTO';
import { PaginatedResponse } from '../../paginated-response';
import { HttpBaseService } from '../base/http-base.service';
import { DadosEmpresaDTO } from '../../model/dto/DadosEmpresaDTO';

@Injectable({
  providedIn: 'root'
})
export class FornecedorService extends HttpBaseService {
  private readonly fornecedorUrl: string;

  constructor(
    private http: HttpClient,
    authService: AuthService,
    router: Router
  ) {
    super(authService, router);
    this.fornecedorUrl = environment.API_URL + '/fornecedores';
  }

 

  acessarPaginaFornecedor(): Observable<any> {
    return this.http.get<any>(this.fornecedorUrl + "/acessarPaginaFornecedor")
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  listarFornecedores(page: number, size: number, idUsuario: number): Observable<PaginatedResponse<FornecedorDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<FornecedorDTO>>(`${this.fornecedorUrl}/listarFornecedores/${idUsuario}`, { params })
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  adicionarFornecedor(fornecedor: FornecedorDTO): Observable<FornecedorDTO> {
    return this.http.post<FornecedorDTO>(`${this.fornecedorUrl}/adicionarFornecedor/${fornecedor.idUsuario}`, fornecedor)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  atualizarFornecedor(id: number, idUsuario: number, fornecedor: FornecedorDTO): Observable<FornecedorDTO> {
    return this.http.put<FornecedorDTO>(`${this.fornecedorUrl}/atualizarFornecedor/${id}/${idUsuario}`, fornecedor)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  deletarFornecedor(id: number, idUsuario: number): Observable<HttpResponse<boolean>> {
    return this.http.delete<boolean>(this.fornecedorUrl + "/deletarFornecedor/" + id + "/" + idUsuario, {
        observe: 'response'
    }).pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }
  
  buscarFornecedorPorId(id: number): Observable<FornecedorDTO> {
    return this.http.get<FornecedorDTO>(`${this.fornecedorUrl}/buscarFornecedorPorId/${id}`)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  listarFornecedoresList(idUsuario: number): Observable<FornecedorDTO[]> {
    return this.http.get<FornecedorDTO[]>(`${this.fornecedorUrl}/listarFornecedoresList/${idUsuario}`)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  // OBTEM DADOS DA EMPRESA ATRAVÉS DO CNPJ
  obterEmpresaViaCNPJ(cnpj: string): Observable<DadosEmpresaDTO> {
    // Remove caracteres não numéricos do CNPJ
    const cnpjLimpo = cnpj.replace(/\D/g, '');
    return this.http.get<DadosEmpresaDTO>(this.fornecedorUrl + "/consultarCNPJ/" + cnpjLimpo)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }
}
