import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { ProdutoService } from '../../service/produto/produto.service';
import {CommonModule, CurrencyPipe, NgForOf, NgIf, NgOptimizedImage} from '@angular/common';
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
import { UtilsService } from '../../service/utils/utils.service';
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';

@Component({
  selector: 'app-produto-list', 
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
    MatPaginatorModule,
    CommonModule
  ]
})

export class ProdutoListComponent implements OnInit {

  // Variáveis
  produtoMaisCaro!: ProdutoDTO;
  stringProdutoMaisCaro: string = '';
  mediaPreco: number = 0;
  tipoPesquisaSelecionado: string = 'nome';
  popUpVisivel = false;  // Controle de visibilidade do pop-up
  imgBase64: string = ''  // Variável para armazenar a imagem do produto

  listaDeProdutos!: ProdutoDTO[];
  private listaDeProdutosOriginal: ProdutoDTO[] = [];
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
  @ViewChild('modalErroExcluirHistorico') modalErroExcluirHistorico!: ElementRef
  @ViewChild('modalAvisoArquivo') modalAvisoArquivo!: ElementRef
  isMobileOrTablet: boolean = false;
  searchValue: string = '';
  searchNomeFornecedor: string = '';
  searchValorInicial: number | null = null;
  avisoTextoModal: string = '';

  constructor(private produtoService: ProdutoService,
              private produtoFunctionsService: ProdutoFunctionsService,
              private authService: AuthService, private deviceService: DeviceService,
              private utils: UtilsService) { }

