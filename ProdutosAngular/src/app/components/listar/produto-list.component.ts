import { Component, OnInit } from '@angular/core';
import { Produto } from '../../model/produto';
import { ProdutoService } from '../../service/produto.service';
import {CurrencyPipe, NgForOf, NgIf, NgOptimizedImage, NgStyle} from '@angular/common';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: '/app-produto-list',
  templateUrl: './produto-list.component.html',
  imports: [
    NgForOf,
    NgbModule,
    NgOptimizedImage,
    NgIf,
    CurrencyPipe,
    FormsModule,
    ReactiveFormsModule
  ],
  styleUrls: ['./produto-list.component.css']
})

export class ProdutoListComponent implements OnInit {

  // Esta variável abaixo é a lista que contém o produto com Valor Total mais caro
  listaProdutoMaisCaro: Produto[] = [];

  // Variável que extrai o nome do produto e o id do produto com o Valor Total mais caro
  stringProdutoMaisCaro: string = '';
  // Variável pra exibir o resultado da query que vem da API sobre média de preços
  mediaPreco: number = 0;

  // Variaveln para: armazenar a lista de produtos para listagem na tela
  listaDeProdutos!: Produto[];
  // Armazenar o produto a ser atualizado no modal de edição
  produtoAtualizar!: Produto;
  // Armazena o produto que aparece no modal de mensagem de exclusão
  produtoExcluido!: Produto;

  private modalService: NgbModal = new NgbModal();

  constructor(private produtoService: ProdutoService) { }

  // Ao abrir a página, chama os endpoints abaixo...
  ngOnInit() {
    // Chama o Função de listar o produto mais caro
    this.listarProdutoMaisCaro();
    // Chama o Função de calcular a média dos valores unitários
    this.calcularMedia();
    // Lista todos os produtos na tela
    this.produtoService.listarProdutos().subscribe(data => {
      // Lista e ordena os produtos por ID, em ordem crescente (de 'a' para 'b', de '0' a '10', por exemplo)
      this.listaDeProdutos = data.sort((a, b) => a.id - b.id);
    });
  }

  // Função chamada ao clicar no botão de Submit (Salvar) do formulário de Edição de produtos
  onSubmitSalvar(modal: any) {
    // Chama o Função de calcular os totalizadores de valor antes de salvar o produto no banco de dados
    this.calcularValores();
    // Salva o produto no banco de dados e fecha o modal
    this.produtoService.atualizarProduto(this.produtoAtualizar.id, this.produtoAtualizar).subscribe();
    modal.close();
  }

  // Função que calcula a média dos valores unitários
  calcularMedia() {
    this.produtoService.calcularMedia().subscribe((media: number) => {
        this.mediaPreco = media;
    });
  }

  // * O endpoint de valor mais caro retorna uma lista para facilitar uma implementação
  // de ranking de valores mais caros, caso necessário

  // Função que busca o produto com Valor Total mais caro
  listarProdutoMaisCaro() {
  this.produtoService.listarProdutoMaisCaro().subscribe((produtos: Produto[]) => {
    this.listaProdutoMaisCaro = produtos;

    // Checa se existe registro na lista para preencher a string e exibir na tela
    // Se não existir, preenche com vazio.
    if (this.listaProdutoMaisCaro[0] != null) {
      this.stringProdutoMaisCaro = '' + this.listaProdutoMaisCaro[0].id + ' - ' + this.listaProdutoMaisCaro[0].nome
    } else {
      this.stringProdutoMaisCaro = '';
    }
  })}

  // Função para deletar um produto através do id. Chama o endpoint, e tendo sucesso na exclusão
  // (retornando true do endpoint, abre o o modal de mensagem de produto excluido com sucesso)
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
        // Atribui o Produto retornado (produtoRetornado) pelo id fornecido
        // ao produto que será atualizado e abre a tela de edição, passando o produto para editar
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
    // Lista todos os produtos na tela
    this.produtoService.listarProdutos().subscribe(data => {
      // Lista e ordena os produtos por ID, em ordem crescente (de 'a' para 'b', de '0' a '10', por exemplo)
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

  // função chamada ao mudar de valor na ComboBox de Promoção no ngModel
  selecionarPromocao(selecionouPromocao: boolean): boolean {
    if (selecionouPromocao) {
      return true;
    } else {
      return false;
    }
  }

  // função chamada quando troca o valor da ComboBox Frete Ativo para saber se o Produto tem frete ou não
  ativarFrete(ativouFrete: boolean): boolean {
    if (ativouFrete) {
      return true;
    } else {
      return false;
    }
  }

  // função chamada para calcular o valor do frete
  // Condição necessária para checar se o produto é Físico ou não, pois a regra do Frete só se aplica
  // para produtos tipo = físico. Se tipoFísico = true, calcula o frete. Caso contrário, frete = 0
  calcularFrete():number {
    let valorFrete: number;

    // CALCULO DE VALOR DE FRETE PELO CEP
    valorFrete = 0

    return valorFrete
  }

  // Calcula os totalizadores de valor. função chamada ao clicar no botão Calcular valores
  // e antes de gravar o produto no banco de dados no onSubmit do formulário ngModel
  calcularValores() {
    let valorFrete: number;

    // Desconto SIM e Frete SIM
    if (this.produtoAtualizar.promocao && this.produtoAtualizar.freteAtivo) {
      valorFrete = this.calcularFrete()
      this.produtoAtualizar.valorTotalDesc = this.produtoAtualizar.valor - (this.produtoAtualizar.valor * 0.1)
      this.produtoAtualizar.valorTotalFrete = this.produtoAtualizar.valorTotalDesc + valorFrete
      this.produtoAtualizar.frete = valorFrete;

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
      valorFrete = this.calcularFrete()
      this.produtoAtualizar.valorTotalFrete = this.produtoAtualizar.valor + valorFrete
      this.produtoAtualizar.valorTotalDesc = 0
      this.produtoAtualizar.frete = valorFrete;

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
