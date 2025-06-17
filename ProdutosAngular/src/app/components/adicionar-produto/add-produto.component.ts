import {Component, OnInit, ViewChild} from '@angular/core';
import { ProdutoService } from '../../service/produto/produto.service';
import {FormsModule} from '@angular/forms';
import {CurrencyPipe, NgForOf, NgIf, NgOptimizedImage, CommonModule} from '@angular/common';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import { ProdutoFunctionsService } from '../../service/produto/produto-functions.service';
import {PorcentagemMaskDirective} from '../../directives/porcentagem-mask.directive';
import {AuthService} from '../../service/auth/auth.service';
import {DeviceService} from '../../service/device/device.service';
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';
import {ProdutoDTO} from '../../model/dto/ProdutoDTO';

@Component({
  selector: 'app-add-produto',
  templateUrl: './add-produto.component.html',
  imports: [
    FormsModule,
    CurrencyPipe,
    NgOptimizedImage,
    PorcentagemMaskDirective,
    NgForOf,
    CommonModule
  ],
  styleUrls: ['./add-produto.component.css']
})

export class AddProdutoComponent implements OnInit {

  // É criado um produto zerado para poder ser acesso pelo ngModel, mas as propriedades são alteradas
  // pelos valores inseridos no formulário, então são passadas para o service da API.
  produto: ProdutoDTO = {
    id: 0,
    idUsuario: 0,
    nome: '',
    descricao: '',
    frete: 0,
    promocao: false,
    valorTotalDesc: 0,
    valorTotalFrete: 0,
    valor: 0,
    valorInicial: 0,
    quantia: 1,
    somaTotalValores: 0,
    freteAtivo: false,
    valorDesconto: 0,
    imagem: null,
    fornecedor: undefined
  };

  // Variáveis
  novoProduto!: ProdutoDTO;
  imagemFile: File = new File([], '', {})
  adicionouProduto: boolean = false
  fornecedores: FornecedorDTO[] = [];
  fornecedorSelecionado: FornecedorDTO | undefined = undefined;

  // Para calcular valor X quantia
  private valorInicialAnterior: number = 0;
  private quantiaAnterior: number = 1;

  // Services
  private modalService: NgbModal = new NgbModal();
  @ViewChild('modalMsgAddProduto') modalMsgAddProduto: any
  isMobileOrTablet: boolean = false;

  constructor(
    private produtoService: ProdutoService,
    private produtoFunctionsService: ProdutoFunctionsService,
    private authService: AuthService,
    private deviceService: DeviceService) {
  }

  ngOnInit() {
    this.produtoService.listarFornecedoresList(this.authService.getUsuarioLogado().idUsuario).subscribe(data => {
      this.fornecedores = data;
      // Garante que o fornecedor seja undefined inicialmente
      this.produto.fornecedor = undefined;
      this.fornecedorSelecionado = undefined;
    });
    this.adicionouProduto = false;
    this.produtoService.acessarPaginaCadastro().subscribe();
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });
  }

  // Função que é chamada ao clicar no botão Submit do formulário HTML (ngModel) ao criar um produto
  onSubmit() {
    this.calcularValores(this.produto);

    this.produto.idUsuario = this.authService.getUsuarioLogado().idUsuario

    // Adiciona o produto no banco depois chama a mensagem de sucesso de adição de produto "msgAddProduto"
    this.produtoService.adicionarProduto(this.produto, this.imagemFile).subscribe({
      next: (produtoAdicionado: ProdutoDTO) => {
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
    if (this.adicionouProduto && this.novoProduto && modalMsgAddProduto) {
      setTimeout(() => {
        try {
          const modalRef = this.modalService.open(modalMsgAddProduto, {size: 'lg'});
          if (modalRef && modalRef.componentInstance) {
            modalRef.componentInstance.produto = this.novoProduto;
          }
        } catch (error) {
          console.error('Erro ao abrir modal:', error);
        }
      }, 100);
    }
  }

  // Faz os calculos gerais após calcular valor X quantia
  calcularValores(produto: ProdutoDTO) {
    this.calcularValorXQuantia(produto).then(() => {(
      this.produtoFunctionsService.calcularValores(produto));
    });
  }

  // Faz o cálculo dos valores em relação a quantidade
  async calcularValorXQuantia(produto: ProdutoDTO): Promise<void> {
    // Inicializa valorUnitario na primeira vez se não existir
    if (!produto.valorInicial) {
      produto.valorInicial = produto.valor;
    }

    // Só calcula se a quantidade ou o valor unitário mudaram
    if (produto.valorInicial !== this.valorInicialAnterior ||
      produto.quantia !== this.quantiaAnterior) {

      produto.valor = produto.quantia * produto.valorInicial;

      // Armazena os valores atuais para comparação futura
      this.valorInicialAnterior = produto.valorInicial;
      this.quantiaAnterior = produto.quantia;

      return Promise.resolve();
    }
  }

  onFileChange(event: any) {
    this.imagemFile = event.target.files[0];
  }

  // Função que é chamada quando o fornecedor é selecionado
  onFornecedorChange(fornecedor: FornecedorDTO | undefined) {
    this.fornecedorSelecionado = fornecedor;
    this.produto.fornecedor = fornecedor;
  }
}