  ngOnInit() {
    // Carrega a lista de fornecedores
    this.produtoService.listarFornecedoresList(this.authService.getUsuarioLogado().idUsuario).subscribe(data => {
      this.fornecedores = data;
    });

    this.produtoService.listarProdutos(this.currentPage, this.pageSize, this.authService.getUsuarioLogado().idUsuario).subscribe(data => {
      this.listaDeProdutos = data.content;
      this.listaDeProdutosOriginal = data.content;
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
  this.produtoService.listarProdutoMaisCaro(idUsuario).subscribe((produto: ProdutoDTO) => {
    this.produtoMaisCaro = produto;

    // Checa se existe registro no produtoMaisCaro
    if (this.produtoMaisCaro != null) {
      this.stringProdutoMaisCaro = '' + this.produtoMaisCaro.id + ' - ' + this.produtoMaisCaro.nome
    } else {
      this.stringProdutoMaisCaro = '';
    }
  })}

  // Função para deletar um produto através do id. Chama o endpoint, e a msg de sucesso
  deletarProduto(modalDeletar: any, id: number, produto: ProdutoDTO) {
    this.produtoService.deletarProduto(id).subscribe({
      next: (response: HttpResponse<boolean>) => {
        if (response.status === 200) {
          this.produtoExcluido = produto;
          this.abrirTelaExclusao(modalDeletar);
          this.atualizarLista();
        }
      },
      error: (err) => {
        const msg: string = (err?.error as string) || '';
        if (msg.includes('Produto possui histórico de venda relacionado')) {
          // Exibe modal amigável
          this.produtoExcluido = produto;
          this.modalService.open(this.modalErroExcluirHistorico);
        }
      }
    });
  }

  // Função para atualizar um produto através do id
  atualizarProduto(janelaEditar: any, id: number, produto: ProdutoDTO) {
    this.produtoService.buscarProdutoPorId(id, this.authService.getUsuarioLogado().idUsuario).subscribe({
      next: (produto) => {
        this.produtoAtualizar = produto;
        
        // Procura o fornecedor na lista de fornecedores
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
      },
      error: () => {
        // Em caso de erro, limpa o produto selecionado e fecha o modal
        this.produtoAtualizar = {} as ProdutoDTO;
        this.modalService.dismissAll();
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
    this.produtoService.listarProdutos(this.currentPage, this.pageSize, this.authService.getUsuarioLogado().idUsuario).subscribe(data => {
      this.listaDeProdutos = data.content;
      this.listaDeProdutosOriginal = data.content;
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
      this.aplicarFiltroLocal();
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
        enderecoFornecedor: this.fornecedorSelecionado.enderecoFornecedor,
        produtos: [],
        dadosEmpresa: this.fornecedorSelecionado.dadosEmpresa // Array vazio para evitar referência circular
      };
      this.produtoAtualizar.fornecedor = fornecedorParaEnviar;
    }
  }

  // Função chamada ao clicar no botão de Submit (Salvar) do formulário de Edição de produtos
  async onSubmitSalvar(modal: any) {
    this.criarFornecedor();

    // Limpa o campo valorDesconto antes de enviar
    if (this.produtoAtualizar.valorDesconto !== undefined && this.produtoAtualizar.valorDesconto !== null) {
      const cleaned = String(this.produtoAtualizar.valorDesconto).replace('%', '');
      this.produtoAtualizar.valorDesconto = Number(cleaned);
    }

    // Aguarda o cálculo dos valores antes de enviar ao backend
    await this.calcularValores(this.produtoAtualizar);

    // Garantir que o ID existe para atualização
    const produtoId = this.produtoAtualizar.id;
    if (produtoId === null || produtoId === undefined) {
      console.error('Erro: ID do produto é obrigatório para atualização');
      return;
    }

    this.produtoService.atualizarProduto(produtoId, this.produtoAtualizar, this.imagemFile).subscribe({
      next: (produtoAtualizado) => {
        modal.close();
        this.atualizarLista(); // Atualiza a lista após salvar
      },
      error: (error) => {
        console.error('Erro ao atualizar produto:', error);
        // Aqui você pode adicionar uma mensagem de erro para o usuário
      }
    });
  }

  calcularValores(produto: ProdutoDTO): Promise<void> {
    return this.calcularValorXQuantia(produto).then(() => {
      return this.produtoFunctionsService.calcularValores(produto);
    });
  }

  // Pesquisa local (dinâmica) baseada nos 3 campos da barra de pesquisa
  efetuarPesquisa() {
    this.aplicarFiltroLocal();
  }

  private aplicarFiltroLocal(): void {
    const nomeTerm = (this.searchValue || '').toString().trim().toLowerCase();
    const fornecedorTerm = (this.searchNomeFornecedor || '').toString().trim().toLowerCase();
    const valorFiltro = this.searchValorInicial;

    const origem = this.listaDeProdutosOriginal || [];
    const filtrado = origem.filter(p => {
      const okNome = nomeTerm ? (p.nome || '').toLowerCase().includes(nomeTerm) : true;
      const okFornecedor = fornecedorTerm ? ((p.fornecedor?.nome || '').toLowerCase().includes(fornecedorTerm)) : true;
      const okValor = (valorFiltro !== null && valorFiltro !== undefined && valorFiltro !== '' as any)
        ? Number(p.valorInicial) === Number(valorFiltro)
        : true;
      return okNome && okFornecedor && okValor;
    });

    this.listaDeProdutos = filtrado;
    this.totalRecords = filtrado.length;
  }

  // Dispara filtro automaticamente conforme o usuário digita (como no PDV)
  onSearchChanged(): void {
    this.aplicarFiltroLocal();
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
  abrirTelaAviso(modalSalvouProduto: any) {
    this.modalService.open(modalSalvouProduto, {size: 'sm'});
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
    const file: File = event.target.files && event.target.files[0];
    if (!file) return;
    const valid = this.utils.validarImagem(file);
    if (!valid.ok) {
      const texto = valid.motivo === 'tipo'
        ? 'Tipo de arquivo inválido. Selecione apenas .jpg ou .png.'
        : 'Arquivo muito grande. Tamanho máximo permitido: 20MB.';
      this.showSmallModal(texto);
      try { event.target.value = null; } catch {}
      this.imagemFile = new File([], 'arquivo_vazio.txt', {});
      return;
    }
    this.imagemFile = file;
  }

  private showSmallModal(texto: string) {
    this.avisoTextoModal = texto;
    try {
      this.modalService.open(this.modalAvisoArquivo, { size: 'sm' });
    } catch {
      alert(texto);
    }
  }

  // Faz o cálculo dos valores em relação a quantidade
  async calcularValorXQuantia(produto: ProdutoDTO): Promise<void> {
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

  // Atualiza valorInicial dinamicamente ao digitar na edição
  onValorInicialChangeAtualizar(novoValor: number) {
    if (this.produtoAtualizar) {
      this.produtoAtualizar.valorInicial = novoValor;
    }
  }

  protected readonly alert = alert;
}
