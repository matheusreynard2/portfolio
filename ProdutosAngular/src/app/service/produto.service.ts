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
    this.produtosUrl = 'http://localhost:8080/api/produtos';
  }

  public listarProdutos(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.produtosUrl + "/listarProdutos");
  }

  public adicionarProduto(produto: Produto) {
    return this.http.post<Produto>(this.produtosUrl + "/adicionarProduto", produto);
  }
}
