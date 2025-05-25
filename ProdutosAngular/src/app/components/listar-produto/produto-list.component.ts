import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { Produto } from '../../model/produto';
import { ProdutoService } from '../../service/produto/produto.service';
import {CurrencyPipe, NgForOf, NgIf, NgOptimizedImage} from '@angular/common';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { ProdutoFunctionsService } from '../../service/produto/produto-functions.service';
import {PorcentagemMaskDirective} from '../../directives/porcentagem-mask.directive';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {AuthService} from '../../service/auth/auth.service';
import {HttpResponse} from '@angular/common/http';
import {DeviceService} from '../../service/device/device.service';
import {ProdutoDTO} from '../../model/dto/ProdutoDTO';

@Component({
  selector: 'app-produto-list',  // Corrigido para 'app-produto-list', sem a barra inicial
  templateUrl: './produto-list.component.html',
  styleUrls: ['./produto-list.component.css'],
  imports: [
    NgForOf,
    NgbModule,
    NgOptimizedImage,
    FormsModule,
    ReactiveFormsModule,
    CurrencyPipe,
    PorcentagemMaskDirective,
    NgIf,
    NgxPaginationModule,
    MatPaginatorModule
  ]
})

export class ProdutoListComponent implements OnInit {

  // Variáveis
  listaProdutoMaisCaro: Produto[] = [];
  stringProdutoMaisCaro: string = '';
  mediaPreco: number = 0;
  tipoPesquisaSelecionado: string = 'id';
  popUpVisivel = false;  // Controle de visibilidade do pop-up
  imgBase64: string = ''  // Variável para armazenar a imagem do produto

  listaDeProdutos!: ProdutoDTO[];
  produtoAtualizar!: ProdutoDTO;
  produtoExcluido!: ProdutoDTO;
  imagemFile: File = new File([], 'arquivo_vazio.txt', {})

  // Variáveis de paginação
  totalRecords: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;

  // Para calcular valor X quantia
  private valorInicialAnterior: number = 0;
  private quantiaAnterior: number = 1;

  private modalService: NgbModal = new NgbModal();
  @ViewChild('searchBar') searchBar!: ElementRef
  @ViewChild('modalAvisoToken') modalAvisoToken!: ElementRef
  isMobileOrTablet: boolean = false;

  constructor(private produtoService: ProdutoService,
              private produtoFunctionsService: ProdutoFunctionsService,
              private authService: AuthService, private deviceService: DeviceService) { }

