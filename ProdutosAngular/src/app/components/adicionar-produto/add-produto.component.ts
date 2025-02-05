import {Component, OnInit, ViewChild} from '@angular/core';
import { ProdutoService } from '../../service/produto/produto.service';
import {FormsModule} from '@angular/forms';
import {Produto} from '../../model/produto';
import {CurrencyPipe, NgIf, NgOptimizedImage} from '@angular/common';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import { ProdutoFunctionsService } from '../../service/produto/produto-functions.service';
import {PorcentagemMaskDirective} from '../../directives/porcentagem-mask.directive';
import {AuthService} from '../../service/auth/auth.service';

@Component({
  selector: 'app-add-produto',
  templateUrl: './add-produto.component.html',
  imports: [
    FormsModule,
    CurrencyPipe,
    NgOptimizedImage,
    PorcentagemMaskDirective
  ],
  styleUrls: ['./add-produto.component.css']
})

export class AddProdutoComponent implements OnInit {

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
    valorDesconto: 0,
    imagem: ''
  };

  // Variáveis
  novoProduto!: Produto;
  imagemFile: File = new File([], 'arquivo_vazio.txt', {})
  adicionouProduto: boolean = false;

  // Services
  private modalService: NgbModal = new NgbModal();
  @ViewChild('modalMsgAddProduto') modalMsgAddProduto: any

  constructor(
    private produtoService: ProdutoService,
    private produtoFunctionsService: ProdutoFunctionsService) {
  }

  ngOnInit() {
    this.adicionouProduto = false;
    this.produtoService.acessarPaginaCadastro().subscribe();
  }

  // Função que é chamada ao clicar no botão Submit do formulário HTML (ngModel) ao criar um produto
  onSubmit() {
    this.calcularValores(this.produto);

    // Adiciona o produto no banco depois chama a mensagem de sucesso de adição de produto "msgAddProduto"
    this.produtoService.adicionarProduto(this.produto, this.imagemFile).subscribe({
      next: (produtoAdicionado: Produto) => {
        this.novoProduto = produtoAdicionado
        this.adicionouProduto = true;
        this.msgAddProduto(this.modalMsgAddProduto)
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

  // Função que abre o modal de mensagem de sucesso após cadastrar um produto
  msgAddProduto(modalMsgAddProduto: any) {
    if (this.adicionouProduto) {
      setTimeout(() => {
        const modalRef = this.modalService.open(modalMsgAddProduto);
        modalRef.componentInstance.produto = this.novoProduto;
      }, 100);
    }
  }

  // Calcula os totalizadores de valor. Função chamada ao clicar no botão Calcular valores
  // e antes de gravar o produto no banco de dados.
  calcularValores(produto: Produto) {
    this.produtoFunctionsService.calcularValores(produto);
  }

  onFileChange(event: any) {
    this.imagemFile = event.target.files[0];
  }
}
