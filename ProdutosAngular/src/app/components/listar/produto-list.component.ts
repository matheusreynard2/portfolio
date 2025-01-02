import { Component, OnInit } from '@angular/core';
import { Produto } from '../../model/produto';
import { ProdutoService } from '../../service/produto.service';
import {NgForOf, NgOptimizedImage} from '@angular/common';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: '/app-produto-list',
  templateUrl: './produto-list.component.html',
  imports: [
    NgForOf,
    NgbModule,
    NgOptimizedImage
  ],
  styleUrls: ['./produto-list.component.css']
})

export class ProdutoListComponent implements OnInit {

  listaDeProdutos: Produto[] = [];
  produtoAtualizado!: Produto;
  private modalService: NgbModal = new NgbModal();
  produtoExcluido!: Produto;

  constructor(private produtoService: ProdutoService) { }

  // Ao abrir a página, chama o endpoint para preencher a lista de produtos
  ngOnInit() {
    this.produtoService.listarProdutos().subscribe(data => {
      this.listaDeProdutos = data.sort((a, b) => a.id - b.id);
    });
  }

  // Método para deletar um produto através do id
  deletarProduto(modalDeletar: any, id: number, produto: Produto) {
    this.produtoService.deletarProduto(id).subscribe({
      next: (response) => {
        if (response == true) {
          this.produtoExcluido = produto;
          this.abrirTelaExclusao(modalDeletar);
        }
      }
    })
  }
  // Método para atualizar um produto através do id
  atualizarProduto(modalEditar: any, id: number, produto: Produto) {
    this.produtoService.atualizarProduto(id, produto).subscribe({
      next: (produtoRetornado: Produto) => {
        // Atribui o Produto retornado pelo id fornecido(produtoRetornado)
        // ao produto que será atualizado e abre a tela de edição
        this.produtoAtualizado = produtoRetornado
        this.abrirTelaEdicao(modalEditar)
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

  // Função que abre o modal via HTML - Janela de edição de produto
  abrirTelaEdicao(modalEditar: any) {
    const modalRef = this.modalService.open(modalEditar);
  }

  // Função que abre o modal via HTML - Janela de exclusão de produto
  abrirTelaExclusao(modalExcluir: any) {
    const modalRef = this.modalService.open(modalExcluir);
    this.atualizarLista();
  }

}