  ngOnInit() {

    this.produtoService.listarProdutos(this.currentPage, this.pageSize).subscribe(data => {
      const usuarioLogadoId = this.authService.getUsuarioLogado().id;
      this.listaDeProdutos = data.content.filter(produto => produto.idUsuario === usuarioLogadoId); // Filtra os produtos do usuário logado
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
    });

    console.log(this.listaDeProdutos)

    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });

    this.listarProdutoMaisCaro(this.authService.getUsuarioLogado().id);
    this.calcularMedia(this.authService.getUsuarioLogado().id);
  }

  // Função que calcula a média dos valores unitários
  calcularMedia(idUsuario: number) {
    this.produtoService.calcularMedia(idUsuario).subscribe((media: number) => {
      this.mediaPreco = media;
    });
  }

  // Função que busca o produto com Valor Total mais caro
  listarProdutoMaisCaro(idUsuario: number) {
  this.produtoService.listarProdutoMaisCaro(idUsuario).subscribe((produtos: Produto[]) => {
    this.listaProdutoMaisCaro = produtos;

    // Checa se existe registro na lista para preencher a string e exibir na tela
    if (this.listaProdutoMaisCaro[0] != null) {
      this.stringProdutoMaisCaro = '' + this.listaProdutoMaisCaro[0].id + ' - ' + this.listaProdutoMaisCaro[0].nome
    } else {
      this.stringProdutoMaisCaro = '';
    }
  })}

  // Função para deletar um produto através do id. Chama o endpoint, e a msg de sucesso
  deletarProduto(modalDeletar: any, id: number, produto: ProdutoDTO) {
    this.produtoService.deletarProduto(id).subscribe({
      next: (response) => {
        if (response.status === 200) {
          this.produtoExcluido = produto;
          this.abrirTelaExclusao(modalDeletar);
        }
      }
    });
    this.atualizarLista();
  }

  // Função para atualizar um produto através do id
  atualizarProduto(janelaEditar: any, id: number, produto: ProdutoDTO) {
    this.produtoService.atualizarProduto(id, produto, this.imagemFile).subscribe({
      next: (produtoRetornado: ProdutoDTO) => {
        this.produtoAtualizar = produtoRetornado
        this.calcularValores(this.produtoAtualizar);
        this.abrirTelaEdicao(janelaEditar)
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
    this.produtoService.listarProdutos(this.currentPage, this.pageSize).subscribe(data => {
      const usuarioLogadoId = this.authService.getUsuarioLogado().id;
      this.listaDeProdutos = data.content.filter(produto => produto.idUsuario === usuarioLogadoId); // Filtra os produtos do usuário logado
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
    });
    this.listarProdutoMaisCaro(this.authService.getUsuarioLogado().id);
    this.calcularMedia(this.authService.getUsuarioLogado().id);
  }

  trocarPagina(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.atualizarLista();
  }

  // Função que abre o modal - Janela de edição de produto
  abrirTelaEdicao(janelaEditar: any) {
    this.modalService.open(janelaEditar, { size: 'lg', backdrop: 'static' });
  }

  // Função que abre o modal - Janela de exclusão de produto
  abrirTelaExclusao(modalExcluir: any) {
    this.modalService.open(modalExcluir);
    this.listarProdutoMaisCaro(this.authService.getUsuarioLogado().id);
    this.calcularMedia(this.authService.getUsuarioLogado().id);
    this.atualizarLista();
  }

  // Função chamada ao clicar no botão de Submit (Salvar) do formulário de Edição de produtos
  onSubmitSalvar(modal: any) {
    this.calcularValores(this.produtoAtualizar);
    this.produtoService.atualizarProduto(this.produtoAtualizar.id, this.produtoAtualizar, this.imagemFile).subscribe();
    modal.close();
  }

// Função chamada ao clicar no botão Pesquisar
  efetuarPesquisa() {
    let searchBar_value = this.searchBar.nativeElement.value;
    const usuarioLogadoId = this.authService.getUsuarioLogado().id;

    this.produtoService.efetuarPesquisa(this.tipoPesquisaSelecionado, searchBar_value, this.authService.getUsuarioLogado().id).subscribe(data => {
      this.listaDeProdutos = data
        .filter(produto => produto.idUsuario === usuarioLogadoId) // Filtra os produtos pelo usuário logado
        .sort((a, b) => a.id - b.id); // Ordena os produtos pelo ID
    });

    this.listarProdutoMaisCaro(usuarioLogadoId);
    this.calcularMedia(usuarioLogadoId);
  }

  // Faz os calculos gerais após calcular valor X quantia
  calcularValores(produto: ProdutoDTO) {
    this.calcularValorXQuantia(produto).then(() => {(
      this.produtoFunctionsService.calcularValores(produto));
    });
  }

  // Função chamada ao mudar de valor na ComboBox de Promoção no ngModel
  selecionarPromocao(selecionouPromocao: boolean): boolean {
    return this.produtoFunctionsService.selecionarPromocao(selecionouPromocao);
  }

  // Função chamada quando troca o valor da ComboBox Frete Ativo para saber se o Produto tem frete ou não
  ativarFrete(ativouFrete: boolean): boolean {
    return this.produtoFunctionsService.ativarFrete(ativouFrete);
  }

  // Função que abre o modal - Janel de Aviso para Atualizar List de Produtos
  abrirTelaAviso(modalAviso: any) {
    this.modalService.open(modalAviso);
  }

  // Função chamada ao mudar de valor na ComboBox Tipo de Pesquisa
  trocarTipoPesquisa() {
    if (this.tipoPesquisaSelecionado =='id' || this.tipoPesquisaSelecionado =='nome') {
      this.tipoPesquisaSelecionado ='nome'
    } else {
      this.tipoPesquisaSelecionado ='id'
    }
  }

  // Método chamado quando o mouse entra no elemento para abrir pop-up de mostrar imagem
  mostrarImgPopup(produto: any) {
    this.imgBase64 = produto.imagem;  // Armazenar a imagem do produto
    this.popUpVisivel = true;        // Mostrar o pop-up
  }

  // Método chamado quando o mouse sai do elemento para abrir pop-up de mostrar imagem
  esconderImgPopup() {
    this.imgBase64 = '';  // Limpar a imagem do produto
    this.popUpVisivel = false;  // Esconder o pop-up
  }

  onFileChange(event: any) {
    this.imagemFile = event.target.files[0];
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

  protected readonly alert = alert;
}
