import { Component } from '@angular/core';
import { ProdutoService } from '../../service/produto.service';
import {FormsModule} from '@angular/forms';
import {Produto} from '../../model/produto';
import {CurrencyPipe, NgIf, NgOptimizedImage} from '@angular/common';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-add-produto',
  templateUrl: './add-produto.component.html',
  imports: [
    FormsModule,
    CurrencyPipe,
    NgOptimizedImage,
    NgIf
  ],
  styleUrls: ['./add-produto.component.css']
})

export class AddProdutoComponent {

  // É criado um produto zerado para poder ser acesso pelo ngModel, mas as propriedades são alteradas
  // pelos valores inseridos no formulário, então são passadas para o service da API.
  produto: Produto = {
    id: 0,
    nome: '',
    descricao: '',
    frete: 0,
    promocao: false,
    valorTotalDesc: 0,
    valorTotalFrete: 0,
    valor: 0,
    quantia: 1,
    somaTotalValores: 0,
    freteAtivo: true
  };

  // Essa varíavel é utilizada para passar o produto para o modal que exibe
  // a mensagem de "sucesso" para quando um novo produto é cadastrado
  novoProduto!: Produto;

  private modalService: NgbModal = new NgbModal();

  constructor(
    private produtoService: ProdutoService) {
  }

  // Função que é chamada ao clicar no botão Submit do formulário HTML (ngModel) ao criar um produto
  onSubmit() {
    // Chama a função de calcular os totalizadores de valor antes de adicionar o produto no banco de dados
    this.calcularValores();
    // Chama a função do service que adiciona o produto no banco de dados
    this.produtoService.adicionarProduto(this.produto).subscribe({
      next: (produtoAdicionado: Produto) => {
        // Atribui o Produto retornado (produtoAdicionado) ao novoProduto
        this.novoProduto = produtoAdicionado
      }
    })
  }

  // Função chamada ao mudar de valor na ComboBox de Promoção no ngModel
  selecionarPromocao(selecionouPromocao: boolean): boolean {
    if (selecionouPromocao) {
      return true;
    } else {
      return false;
    }
  }

  // Função chamada quando troca o valor da ComboBox Frete para saber se o Produto tem frete ou não
  ativarFrete(ativouFrete: boolean): boolean {
    if (ativouFrete) {
      return true;
    } else {
      return false;
    }
  }

  // Função que abre o modal de mensagem de sucesso após cadastrar um produto
  msgAddProduto(modalMsg: any) {
    const modalRef = this.modalService.open(modalMsg);
    // Espera 0.3 segundos para setar o produto no modal por que ele chama no arquivo HTML o submit e o click
    // ao mesmo tempo. Então, primeiramente ele chama o submit, grava no banco, depois de 0.3 segundos chama o modal.
    // Pois se chamar os dois eventos ao mesmo tempo, gera conflito, e o submit não consegue gravar no banco de dados.
    setTimeout(() => {
      modalRef.componentInstance.produto = this.novoProduto;
    }, 300);
  }

  // Função chamada para calcular o valor do frete
  calcularFrete():number {
    let valorFrete: number;

    // AQUI VAI COLOCAR O VALOR DO FRETE E ACORDO COM O CÁLCULO DO CEP
    valorFrete = 0

    return valorFrete
  }

  // Calcula os totalizadores de valor. Função chamada ao clicar no botão Calcular valores
  // e antes de gravar o produto no banco de dados no onSubmit do formulário ngModel
  calcularValores() {
    let valorFrete: number;

    // Desconto SIM e Frete SIM
    if (this.produto.promocao && this.produto.freteAtivo) {
      valorFrete = this.calcularFrete()
      this.produto.valorTotalDesc = this.produto.valor - (this.produto.valor * 0.1)
      this.produto.valorTotalFrete = this.produto.valorTotalDesc + valorFrete
      this.produto.frete = valorFrete;

      this.produto.somaTotalValores = this.produto.valorTotalDesc + this.produto.frete
    }

    // Desconto SIM e Frete NÃO
    if (this.produto.promocao && !this.produto.freteAtivo) {
      this.produto.frete = 0
      this.produto.valorTotalFrete = 0
      this.produto.valorTotalDesc = this.produto.valor - (this.produto.valor * 0.1)

      this.produto.somaTotalValores = this.produto.valorTotalDesc
    }

    // Desconto NÃO e Frete SIM
    if (!this.produto.promocao && this.produto.freteAtivo) {
      valorFrete = this.calcularFrete()
      this.produto.valorTotalFrete = this.produto.valor + valorFrete
      this.produto.valorTotalDesc = 0
      this.produto.frete = valorFrete;

      this.produto.somaTotalValores = this.produto.valorTotalFrete
    }

    // Desconto NÃO e Frete NÃO
    if (!this.produto.promocao && !this.produto.freteAtivo) {
      this.produto.frete = 0
      this.produto.valorTotalFrete = 0
      this.produto.valorTotalDesc = 0

      this.produto.somaTotalValores = this.produto.valor
    }

  }
}
