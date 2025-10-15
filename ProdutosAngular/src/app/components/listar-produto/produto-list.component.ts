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
import { catchError, finalize, tap } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import {RelatoriosService, ExportarRelatorioPayload} from '../../service/relatorios/relatorios.service';

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
  popUpVisivel = false; 
  imgBase64: string = ''  

  listaDeProdutos!: ProdutoDTO[];
  private listaDeProdutosOriginal: ProdutoDTO[] = [];
  sortColumn: 'checkbox' | 'id' | 'nome' | 'fornecedor' | 'descricao' | 'quantia' | 'valorInicial' | 'promocao' | 'valorTotalDesc' | 'somaTotalValores' = 'id';
  sortDirection: 'asc' | 'desc' = 'asc';
  private cacheSortSignature = '';
  private cacheListaOrdenada: ProdutoDTO[] = [];
  isLoadingProdutos: boolean = false;
  produtoAtualizar!: ProdutoDTO;
  produtoExcluido!: ProdutoDTO;
  imagemFile: File = new File([], 'arquivo_vazio.txt', {})
  fornecedores: FornecedorDTO[] = []; // Lista de fornecedores
  fornecedorSelecionado: FornecedorDTO | undefined = undefined; // Fornecedor selecionado

  // Variáveis de paginação
  totalRecords: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  // Seleção múltipla
  selecionadosProdutoIds: Set<number> = new Set<number>();

  // Para calcular valor X quantia
  private valorInicialAnterior: number = 0;
  private quantiaAnterior: number = 1;

  private modalService: NgbModal = new NgbModal();
  @ViewChild('searchBar') searchBar!: ElementRef
  @ViewChild('modalAvisoToken') modalAvisoToken!: ElementRef
  @ViewChild('modalErroExcluirHistorico') modalErroExcluirHistorico!: ElementRef
  @ViewChild('modalAvisoArquivo') modalAvisoArquivo!: ElementRef
  @ViewChild('modalConfirmDeleteMultiProdutos') modalConfirmDeleteMultiProdutos!: ElementRef
  @ViewChild('modalConfirmDeleteProduto') modalConfirmDeleteProduto!: ElementRef
  @ViewChild('modalMsgExcluir') modalMsgExcluir!: ElementRef
  isMobileOrTablet: boolean = false;
  searchValue: string = '';
  searchNomeFornecedor: string = '';
  searchValorInicial: number | null = null;
  avisoTextoModal: string = '';
  tipoHistoricoModal: 'VENDA' | 'COMPRA' = 'VENDA';
  produtosRelacionadosErro: { id: number, nome: string }[] = [];
  private idNomeCache = new Map<number, string>();

  constructor(private produtoService: ProdutoService,
              private produtoFunctionsService: ProdutoFunctionsService,
              private authService: AuthService, private deviceService: DeviceService,
              private utils: UtilsService,
              private relatoriosService: RelatoriosService) { }

  ngOnInit() {
    // Carrega a lista de fornecedores
    this.produtoService.listarFornecedoresList(this.authService.getUsuarioLogado().idUsuario).subscribe(data => {
      this.fornecedores = data;
    });

    this.isLoadingProdutos = true;
    this.produtoService.listarProdutos(this.currentPage, this.pageSize, this.authService.getUsuarioLogado().idUsuario).subscribe({
      next: (data) => {
        this.listaDeProdutos = data.content;
        this.listaDeProdutosOriginal = data.content;
        data.content.forEach((p: ProdutoDTO) => {
          if (p?.id) this.idNomeCache.set(p.id, p?.nome || '');
        });
        this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
        this.isLoadingProdutos = false;
      },
      error: () => {
        this.isLoadingProdutos = false;
      }
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
        const msg: string = (err?.error?.message || err?.error || '') as string;
        this.produtosRelacionadosErro = [{ id: produto.id || 0, nome: produto.nome || '' }];
        if (msg.includes('histórico de venda') || msg.toLowerCase().includes('venda')) {
          this.produtoExcluido = produto;
          this.tipoHistoricoModal = 'VENDA';
          this.modalService.open(this.modalErroExcluirHistorico);
          return;
        }
        if (msg.includes('histórico de compra') || msg.toLowerCase().includes('compra')) {
          this.produtoExcluido = produto;
          this.tipoHistoricoModal = 'COMPRA';
          this.modalService.open(this.modalErroExcluirHistorico);
          return;
        }
      }
    });
  }

  // Abertura de modal de confirmação (exclusão individual)
  abrirModalConfirmarExclusaoProduto(produto: ProdutoDTO) {
    this.produtoExcluido = produto;
    this.modalService.open(this.modalConfirmDeleteProduto, { size: 'sm', backdrop: 'static' });
  }

  confirmarExclusaoProduto(modal: any) {
    const id = this.produtoExcluido?.id ?? 0;
    if (!id) { modal.close(); return; }
    this.deletarProduto(this.modalMsgExcluir, id, this.produtoExcluido);
    modal.close();
  }

  // Seleção múltipla helpers
  isTodosProdutosSelecionados(): boolean {
    return (this.listaDeProdutos || []).length > 0 && (this.listaDeProdutos || []).every(p => this.selecionadosProdutoIds.has(p.id || -1));
  }

  onToggleSelecionarTodosProdutos(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const checked = !!input?.checked;
    if (checked) {
      (this.listaDeProdutos || []).forEach(p => {
        const id = (p.id ?? 0);
        if (id > 0) this.selecionadosProdutoIds.add(id);
      });
    } else {
      this.selecionadosProdutoIds.clear();
    }
  }

  onToggleCheckboxProduto(produto: ProdutoDTO, event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const checked = !!input?.checked;
    const id = produto.id ?? 0;
    if (id <= 0) return;
    if (checked) this.selecionadosProdutoIds.add(id); else this.selecionadosProdutoIds.delete(id);
  }

  abrirModalExcluirSelecionadosProdutos() {
    const ids = Array.from(this.selecionadosProdutoIds.values());
    if (ids.length === 0) return;
    this.produtosRelacionadosErro = [];
    this.modalService.open(this.modalConfirmDeleteMultiProdutos, { size: 'sm', backdrop: 'static' });
  }

  confirmarExclusaoSelecionadosProdutos(modal: any) {
    const ids = Array.from(this.selecionadosProdutoIds.values());
    if (ids.length === 0) { modal.close(); return; }
    // Atualização otimista imediata na UI
    const idsSet = new Set(ids);
    this.listaDeProdutos = (this.listaDeProdutos || []).filter(p => !idsSet.has(p.id || 0));
    this.totalRecords = (this.listaDeProdutos || []).length;
    this.selecionadosProdutoIds.clear();
    this.produtoMultiDelete(ids);
    modal.close();
  }

  private produtoMultiDelete(ids: number[]) {
    this.produtosRelacionadosErro = []; // limpa antes

    this.produtoService.excluirMultiProdutos(ids)
      .pipe(
        // se chegou aqui sem erro, consideramos sucesso
        tap(() => {
          // reforça estado final
          this.selecionadosProdutoIds.clear();
          this.produtosRelacionadosErro = [];
          // revalida com o backend
          this.atualizarLista();
        }),
        catchError(async (err) => {
        const payload = await this.alinharResponseMultiDelete(err);
        const produtosBackend = Array.isArray(payload?.produtos) ? payload.produtos : [];

        // Monta lista APENAS a partir do backend (evita falsos positivos)
        const bloqueados = produtosBackend
          .map((pb: any) => ({
            id: Number(pb.id ?? pb.produtoId ?? pb.idProduto ?? pb['produto_id'] ?? 0),
            nome: String(pb.nome ?? pb.nomeProduto ?? pb['nome_produto'] ?? this.getNomeLocalById(Number(pb.id ?? 0)))
          }))
          .filter(p => p.id > 0);

        // Filtra para manter apenas os que ainda existem na lista (não foram excluídos)
        const aindaExibidos = bloqueados.filter(p =>
          !!(this.listaDeProdutos?.some(lp => lp.id === p.id) || this.listaDeProdutosOriginal?.some(lo => lo.id === p.id))
        );
        this.produtosRelacionadosErro = aindaExibidos;

        // Define tipo com base no payload
        const msg = String(payload?.message || '');
        const temVenda  = produtosBackend.some((p: any) => (p.tipo || '').toUpperCase() === 'VENDA') || msg.toLowerCase().includes('venda');
        const temCompra = produtosBackend.some((p: any) => (p.tipo || '').toUpperCase() === 'COMPRA') || msg.toLowerCase().includes('compra');
        this.tipoHistoricoModal = temVenda ? 'VENDA' : 'COMPRA';

        if ((this.produtosRelacionadosErro?.length || 0) > 0) {
          this.modalService.open(this.modalErroExcluirHistorico);
        }
        // não propaga erro para não quebrar finalize()
        return EMPTY;
      }),
      finalize(() => {
        // sempre refaz a listagem (sucesso ou erro)
        this.atualizarLista();
      })
    )
    .subscribe();
}
  
  private async alinharResponseMultiDelete(err: any): Promise<{ produtos?: any[]; message?: string }> {
    let payload: any = err?.error;
    // 3) Se veio array, use o primeiro item (seu backend manda [{"produtos":...}])
    if (Array.isArray(payload)) payload = payload[0];

    // 4) Garante objeto
    if (!payload || typeof payload !== 'object') return { produtos: [], message: '' };
    return payload;
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
      data.content.forEach((p: ProdutoDTO) => {
        if (p?.id) this.idNomeCache.set(p.id, p?.nome || '');
      });
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
      this.cacheSortSignature = '';
      this.aplicarFiltroLocal();
    });
    this.listarProdutoMaisCaro(this.authService.getUsuarioLogado().idUsuario);
    this.calcularMedia(this.authService.getUsuarioLogado().idUsuario);
  }

  baixarPdfProdutos(): void {
    const produtos = this.obterListaOrdenada();
    if (!produtos || produtos.length === 0) {
      this.showSmallModal('Não há produtos para exportar.');
      return;
    }

    const colunas = ['ID', 'Nome', 'Fornecedor', 'Quantidade', 'Valor unitário', 'Promoção', 'Vl qtd desc', 'Valor total'];
    const linhas = produtos.map((produto: ProdutoDTO) => ({
      'ID': produto.id ?? '-',
      'Nome': produto.nome || '-',
      'Fornecedor': produto.fornecedor?.nome || '- (Sem fornecedor)',
      'Quantidade': produto.quantia ?? 0,
      'Valor unitário': this.formatarValor(produto.valorInicial ?? 0),
      'Promoção': produto.promocao ? 'Ativa' : 'Inativa',
      'Vl qtd desc': this.formatarValor(produto.valorTotalDesc ?? 0),
      'Valor total': this.formatarValor(produto.somaTotalValores ?? 0)
    }));

    const totalRegistros = produtos.length;
    const payload: ExportarRelatorioPayload = {
      titulo: 'Lista de produtos',
      colunas,
      linhas,
      paisagem: true,
      rodapeDireita: totalRegistros > 0 ? `Total de registros: ${totalRegistros}` : undefined
    };

    this.relatoriosService.exportarPdf(payload).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'lista-produtos.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.showSmallModal('Erro ao exportar PDF. Tente novamente.');
      }
    });
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
    this.cacheSortSignature = '';
    this.obterListaOrdenada();
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

  private getNomeLocalById(id: number): string {
    return this.idNomeCache.get(id)
        || (this.listaDeProdutos?.find(p => p.id === id)?.nome ?? '')
        || (this.listaDeProdutosOriginal?.find(p => p.id === id)?.nome ?? '');
  }

  setSort(column: 'checkbox' | 'id' | 'nome' | 'fornecedor' | 'descricao' | 'quantia' | 'valorInicial' | 'promocao' | 'valorTotalDesc' | 'somaTotalValores'): void {
    if (column === 'checkbox') return;
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = column === 'id' || column === 'nome' ? 'asc' : 'desc';
    }
    this.cacheSortSignature = '';
    this.obterListaOrdenada();
  }

  getSortClass(column: 'checkbox' | 'id' | 'nome' | 'fornecedor' | 'descricao' | 'quantia' | 'valorInicial' | 'promocao' | 'valorTotalDesc' | 'somaTotalValores'): string {
    if (this.sortColumn !== column) return 'inactive';
    return this.sortDirection === 'asc' ? 'asc' : 'desc';
  }

  get produtosOrdenados(): ProdutoDTO[] {
    return this.obterListaOrdenada();
  }

  private obterListaOrdenada(): ProdutoDTO[] {
    const assinatura = `${this.sortColumn}_${this.sortDirection}_${this.listaDeProdutos?.length || 0}_${this.totalRecords}`;
    if (this.cacheSortSignature === assinatura) {
      return this.cacheListaOrdenada;
    }
    const lista = [...(this.listaDeProdutos || [])];
    lista.sort((a, b) => this.compararProdutos(a, b));
    this.cacheListaOrdenada = lista;
    this.cacheSortSignature = assinatura;
    return lista;
  }

  private compararProdutos(a: ProdutoDTO, b: ProdutoDTO): number {
    let valorA: any;
    let valorB: any;
    switch (this.sortColumn) {
      case 'id':
        valorA = a.id ?? 0;
        valorB = b.id ?? 0;
        break;
      case 'nome':
        valorA = (a.nome || '').toLowerCase();
        valorB = (b.nome || '').toLowerCase();
        break;
      case 'fornecedor':
        valorA = (a.fornecedor?.nome || '').toLowerCase();
        valorB = (b.fornecedor?.nome || '').toLowerCase();
        break;
      case 'descricao':
        valorA = (a.descricao || '').toLowerCase();
        valorB = (b.descricao || '').toLowerCase();
        break;
      case 'quantia':
        valorA = a.quantia ?? 0;
        valorB = b.quantia ?? 0;
        break;
      case 'valorInicial':
        valorA = a.valorInicial ?? 0;
        valorB = b.valorInicial ?? 0;
        break;
      case 'promocao':
        valorA = a.promocao ? 1 : 0;
        valorB = b.promocao ? 1 : 0;
        break;
      case 'valorTotalDesc':
        valorA = a.valorTotalDesc ?? 0;
        valorB = b.valorTotalDesc ?? 0;
        break;
      case 'somaTotalValores':
        valorA = a.somaTotalValores ?? 0;
        valorB = b.somaTotalValores ?? 0;
        break;
      default:
        valorA = 0;
        valorB = 0;
    }
    if (valorA < valorB) return this.sortDirection === 'asc' ? -1 : 1;
    if (valorA > valorB) return this.sortDirection === 'asc' ? 1 : -1;
    return 0;
  }

  protected readonly alert = alert;
}
