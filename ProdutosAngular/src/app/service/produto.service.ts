import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  // ENDPOINT GET - Listar todos os produtos
  public listarProdutos(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/listarProdutos");
  }

  // ENDPOINT POST - Adicionar / Cadastrar um novo produto
  public adicionarProduto(produto: Produto) {
    return this.http.post<Produto>(this.produtosUrl + "/adicionarProduto", produto);
  }

  // ENDPOINT DELETE - Deletar / Excluir um produto
  public deletarProduto(id: number) {
    return this.http.delete(this.produtosUrl + "/deletarProduto/" + id);
  }

  // ENDPOINT PUT - Atualizar um produto
  public atualizarProduto(id: number, produto: Produto) {
    return this.http.put<Produto>(this.produtosUrl + "/atualizarProduto/" + id, produto);
  }

}
