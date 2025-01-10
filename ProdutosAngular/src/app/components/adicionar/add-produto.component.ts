import { Component } from '@angular/core';
import { ProdutoService } from '../../service/produto.service';
import {FormsModule} from '@angular/forms';
import {Produto} from '../../model/produto';
import {CurrencyPipe, NgIf, NgOptimizedImage} from '@angular/common';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import { ProdutoFunctionsService } from '../../service/produto-functions.service';

@Component({
  selector: 'app-add-produto',
  templateUrl: './add-produto.component.html',
  imports: [
    FormsModule,
    CurrencyPipe,
    NgOptimizedImage,
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
    freteAtivo: false,
    desconto: 0
  };

  // Variáveis
  novoProduto!: Produto;

  // Services
  private produtoFunctionsService: ProdutoFunctionsService = new ProdutoFunctionsService();
  private modalService: NgbModal = new NgbModal();

  constructor(
    private produtoService: ProdutoService) {
  }

  // Função que é chamada ao clicar no botão Submit do formulário HTML (ngModel) ao criar um produto
  onSubmit() {
    this.calcularValores();
    this.produtoService.adicionarProduto(this.produto).subscribe({
      next: (produtoAdicionado: Produto) => {
        this.novoProduto = produtoAdicionado
      }
    })
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
  calcularFrete(): number {
    return this.produtoFunctionsService.calcularFrete();
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

  // Calcula os totalizadores de valor. Função chamada ao clicar no botão Calcular valores
  // , antse de gravar produto no banco de dados.
  calcularValores() {
    // Desconto SIM e Frete SIM
    if (this.produto.promocao && this.produto.freteAtivo) {
      this.produto.valorTotalDesc = this.produto.valor - (this.produto.valor * 0.1)
      this.produto.valorTotalFrete = this.produto.valorTotalDesc + this.calcularFrete()
      this.produto.frete = this.calcularFrete()

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
      this.produto.valorTotalFrete = this.produto.valor + this.calcularFrete()
      this.produto.valorTotalDesc = 0
      this.produto.frete = this.calcularFrete()

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
