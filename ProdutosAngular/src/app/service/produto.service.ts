import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Produto } from '../model/produto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'  // Garante que o serviço esteja disponível globalmente em toda a aplicação
})
export class ProdutoService {

  private produtosUrl: string;

  constructor(private http: HttpClient) {
    // URL DO REST CONTROLLER
    this.produtosUrl = 'http://localhost:8080/api/produtos';
  }

  // ENDPOINT GET - Listar produtos pela barra de pesquisa
  public efetuarPesquisa(tipoPesquisa: string, valorPesquisa: string): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/efetuarPesquisa/" + tipoPesquisa + "/" + valorPesquisa);
  }

  // ENDPOINT GET - Listar todos os produtos
  public listarProdutos(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/listarProdutos");
  }

  // ENDPOINT POST - Adicionar / Cadastrar um novo produto
    public adicionarProduto(produto: Produto, imagem: File) {

    const formData = new FormData();
    const headers = new HttpHeaders();
    formData.append('produtoJSON', JSON.stringify(produto)); // Produto como JSON
    formData.append('imagemFile', imagem);                   // Arquivo de imagem

    return this.http.post<Produto>(this.produtosUrl + "/adicionarProduto/", formData, { headers });
  }

  // ENDPOINT DELETE - Deletar / Excluir um produto
  public deletarProduto(id: number) {
    return this.http.delete(this.produtosUrl + "/deletarProduto/" + id);
  }

  // ENDPOINT PUT - Atualizar um produto
  public atualizarProduto(id: number, produto: Produto, imagem: File) {
    const formData = new FormData();
    const headers = new HttpHeaders();
    formData.append('produtoJSON', JSON.stringify(produto)); // Produto como JSON
    formData.append('imagemFile', imagem);                   // Arquivo de imagem

    return this.http.put<Produto>(this.produtosUrl + "/atualizarProduto/" + id, formData,  { headers });
  }

  // ENDPOINT GET - Listar produto mais caro
  public listarProdutoMaisCaro(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/produtoMaisCaro");
  }

  // ENDPOINT GET - Calcular média dos valores unitários
  public calcularMedia(): Observable<number> {
    return this.http.get<number>(this.produtosUrl + "/mediaPreco");
  }

  // ENDPOINT GET - Calcula o valor de desconto sobre o produto
  public calcularDesconto(valorProduto: number, porcentagemDesconto: number): Observable<number> {
    return this.http.get<number>(this.produtosUrl + "/calcularDesconto/" + valorProduto + '/' + porcentagemDesconto);
  }

}
