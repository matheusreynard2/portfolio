import { Component, OnInit } from '@angular/core';
import { Produto } from '../../model/produto';
import { ProdutoService } from '../../service/produto.service';
import {NgForOf} from '@angular/common';
import {Observable} from 'rxjs';

@Component({
  selector: '/app-produto-list',
  templateUrl: './produto-list.component.html',
  imports: [
    NgForOf
  ],
  styleUrls: ['./produto-list.component.css']
})

export class ProdutoListComponent implements OnInit {

  listaDeProdutos: Produto[] = [];

  constructor(private produtoService: ProdutoService) { }

  // Ao abrir a página, chama o endpoint para preencher a lista de produtos
  ngOnInit() {
    this.produtoService.listarProdutos().subscribe(data => {
      this.listaDeProdutos = data;
    });
  }

  // Método para deletar um produto através do id
  deletarProduto(id: number) {
    this.produtoService.deletarProduto(id).subscribe({
      next: (response) => {
        if (response == true) {
          this.exibirAlerta("Produto ID número " + id + " excluído com sucesso!")
          this.atualizarLista()
        }
      }
    })
  }

  // Método que formata o valor para R$ para ser exibido na tabela
  formatarValor(valor: number): string {
    return valor.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }

  // Método que atualiza a lista de produtos
  atualizarLista(): void {
    this.produtoService.listarProdutos().subscribe(data => {
      this.listaDeProdutos = data;
    });
  }

  // Método que exibe uma mensagem passada como parâmetro
  exibirAlerta(mensagem: string) {
    window.alert(mensagem)
  }

}
