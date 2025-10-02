import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule, CurrencyPipe, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProdutoService } from '../../service/produto/produto.service';
import { AuthService } from '../../service/auth/auth.service';
import { ProdutoDTO } from '../../model/dto/ProdutoDTO';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { NgbModule, NgbModal, NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { CompraDTO } from '../../model/dto/CompraDTO';
import { ComprarProdutosService } from '../../service/comprar-produtos/comprar-produtos.service';
import { HistoricoComprasDTO } from '../../model/dto/HistoricoComprasDTO';

@Component({
  selector: 'app-comprar-produtos',
  templateUrl: './comprar-produtos.component.html',
  styleUrls: ['./comprar-produtos.component.css'],
  imports: [CommonModule, FormsModule, NgIf, NgForOf, CurrencyPipe, MatPaginatorModule, NgxMaskDirective, NgbModule, NgbToastModule],
  providers: [provideNgxMask()]
})
export class ComprarProdutosComponent implements OnInit {
  @ViewChild('modalCompraSucessoTmpl') modalCompraSucessoTmpl!: TemplateRef<any>;
  @ViewChild('modalHistoricoComprasTmpl') modalHistoricoComprasTmpl!: TemplateRef<any>;
  @ViewChild('modalConfirmDeleteCompra') modalConfirmDeleteCompra!: TemplateRef<any>;
  @ViewChild('modalConfirmDeleteMultiCompra') modalConfirmDeleteMultiCompra!: TemplateRef<any>;
  @ViewChild('modalSaldoInsuficiente') modalSaldoInsuficiente!: TemplateRef<any>;
  // Toast de salvar
  showSaveToast: boolean = false;
  saveToastText: string = '';
  private todosProdutos: ProdutoDTO[] = [];
  produtosFiltrados: ProdutoDTO[] = [];
  carregando = false;
  erro: string | null = null;

  // seleção múltipla: mapa idProduto -> { quantidade, preco }
  selecionados: { [produtoId: number]: { quantidade: number; preco: number } } = {};
  compras: CompraDTO[] = [];
  historico: HistoricoComprasDTO[] = [];
  historicoSelecionado: HistoricoComprasDTO | null = null;
  historicoComprasFiltrado: HistoricoComprasDTO[] = [];
  historicoFiltro: string = '';
  selectedHistoricoCompraId: number | null = null;
  selecionadosHistoricoIds: Set<number> = new Set<number>();
  // Visualização de itens no histórico de compras
  itensCompraVisualizacao: { idProduto: number | null; nome: string; descricao: string; precoUnit: number; quantidade: number; subtotal: number }[] = [];
  compraParaVisualizar: HistoricoComprasDTO | null = null;
  salvou = false;
  dirty = false;
  ultimoHistoricoGerado: HistoricoComprasDTO | null = null;
  usuarioLogin: string = '';
  // Resumo para modal de sucesso de compra (espelhando modal de venda)
  lastCompraResumo: { id: number; totalQuantidade: number; totalValor: number } | null = null;
  lastCompraItensResumo: { nome: string; quantidade: number; subtotal: number }[] = [];
  // Loading states
  isFinalizandoCompra: boolean = false;
  isExcluindoHistorico: boolean = false;
  // Overlay de carregamento (estilo CEP)
  overlayCarregando: boolean = false;
  overlayTexto: string = '';
  // Dados para modal de saldo insuficiente
  saldoAtual: number = 0;
  totalCompraAtual: number = 0;

  // Filtros e paginação
  searchNome: string = '';
  searchFornecedor: string = '';
  totalRecords: number = 0;
  currentPage: number = 0;
  pageSize: number = 5;

  constructor(private produtoService: ProdutoService, private auth: AuthService, private comprasService: ComprarProdutosService, private modalService: NgbModal) {}

  ngOnInit(): void {
    const usuario = this.auth.getUsuarioLogado();
    this.usuarioLogin = (usuario as any)?.login || '';
    const idUsuario = usuario?.idUsuario;
    if (!idUsuario) {
      this.todosProdutos = [];
      this.produtosFiltrados = [];
      return;
    }
    this.carregarProdutos(idUsuario);
    this.carregarHistorico(idUsuario);

    // Exibe toast de saldo atualizado após refresh, se marcado anteriormente
    try {
      const shouldShowSaldoToast = localStorage.getItem('saldo.toast.after.refresh');
      if (shouldShowSaldoToast === '1') {
        this.saveToastText = 'Saldo atualizado';
        this.showSaveToast = true;
        setTimeout(() => {
          this.showSaveToast = false;
          try { localStorage.removeItem('saldo.toast.after.refresh'); } catch {}
        }, 2000);
      }
    } catch {}
  }

  private carregarProdutos(idUsuario: number): void {
    this.carregando = true;
    this.erro = null;
    // Carrega uma quantidade suficiente para paginação client-side
    this.produtoService.listarProdutos(0, 1000, idUsuario).subscribe({
      next: (res) => {
        this.todosProdutos = res.content || [];
        this.aplicarFiltros();
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Não foi possível carregar os produtos.';
        this.carregando = false;
      }
    });
  }

  private carregarHistorico(idUsuario: number): void {
    this.comprasService.getHistoricoCompras(idUsuario).subscribe({
      next: (lista) => {
        this.historico = lista || [];
        this.aplicarFiltroHistoricoCompras();
      },
      error: () => {}
    });
  }

  isSelecionado(p: ProdutoDTO): boolean {
    const id = p.id ?? -1;
    return id in this.selecionados;
  }

  toggleSelecionado(p: ProdutoDTO, checked: boolean): void {
    const id = p.id ?? -1;
    if (id < 0) return;
    if (checked) {
      if (!this.selecionados[id]) {
        this.selecionados[id] = {
          quantidade: 0,
          preco: 0
        };
      }
    } else {
      delete this.selecionados[id];
    }
  }

  getTotalLinha(p: ProdutoDTO): number {
    const id = p.id ?? -1;
    const sel = id >= 0 ? this.selecionados[id] : undefined;
    if (!sel) return 0;
    const q = Math.max(0, sel.quantidade ?? 0);
    const pr = Math.max(0, sel.preco ?? 0);
    return q * pr;
  }

  onQuantidadeChanged(p: ProdutoDTO, value: any): void {
    const id = p.id ?? -1;
    if (!(id in this.selecionados)) return;
    const n = Number(value);
    const clamped = !Number.isFinite(n) || n < 0 ? 0 : Math.floor(n);
    this.selecionados[id].quantidade = clamped;
    this.syncCompra(p);
    this.salvou = false;
    this.dirty = true;
  }

  onPrecoChanged(p: ProdutoDTO, value: any): void {
    const id = p.id ?? -1;
    if (!(id in this.selecionados)) return;
    const n = Number(value);
    const clamped = !Number.isFinite(n) || n < 0 ? 0 : n;
    this.selecionados[id].preco = clamped;
    this.syncCompra(p);
    this.salvou = false;
    this.dirty = true;
  }

  onToggleChange(p: ProdutoDTO, event: Event): void {
    const input = event?.target as HTMLInputElement | null;
    const checked = !!(input && input.checked);
    this.toggleSelecionado(p, checked);
    if (checked) {
      this.syncCompra(p);
    } else {
      // remove compra da lista ao desmarcar
      const id = p.id ?? -1;
      this.compras = this.compras.filter(c => (c.produto.id ?? -2) !== id);
    }
    this.salvou = false;
    this.dirty = true;
  }

  onQuantidadeKeyDown(event: KeyboardEvent): void {
    const key = event.key;
    if (key === '-' || key === '+') {
      event.preventDefault();
    }
  }

  get totalQuantidadeSelecionada(): number {
    return Object.keys(this.selecionados)
      .map(k => this.selecionados[Number(k)]?.quantidade ?? 0)
      .reduce((a, b) => a + Math.max(0, b), 0);
  }

  get totalValorSelecionado(): number {
    return Object.keys(this.selecionados)
      .map(k => {
        const sel = this.selecionados[Number(k)];
        const q = Math.max(0, sel?.quantidade ?? 0);
        const p = Math.max(0, sel?.preco ?? 0);
        return q * p;
      })
      .reduce((a, b) => a + b, 0);
  }

  private syncCompra(p: ProdutoDTO): void {
    const id = p.id ?? -1;
    if (id < 0 || !(id in this.selecionados)) return;
    const sel = this.selecionados[id];
    const qtde = Math.max(0, sel.quantidade ?? 0);
    const preco = Math.max(0, sel.preco ?? 0);
    const total = qtde * preco;

    const idUsuario = this.auth.getUsuarioLogado()?.idUsuario ?? 0;
    const compra: CompraDTO = {
      id: null,
      idUsuario,
      produto: p,
      quantidadeComprada: qtde,
      valorUnitarioCompra: preco,
      valorTotalCompra: total
    };

    const idx = this.compras.findIndex(c => (c.produto.id ?? -2) === id);
    if (idx >= 0) {
      this.compras[idx] = compra;
    } else {
      this.compras.push(compra);
    }
  }

  salvarCompras(): void {
    // Valida saldo antes de salvar
    const saldo = Number((this.auth.getUsuarioLogado() as any)?.saldo || 0);
    const total = this.totalValorSelecionado;
    if (saldo <= 0 || (total > 0 && saldo < total)) {
      this.saldoAtual = saldo;
      this.totalCompraAtual = total;
      if (this.modalSaldoInsuficiente) {
        this.modalService.open(this.modalSaldoInsuficiente);
      }
      return;
    }
    // mantém somente itens marcados e válidos
    const marcados = Object.keys(this.selecionados).map(k => Number(k)).filter(id => this.isSelecionado({ id } as ProdutoDTO));
    this.compras = this.compras.filter(c => {
      const id = c.produto.id ?? -1;
      const valido = c.quantidadeComprada >= 1 && c.valorUnitarioCompra >= 0;
      return marcados.includes(id) && valido;
    });
    this.salvou = true;
    this.dirty = false;
    // Exibe toast de sucesso (substitui modal)
    this.saveToastText = 'Informações da compra foram salvas';
    this.showSaveToast = true;
    setTimeout(() => (this.showSaveToast = false), 2000);
  }

  finalizarCompras(): void {
    // Envia a lista inteira de compras em uma única chamada
    if (!this.podeFinalizar || this.isFinalizandoCompra) { return; }
    // Checagem defensiva de saldo antes de iniciar processo
    try {
      const saldo = Number((this.auth.getUsuarioLogado() as any)?.saldo || 0);
      const total = this.totalValorSelecionado;
      if (saldo <= 0 || (total > 0 && saldo < total)) {
        this.saldoAtual = saldo;
        this.totalCompraAtual = total;
        if (this.modalSaldoInsuficiente) {
          this.modalService.open(this.modalSaldoInsuficiente);
        }
        return;
      }
    } catch {}
    this.isFinalizandoCompra = true;
    this.overlayCarregando = true;
    this.overlayTexto = 'Finalizando compra...';
    this.comprasService.cadastrarCompras(this.compras).subscribe({
      next: (hist: HistoricoComprasDTO) => {
        // atualiza histórico dinamicamente adicionando a última compra no topo
        this.historico = [hist, ...this.historico];
        // Reaplica filtro para refletir imediatamente na UI
        this.aplicarFiltroHistoricoCompras();
        this.ultimoHistoricoGerado = hist;
        this.salvou = false;
        // Prepara dados do modal de sucesso espelhando o modal de venda
        const idCompra = (hist.id ?? 0) as number;
        this.lastCompraResumo = {
          id: idCompra,
          totalQuantidade: hist.quantidadeTotal,
          totalValor: hist.valorTotal
        };
        this.lastCompraItensResumo = (hist.compras || []).map((c) => ({
          nome: c?.produto?.nome ?? '',
          quantidade: c?.quantidadeComprada ?? 0,
          subtotal: c?.valorTotalCompra ?? 0
        }));
        // abre modal de sucesso
        if (this.modalCompraSucessoTmpl) {
          const ref = this.modalService.open(this.modalCompraSucessoTmpl, { size: 'lg' });
          const onClose = () => {
            try { localStorage.setItem('saldo.toast.after.refresh', '1'); } catch {}
            window.location.reload();
          };
          ref.closed.subscribe(onClose);
          ref.dismissed.subscribe(onClose);
        }
        // Atualiza saldo no perfil (debita total da compra)
        try {
          const u = this.auth.getUsuarioLogado() as any;
          const saldoAtual = Number(u?.saldo || 0);
          const novoSaldo = saldoAtual - Number(hist?.valorTotal || 0);
          this.auth.adicionarUsuarioLogado({ ...u, saldo: novoSaldo } as any);
        } catch {}
        // Recarrega do servidor para garantir consistência
        const idUsuarioRefresh = this.auth.getUsuarioLogado()?.idUsuario;
        if (idUsuarioRefresh) {
          // Recarrega histórico
          this.comprasService.getHistoricoCompras(idUsuarioRefresh).subscribe({
            next: (lista) => {
              this.historico = lista || [];
              this.aplicarFiltroHistoricoCompras();
            }
          });
          // Recarrega lista de produtos para refletir novas quantidades
          this.carregarProdutos(idUsuarioRefresh);
          // Limpa seleção e compras para evitar inputs habilitados
          this.selecionados = {};
          this.compras = [];
          this.salvou = false;
          this.dirty = false;
          this.isFinalizandoCompra = false;
          this.overlayCarregando = false;
          this.overlayTexto = '';
        } else {
          this.isFinalizandoCompra = false;
          this.overlayCarregando = false;
          this.overlayTexto = '';
        }
      },
      error: (err) => {
        console.error('Erro ao finalizar compras:', err);
        this.isFinalizandoCompra = false;
        this.overlayCarregando = false;
        this.overlayTexto = '';
      }
    });
  }

  get podeFinalizar(): boolean {
    if (!this.salvou || this.dirty) return false;
    const idsSelecionadosValidos = Object.keys(this.selecionados)
      .map(k => Number(k))
      .filter(id => this.isSelecionado({ id } as ProdutoDTO))
      .filter(id => {
        const sel = this.selecionados[id];
        return (sel?.quantidade ?? 0) >= 1 && (sel?.preco ?? 0) >= 0;
      });
    if (idsSelecionadosValidos.length === 0) return false;
    const idsSalvos = new Set(this.compras.map(c => c.produto.id ?? -1));
    const todosSalvos = idsSelecionadosValidos.every(id => idsSalvos.has(id));
    if (!todosSalvos) return false;
    // Validação de saldo: não permite finalizar com saldo = 0 ou saldo < total da compra
    const saldo = Number((this.auth.getUsuarioLogado() as any)?.saldo || 0);
    const total = this.totalValorSelecionado;
    if (saldo <= 0) return false;
    if (total > 0 && saldo < total) return false;
    return true;
  }

  get podeSalvar(): boolean {
    const idsSelecionadosValidos = Object.keys(this.selecionados)
      .map(k => Number(k))
      .filter(id => this.isSelecionado({ id } as ProdutoDTO))
      .filter(id => {
        const sel = this.selecionados[id];
        return (sel?.quantidade ?? 0) >= 1 && (sel?.preco ?? 0) >= 0;
      });
    return idsSelecionadosValidos.length >= 1;
  }

  abrirHistorico(): void {
    if (this.modalHistoricoComprasTmpl) {
      this.modalService.open(this.modalHistoricoComprasTmpl, { size: 'xl' });
    }
  }

  // Histórico estilo PDV
  aplicarFiltroHistoricoCompras(): void {
    const termo = (this.historicoFiltro || '').toLowerCase().trim();
    if (!termo) {
      this.historicoComprasFiltrado = this.historico;
      return;
    }
    const termoNumero = Number(termo);
    const buscarPorIdProduto = !isNaN(termoNumero);
    this.historicoComprasFiltrado = (this.historico || []).filter((h) => {
      const compras = h.compras || [];
      return compras.some((c) => {
        const prod = c?.produto;
        if (!prod) return false;
        if (buscarPorIdProduto) {
          return (prod.id ?? -1) === termoNumero;
        }
        const nome = (prod?.nome || '').toLowerCase();
        return nome.includes(termo);
      });
    });
  }

  onToggleHistoricoCompra(h: HistoricoComprasDTO): void {
    if (this.selectedHistoricoCompraId === h.id) {
      this.selectedHistoricoCompraId = null;
      this.compraParaVisualizar = null;
      this.itensCompraVisualizacao = [];
    } else {
      this.selectedHistoricoCompraId = h.id ?? null;
      this.compraParaVisualizar = h;
      const itens = (h.compras || []).map((c) => {
        const nome = c?.produto?.nome || '';
        const descricao = c?.produto?.descricao || '';
        const precoUnit = c?.valorUnitarioCompra || 0;
        const quantidade = c?.quantidadeComprada || 0;
        return {
          idProduto: c?.produto?.id ?? null,
          nome,
          descricao,
          precoUnit,
          quantidade,
          subtotal: precoUnit * quantidade
        };
      });
      this.itensCompraVisualizacao = itens;
    }
  }

  toggleSelecionadoHistorico(h: HistoricoComprasDTO, checked: boolean): void {
    const id = h.id ?? null;
    if (!id) return;
    if (checked) {
      this.selecionadosHistoricoIds.add(id);
    } else {
      this.selecionadosHistoricoIds.delete(id);
    }
  }

  onToggleCheckboxHistorico(h: HistoricoComprasDTO, event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const checked = !!input?.checked;
    this.toggleSelecionadoHistorico(h, checked);
  }

  isTodosSelecionados(): boolean {
    if (!this.historicoComprasFiltrado || this.historicoComprasFiltrado.length === 0) return false;
    return this.historicoComprasFiltrado.every(h => this.selecionadosHistoricoIds.has(h.id || -1));
  }

  onToggleSelecionarTodos(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const checked = !!input?.checked;
    if (!this.historicoComprasFiltrado) return;
    if (checked) {
      this.historicoComprasFiltrado.forEach(h => this.selecionadosHistoricoIds.add(h.id || -1));
    } else {
      this.selecionadosHistoricoIds.clear();
    }
  }

  excluirSelecionadosHistorico(): void {
    const ids = Array.from(this.selecionadosHistoricoIds.values());
    if (ids.length === 0) { return; }
    this.overlayCarregando = true;
    this.overlayTexto = 'Excluindo registros selecionados...';
    this.comprasService.excluirMultiHistoricosCompras(ids).subscribe({
      next: () => {
        this.historico = this.historico.filter(h => !this.selecionadosHistoricoIds.has(h.id ?? -1));
        this.selecionadosHistoricoIds.clear();
        this.aplicarFiltroHistoricoCompras();
        this.overlayCarregando = false;
        this.overlayTexto = '';
      },
      error: () => {
        this.overlayCarregando = false;
        this.overlayTexto = '';
      }
    });
  }

  abrirModalExcluirSelecionados(): void {
    if (this.modalConfirmDeleteMultiCompra) {
      this.modalService.open(this.modalConfirmDeleteMultiCompra, { size: 'sm' });
    }
  }

  confirmarExclusaoSelecionados(modalRef: any): void {
    modalRef.close();
    this.excluirSelecionadosHistorico();
  }

  abrirModalExcluirCompra(h: HistoricoComprasDTO, event?: Event): void {
    if (event) { event.stopPropagation(); }
    this.compraParaVisualizar = h; // reutiliza para guardar o alvo da exclusão
    if (this.modalConfirmDeleteCompra) {
      this.modalService.open(this.modalConfirmDeleteCompra, { size: 'sm' });
    }
  }

  confirmarExclusaoCompra(modalRef: any): void {
    const id = this.compraParaVisualizar?.id;
    if (!id) { modalRef.dismiss(); return; }
    this.isExcluindoHistorico = true;
    this.overlayCarregando = true;
    this.overlayTexto = 'Excluindo registro...';
    this.comprasService.excluirHistoricoCompra(id).subscribe({
      next: () => {
        this.historico = this.historico.filter(x => x.id !== id);
        this.selectedHistoricoCompraId = null;
        this.compraParaVisualizar = null;
        this.itensCompraVisualizacao = [];
        this.aplicarFiltroHistoricoCompras();
        this.isExcluindoHistorico = false;
        modalRef.close();
        this.overlayCarregando = false;
        this.overlayTexto = '';
      },
      error: () => {
        this.isExcluindoHistorico = false;
        modalRef.dismiss();
        this.overlayCarregando = false;
        this.overlayTexto = '';
      }
    });
  }

  getQuantidade(p: ProdutoDTO): number {
    const id = p.id ?? -1;
    const sel = this.selecionados[id];
    return Math.max(0, sel ? sel.quantidade ?? 0 : 0);
  }

  getPreco(p: ProdutoDTO): number {
    const id = p.id ?? -1;
    const sel = this.selecionados[id];
    return Math.max(0, sel ? sel.preco ?? 0 : 0);
  }

  onSearchChanged(): void {
    this.aplicarFiltros();
  }

  private aplicarFiltros(): void {
    const nome = (this.searchNome || '').toLowerCase();
    const fornecedor = (this.searchFornecedor || '').toLowerCase();
    this.produtosFiltrados = this.todosProdutos.filter(p => {
      const okNome = !nome || (p.nome || '').toLowerCase().includes(nome);
      const okForn = !fornecedor || (p.fornecedor?.nome || '').toLowerCase().includes(fornecedor);
      return okNome && okForn;
    });
    this.totalRecords = this.produtosFiltrados.length;
    this.currentPage = 0;
  }

  get produtosPaginados(): ProdutoDTO[] {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    return this.produtosFiltrados.slice(start, end);
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
  }
}


