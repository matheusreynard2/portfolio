import { Component, OnInit } from '@angular/core';
import { Produto } from '../../model/produto';
import { ProdutoService } from '../../service/produto.service';
import {NgForOf} from '@angular/common';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: '/app-produto-list',
  templateUrl: './produto-list.component.html',
  imports: [
    NgForOf,
    NgbModule
  ],
  styleUrls: ['./produto-list.component.css']
})

export class ProdutoListComponent implements OnInit {

  listaDeProdutos: Produto[] = [];
  produtoAtualizado!: Produto;
  private modalService: NgbModal = new NgbModal();

  constructor(private produtoService: ProdutoService) { }

  // Ao abrir a página, chama o endpoint para preencher a lista de produtos
  ngOnInit() {
    this.produtoService.listarProdutos().subscribe(data => {
      this.listaDeProdutos = data.sort((a, b) => a.id - b.id);
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
  // Método para atualizar um produto através do id
  atualizarProduto(modalEditar: any, id: number, produto: Produto) {
    this.produtoService.atualizarProduto(id, produto).subscribe({
      next: (produtoRetornado: Produto) => {
        // Atribui o Produto retornado (produtoRetornado) ao produto que será atualizado
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

  // Método que exibe uma mensagem passada como parâmetro
  exibirAlerta(mensagem: string) {
    window.alert(mensagem)
  }

  // Função que abre o modal via HTML - Janela de edição de produto
  abrirTelaEdicao(modalEditar: any) {
    const modalRef = this.modalService.open(modalEditar);
  }

}
