import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { PaginatedResponse } from '../../paginated-response';
import { environment } from '../../../environments/environment';
import { FornecedorDTO } from '../../model/dto/FornecedorDTO';
import { ProdutoDTO } from '../../model/dto/ProdutoDTO';
import { Produto } from '../../model/produto';
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
      .pipe(catchError(error => this.handleError(error)));
  }

  efetuarPesquisa(tipoPesquisa: string, valorPesquisa: string, idUsuario: number): Observable<ProdutoDTO[]> {
    return this.http.get<ProdutoDTO[]>(
      `${this.produtosUrl}/efetuarPesquisa/${tipoPesquisa}/${valorPesquisa}/${idUsuario}`
    ).pipe(catchError(error => this.handleError(error)));
  }

  listarProdutos(page: number, size: number): Observable<PaginatedResponse<ProdutoDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<ProdutoDTO>>(this.produtosUrl + "/listarProdutos", { params })
      .pipe(catchError(error => this.handleError(error)));
  }

  adicionarProduto(produto: ProdutoDTO, imagem: File): Observable<ProdutoDTO> {
    const formData = this.createProdutoFormData(produto, imagem);
    return this.http.post<ProdutoDTO>(this.produtosUrl + "/adicionarProduto", formData)
      .pipe(catchError(error => this.handleError(error)));
  }

  deletarProduto(id: number): Observable<any> {
    return this.http.delete(this.produtosUrl + "/deletarProduto/" + id, {
      observe: 'response',
      responseType: 'text'
    }).pipe(catchError(error => this.handleError(error)));
  }

  atualizarProduto(id: number, produto: ProdutoDTO, imagem: File): Observable<ProdutoDTO> {
    const formData = this.createProdutoFormData(produto, imagem);
    return this.http.put<ProdutoDTO>(this.produtosUrl + "/atualizarProduto/" + id, formData)
      .pipe(catchError(error => this.handleError(error)));
  }

  listarProdutoMaisCaro(idUsuario: number): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/produtoMaisCaro/" + idUsuario)
      .pipe(catchError(error => this.handleError(error)));
  }

  calcularMedia(idUsuario: number): Observable<number> {
    return this.http.get<number>(this.produtosUrl + "/mediaPreco/" + idUsuario)
      .pipe(catchError(error => this.handleError(error)));
  }

  calcularDesconto(valorProduto: number, porcentagemDesconto: number): Observable<number> {
    return this.http.get<number>(
      `${this.produtosUrl}/calcularDesconto/${valorProduto}/${porcentagemDesconto}`
    ).pipe(catchError(error => this.handleError(error)));
  }

  listarFornecedoresList(idUsuario: number): Observable<FornecedorDTO[]> {
    return this.http.get<FornecedorDTO[]>(this.fornecedorUrl + "/listarFornecedoresList/" + idUsuario)
      .pipe(catchError(error => this.handleError(error)));
  }

  buscarProdutoPorId(id: number, idUsuario: number): Observable<ProdutoDTO> {
    return this.http.get<ProdutoDTO>(`${this.produtosUrl}/buscarProduto/${id}/${idUsuario}`)
      .pipe(catchError(error => this.handleError(error)));
  }
}
