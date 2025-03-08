import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import { Produto } from '../../model/produto';
import {BehaviorSubject, catchError, Observable, throwError} from 'rxjs';
import {AuthService} from '../auth/auth.service';
import {Router} from '@angular/router';
import {PaginatedResponse} from '../../paginated-response';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'  // Garante que o serviço esteja disponível globalmente em toda a aplicação
})
export class ProdutoService {

  private produtosUrl: string;
  private apiUrl = environment.API_URL;

  constructor(private http: HttpClient, private authService: AuthService, private router: Router) {
    // URL DO REST CONTROLLER
    this.produtosUrl = this.apiUrl + '/produtos';
  }

  // Se o token expirou, remove o token
  public verificarRedirecionar() {
    if (this.authService.existeToken()) {
      this.authService.removerToken()
      this.authService.adicionarTokenExpirado('true')
      this.router.navigate(['/login']);
    }
  }

  // ========= OK ========= ENDPOINT GET - Permitir o acesso a página de cadastrar produtos
  public acessarPaginaCadastro() {
    return this.http.get<any>(this.produtosUrl + "/acessarPaginaCadastro").pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    )
  }

  // ENDPOINT GET - Listar produtos pela barra de pesquisa
  public efetuarPesquisa(tipoPesquisa: string, valorPesquisa: string): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/efetuarPesquisa/" + tipoPesquisa + "/" + valorPesquisa).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    );
  }

  // ========= OK ========= ENDPOINT GET - Listar todos os produtos
  public listarProdutos(page: number, size: number): Observable<PaginatedResponse<Produto>> {

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<Produto>>(this.produtosUrl + "/listarProdutos", {params}).pipe(
    // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
        this.verificarRedirecionar()
      return throwError(error);
      })
    );
  }

  // ========= OK ========= ENDPOINT POST - Adicionar / Cadastrar um novo produto
    public adicionarProduto(produto: Produto, imagem: File) {
    const formData = new FormData();
    const headers = new HttpHeaders();
    formData.append('produtoJSON', JSON.stringify(produto)); // Produto como JSON
    formData.append('imagemFile', imagem);                   // Arquivo de imagem

    return this.http.post<Produto>(this.produtosUrl + "/adicionarProduto", formData, { headers }).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    );
  }

  // ========= OK ========= ENDPOINT DELETE - Deletar / Excluir um produto
  public deletarProduto(id: number) {
    return this.http.delete(this.produtosUrl + "/deletarProduto/" + id).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    );
  }

  // ========= OK =========  ENDPOINT PUT - Atualizar um produto
  public atualizarProduto(id: number, produto: Produto, imagem: File) {
    const formData = new FormData();
    const headers = new HttpHeaders();
    formData.append('produtoJSON', JSON.stringify(produto)); // Produto como JSON
    formData.append('imagemFile', imagem);                   // Arquivo de imagem

    return this.http.put<Produto>(this.produtosUrl + "/atualizarProduto/" + id, formData,  { headers }).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    );
  }

  // ENDPOINT GET - Listar produto mais caro
  public listarProdutoMaisCaro(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/produtoMaisCaro");
  }

  // ENDPOINT GET - Calcular média dos valores unitários
  public calcularMedia(): Observable<number> {
    return this.http.get<number>(this.produtosUrl + "/mediaPreco");
  }

  // ========= OK ========= ENDPOINT GET - Calcula o valor de desconto sobre o produto
  public calcularDesconto(valorProduto: number, porcentagemDesconto: number): Observable<number> {
    return this.http.get<number>(this.produtosUrl + "/calcularDesconto/" + valorProduto + '/' + porcentagemDesconto).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        return throwError(error);
      })
    );
  }

}
