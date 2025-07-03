import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { PaginatedResponse } from '../../paginated-response';
import { environment } from '../../../environments/environment';
import { FornecedorDTO } from '../../model/dto/FornecedorDTO';
import { ProdutoDTO } from '../../model/dto/ProdutoDTO';
import { HttpBaseService } from '../base/http-base.service';

@Injectable({
  providedIn: 'root'
})
export class ProdutoService extends HttpBaseService {
  private readonly produtosUrl: string;
  private readonly fornecedorUrl: string;

  constructor(
    private http: HttpClient,
    authService: AuthService,
    router: Router
  ) {
    super(authService, router);
    this.produtosUrl = environment.API_URL + '/produtos';
    this.fornecedorUrl = environment.API_URL + '/fornecedores';
  }

  acessarPaginaCadastro(): Observable<any> {
    return this.http.get<any>(this.produtosUrl + "/acessarPaginaCadastro")
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  efetuarPesquisa(idUsuario: number, id?: number | null, nome?: string | null, nomeFornecedor?: string | null, valorInicial?: number | null): Observable<ProdutoDTO[]> {
    let params = new HttpParams().set('idUsuario', idUsuario.toString());
    if (id !== undefined && id !== null) {
      params = params.set('idProduto', id.toString());
    }
    if (nome !== undefined && nome !== null && nome !== '') {
      params = params.set('nomeProduto', nome);
    }
    if (nomeFornecedor !== undefined && nomeFornecedor !== null && nomeFornecedor !== '') {
      params = params.set('nomeFornecedor', nomeFornecedor);
    }
    if (valorInicial !== undefined && valorInicial !== null) {
      params = params.set('valorInicial', valorInicial.toString());
    }
    console.log(params)
    return this.http.get<ProdutoDTO[]>(
      `${this.produtosUrl}/efetuarPesquisa`,
      { params }
    ).pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  listarProdutos(page: number, size: number, idUsuario: number): Observable<PaginatedResponse<ProdutoDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('idUsuario', idUsuario.toString());

    return this.http.get<PaginatedResponse<ProdutoDTO>>(this.produtosUrl + "/listarProdutos", { params })
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  adicionarProduto(produto: ProdutoDTO, imagem: File): Observable<ProdutoDTO> {
    const formData = this.createProdutoFormData(produto, imagem);
    return this.http.post<ProdutoDTO>(this.produtosUrl + "/adicionarProduto", formData)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  deletarProduto(id: number): Observable<HttpResponse<boolean>> {
    return this.http.delete<boolean>(this.produtosUrl + "/deletarProduto/" + id, {
        observe: 'response'
    }).pipe(catchError(error => this.catchErrorTokenExpirado(error)));
}

  atualizarProduto(id: number, produto: ProdutoDTO, imagem: File): Observable<ProdutoDTO> {
    const formData = this.createProdutoFormData(produto, imagem);
    return this.http.put<ProdutoDTO>(this.produtosUrl + "/atualizarProduto/" + id, formData)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  listarProdutoMaisCaro(idUsuario: number): Observable<ProdutoDTO> {
    return this.http.get<ProdutoDTO>(this.produtosUrl + "/produtoMaisCaro/" + idUsuario)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  calcularMedia(idUsuario: number): Observable<number> {
    return this.http.get<number>(this.produtosUrl + "/mediaPreco/" + idUsuario)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  calcularDesconto(valorProduto: number, porcentagemDesconto: number): Observable<number> {
    return this.http.get<number>(
      `${this.produtosUrl}/calcularDesconto/${valorProduto}/${porcentagemDesconto}`
    ).pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  listarFornecedoresList(idUsuario: number): Observable<FornecedorDTO[]> {
    return this.http.get<FornecedorDTO[]>(this.fornecedorUrl + "/listarFornecedoresList/" + idUsuario)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  buscarProdutoPorId(id: number, idUsuario: number): Observable<ProdutoDTO> {
    return this.http.get<ProdutoDTO>(`${this.produtosUrl}/buscarProduto/${id}/${idUsuario}`)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }
}
