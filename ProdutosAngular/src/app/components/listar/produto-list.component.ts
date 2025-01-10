import { Component, OnInit } from '@angular/core';
import { Produto } from '../../model/produto';
import { ProdutoService } from '../../service/produto.service';
import {CurrencyPipe, NgForOf, NgIf, NgOptimizedImage, NgStyle} from '@angular/common';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { ProdutoFunctionsService } from '../../service/produto-functions.service';

@Component({
  selector: '/app-produto-list',
  templateUrl: './produto-list.component.html',
  imports: [
    NgForOf,
    NgbModule,
    NgOptimizedImage,
    CurrencyPipe,
    FormsModule,
    ReactiveFormsModule
  ],
  styleUrls: ['./produto-list.component.css']
})

export class ProdutoListComponent implements OnInit {

  // Variáveis
  listaProdutoMaisCaro: Produto[] = [];
  stringProdutoMaisCaro: string = '';
  mediaPreco: number = 0;

  listaDeProdutos!: Produto[];
  produtoAtualizar!: Produto;
  produtoExcluido!: Produto;

  // Services
  private modalService: NgbModal = new NgbModal();
  private produtoFunctionsService: ProdutoFunctionsService = new ProdutoFunctionsService();

  constructor(private produtoService: ProdutoService) { }

  // Ao abrir a página, chama os endpoints abaixo...
  ngOnInit() {
    this.listarProdutoMaisCaro();
    this.calcularMedia();
    // Lista e ordena os produtos por ID
    this.produtoService.listarProdutos().subscribe(data => {
      this.listaDeProdutos = data.sort((a, b) => a.id - b.id);
    });
  }

  // Função chamada ao clicar no botão de Submit (Salvar) do formulário de Edição de produtos
  onSubmitSalvar(modal: any) {
    this.calcularValores();
    this.produtoService.atualizarProduto(this.produtoAtualizar.id, this.produtoAtualizar).subscribe();
    modal.close();
  }

  // Função que calcula a média dos valores unitários
  calcularMedia() {
    this.produtoService.calcularMedia().subscribe((media: number) => {
        this.mediaPreco = media;
    });
  }

  // Função que busca o produto com Valor Total mais caro
  listarProdutoMaisCaro() {
  this.produtoService.listarProdutoMaisCaro().subscribe((produtos: Produto[]) => {
    this.listaProdutoMaisCaro = produtos;

    // Checa se existe registro na lista para preencher a string e exibir na tela
    if (this.listaProdutoMaisCaro[0] != null) {
      this.stringProdutoMaisCaro = '' + this.listaProdutoMaisCaro[0].id + ' - ' + this.listaProdutoMaisCaro[0].nome
    } else {
      this.stringProdutoMaisCaro = '';
    }
  })}

  // Função para deletar um produto através do id. Chama o endpoint, e a msg de sucesso
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

  // Função para atualizar um produto através do id
  atualizarProduto(modalEditar: any, id: number, produto: Produto) {
    this.produtoService.atualizarProduto(id, produto).subscribe({
      next: (produtoRetornado: Produto) => {
        this.produtoAtualizar = produtoRetornado
        this.calcularValores();
        this.abrirTelaEdicao(modalEditar)
      }
    })
  }

  // Função que formata o valor para R$ para ser exibido na tabela
  formatarValor(valor: number): string {
    return valor.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }

  // Função que atualiza a lista de produtos
  atualizarLista(): void {
    this.produtoService.listarProdutos().subscribe(data => {
      this.listaDeProdutos = data.sort((a, b) => a.id - b.id);
    });
    this.listarProdutoMaisCaro();
    this.calcularMedia();
  }

  // Função que abre o modal - Janela de edição de produto
  abrirTelaEdicao(modalEditar: any) {
    this.modalService.open(modalEditar);
  }

  // Função que abre o modal - Janela de exclusão de produto
  abrirTelaExclusao(modalExcluir: any) {
    this.modalService.open(modalExcluir);
    this.listarProdutoMaisCaro();
    this.calcularMedia();
    this.atualizarLista();
  }

  // Função que abre o modal - Janel de Aviso para Atualizar List de Produtos
  abrirTelaAviso(modalAviso: any) {
    this.modalService.open(modalAviso);
  }

  // Função chamada ao mudar de valor na ComboBox de Promoção no ngModel
  selecionarPromocao(selecionouPromocao: boolean): boolean {
    return this.produtoFunctionsService.selecionarPromocao(selecionouPromocao);
  }

  // Função chamada quando troca o valor da ComboBox Frete Ativo para saber se o Produto tem frete ou não
  ativarFrete(ativouFrete: boolean): boolean {
    return this.produtoFunctionsService.ativarFrete(ativouFrete);
  }

  // função chamada para calcular o valor do frete
  calcularFrete():number {
    return this.produtoFunctionsService.calcularFrete()
  }

  // Calcula os totalizadores de valor. função chamada ao clicar no botão Calcular valores
  // e antes de gravar o produto no banco de dados no onSubmit do formulário ngModel
  calcularValores() {
    // Desconto SIM e Frete SIM
    if (this.produtoAtualizar.promocao && this.produtoAtualizar.freteAtivo) {
      this.produtoAtualizar.valorTotalDesc = this.produtoAtualizar.valor - (this.produtoAtualizar.valor * 0.1)
      this.produtoAtualizar.valorTotalFrete = this.produtoAtualizar.valorTotalDesc + this.calcularFrete()
      this.produtoAtualizar.frete = this.calcularFrete()

      this.produtoAtualizar.somaTotalValores = this.produtoAtualizar.valorTotalDesc + this.produtoAtualizar.frete
    }

    // Desconto SIM e Frete NÃO
    if (this.produtoAtualizar.promocao && !this.produtoAtualizar.freteAtivo) {
      this.produtoAtualizar.frete = 0
      this.produtoAtualizar.valorTotalFrete = 0
      this.produtoAtualizar.valorTotalDesc = this.produtoAtualizar.valor - (this.produtoAtualizar.valor * 0.1)

      this.produtoAtualizar.somaTotalValores = this.produtoAtualizar.valorTotalDesc
    }

    // Desconto NÃO e Frete SIM
    if (!this.produtoAtualizar.promocao && this.produtoAtualizar.freteAtivo) {
      this.produtoAtualizar.valorTotalFrete = this.produtoAtualizar.valor + this.calcularFrete()
      this.produtoAtualizar.valorTotalDesc = 0
      this.produtoAtualizar.frete = this.calcularFrete();

      this.produtoAtualizar.somaTotalValores = this.produtoAtualizar.valorTotalFrete
    }

    // Desconto NÃO e Frete NÃO
    if (!this.produtoAtualizar.promocao && !this.produtoAtualizar.freteAtivo) {
      this.produtoAtualizar.frete = 0
      this.produtoAtualizar.valorTotalFrete = 0
      this.produtoAtualizar.valorTotalDesc = 0

      this.produtoAtualizar.somaTotalValores = this.produtoAtualizar.valor
    }

  }

}
