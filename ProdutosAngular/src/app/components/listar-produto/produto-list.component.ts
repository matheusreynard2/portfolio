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
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';
import {Fornecedor} from '../../model/fornecedor';

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
  fornecedores: FornecedorDTO[] = []; // Lista de fornecedores
  fornecedorSelecionado: FornecedorDTO | undefined = undefined; // Fornecedor selecionado

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
    // Carrega a lista de fornecedores
    this.produtoService.listarFornecedoresList(this.authService.getUsuarioLogado().idUsuario).subscribe(data => {
      this.fornecedores = data;
    });

    this.produtoService.listarProdutos(this.currentPage, this.pageSize).subscribe(data => {
      const usuarioLogadoId = this.authService.getUsuarioLogado().idUsuario;
      this.listaDeProdutos = data.content.filter(produto => produto.idUsuario === usuarioLogadoId); // Filtra os produtos do usuário logado
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
    });

    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });

    this.listarProdutoMaisCaro(this.authService.getUsuarioLogado().idUsuario);
    this.calcularMedia(this.authService.getUsuarioLogado().idUsuario);
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
    // Primeiro busca o produto completo por ID
    this.produtoService.buscarProdutoPorId(id).subscribe({
      next: (produtoCompleto: ProdutoDTO) => {
        this.produtoAtualizar = produtoCompleto;
        
        // Procura o fornecedor na lista de fornecedores
        if (produtoCompleto.fornecedor) {
          const fornecedorEncontrado = this.fornecedores.find(
            fornecedor => fornecedor.id === produtoCompleto.fornecedor?.id
          );
          
          if (fornecedorEncontrado) {
            this.fornecedorSelecionado = fornecedorEncontrado;
            this.produtoAtualizar.fornecedor = fornecedorEncontrado;
          }
        }
        
        this.calcularValores(this.produtoAtualizar);
        this.abrirTelaEdicao(janelaEditar);
      },
      error: (error) => {
        console.error('Erro ao buscar produto:', error);
        // Em caso de erro, usa o produto da lista como fallback
        this.produtoAtualizar = produto;
        
        // Procura o fornecedor na lista de fornecedores mesmo no caso de erro
        if (produto.fornecedor) {
          const fornecedorEncontrado = this.fornecedores.find(
            fornecedor => fornecedor.id === produto.fornecedor?.id
          );
          
          if (fornecedorEncontrado) {
            this.fornecedorSelecionado = fornecedorEncontrado;
            this.produtoAtualizar.fornecedor = fornecedorEncontrado;
          }
        }
        
        this.calcularValores(this.produtoAtualizar);
        this.abrirTelaEdicao(janelaEditar);
      }
    });
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
      const usuarioLogadoId = this.authService.getUsuarioLogado().idUsuario;
      this.listaDeProdutos = data.content.filter(produto => produto.idUsuario === usuarioLogadoId); // Filtra os produtos do usuário logado
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
    });
    this.listarProdutoMaisCaro(this.authService.getUsuarioLogado().idUsuario);
    this.calcularMedia(this.authService.getUsuarioLogado().idUsuario);
  }

  trocarPagina(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.atualizarLista();
  }

  // Função que abre o modal - Janela de edição de produto
  abrirTelaEdicao(janelaEditar: any) {
    this.modalService.open(janelaEditar, { 
      size: 'xl', 
      backdrop: 'static',
      windowClass: 'modal-xl',
      centered: true
    });
  }

  // Função que abre o modal - Janela de exclusão de produto
  abrirTelaExclusao(modalExcluir: any) {
    this.modalService.open(modalExcluir);
    this.listarProdutoMaisCaro(this.authService.getUsuarioLogado().idUsuario);
    this.calcularMedia(this.authService.getUsuarioLogado().idUsuario);
    this.atualizarLista();
  }

  criarFornecedor() {
    // Garante que o fornecedor seja enviado corretamente
    if (this.fornecedorSelecionado) {
      // Cria uma cópia do fornecedor sem a referência circular
      const fornecedorParaEnviar: FornecedorDTO = {
        id: this.fornecedorSelecionado.id,
        idUsuario: this.authService.getUsuarioLogado().idUsuario,
        nome: this.fornecedorSelecionado.nome,
        nrResidencia: this.fornecedorSelecionado.nrResidencia,
        enderecoFornecedor: this.fornecedorSelecionado.enderecoFornecedor,
        produtos: [] // Array vazio para evitar referência circular
      };
      this.produtoAtualizar.fornecedor = fornecedorParaEnviar;
    }
  }

  // Função chamada ao clicar no botão de Submit (Salvar) do formulário de Edição de produtos
  onSubmitSalvar(modal: any) {
    this.calcularValores(this.produtoAtualizar);
    
   this.criarFornecedor();

    // CHAMA O ATUALIZAR PRODUTO
    this.produtoService.atualizarProduto(this.produtoAtualizar.id, this.produtoAtualizar, this.imagemFile).subscribe({
      next: (produtoAtualizado) => {
        console.log('Produto atualizado com sucesso:', produtoAtualizado);
        modal.close();
        this.atualizarLista(); // Atualiza a lista após salvar
      },
      error: (error) => {
        console.error('Erro ao atualizar produto:', error);
        // Aqui você pode adicionar uma mensagem de erro para o usuário
      }
    });
  }

// Função chamada ao clicar no botão Pesquisar
  efetuarPesquisa() {
    let searchBar_value = this.searchBar.nativeElement.value;
    const usuarioLogadoId = this.authService.getUsuarioLogado().idUsuario;

    this.produtoService.efetuarPesquisa(this.tipoPesquisaSelecionado, searchBar_value, this.authService.getUsuarioLogado().idUsuario).subscribe(data => {
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

  // Função que é chamada quando o fornecedor é selecionado
  onFornecedorChange(fornecedor: FornecedorDTO | undefined) {
    this.fornecedorSelecionado = fornecedor;
    if (this.produtoAtualizar) {
      this.produtoAtualizar.fornecedor = fornecedor;
    }
  }

  protected readonly alert = alert;
}
