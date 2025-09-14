import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule, NgbModal, NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ProdutoDTO } from '../../model/dto/ProdutoDTO';
import { ProdutoService } from '../../service/produto/produto.service';
import { AuthService } from '../../service/auth/auth.service';
import { PontoVendaService } from '../../service/ponto-venda/ponto-venda.service';
import { CaixaItemDTO } from '../../model/dto/CaixaItemDTO';
import { VendaCaixaDTO } from '../../model/dto/VendaCaixaDTO';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';

interface CaixaItem extends CaixaItemDTO {}

@Component({
  selector: 'app-ponto-venda',
  standalone: true,
  imports: [CommonModule, NgIf, NgForOf, FormsModule, HttpClientModule, NgbModule, NgbToastModule, MatPaginatorModule],
  templateUrl: './ponto-venda.component.html',
  styleUrls: ['./ponto-venda.component.css']
})
export class PontoVendaComponent implements OnInit {
  @ViewChild('modalVendaSucessoTmpl') modalVendaSucessoTmpl!: TemplateRef<any>;
  @ViewChild('modalVisualizarVenda') modalVisualizarVenda!: TemplateRef<any>;
  @ViewChild('modalHistorico') modalHistorico!: TemplateRef<any>;
  @ViewChild('modalConfirmDeleteMultiVendas') modalConfirmDeleteMultiVendas!: TemplateRef<any>;
  vendaId: number = 0;
  produtos: ProdutoDTO[] = [];
  produtosFiltrados: ProdutoDTO[] = [];
  totalRecords: number = 0;
  currentPage: number = 0;
  pageSize: number = 5;
  caixa: CaixaItem[] = [];
  // Quantidade digitada por produto para adição manual ao caixa
  quantidadeDigitada: { [produtoId: number]: number } = {};
  totalQuantidade = 0;
  totalValor = 0;
  salvarHabilitado = true;
  finalizarHabilitado = false; // após primeiro salvar, permanece habilitado até finalizar
  filtro = '';
  // Modo de preço selecionado: valorInicial | valorTotalDesc | valorTotalFrete
  modoPreco: 'valorInicial' | 'valorTotalDesc' | 'valorTotalFrete' = 'valorInicial';
  historicoVendas: any[] = [];
  historicoVendasFiltrado: any[] = [];
  historicoFiltro: string = '';
  usuarioLogin: string = '';
  vendaParaVisualizar: VendaCaixaDTO | null = null;
  itensVisualizacao: { item: CaixaItem; produto?: ProdutoDTO; precoUnit: number; subtotal: number }[] = [];
  selectedHistoricoId: number | null = null;
  selecionadosHistoricoVendasIds: Set<number> = new Set<number>();
  // Toast de salvar
  showSaveToast: boolean = false;
  saveToastText: string = '';
  // Modal de sucesso ao finalizar
  lastVendaResumo: { id: number; totalQuantidade: number; totalValor: number } | null = null;
  lastVendaItensResumo: { nome: string; quantidade: number; subtotal: number }[] = [];
  // Estado de buffering ao salvar
  isSaving: boolean = false;
  // Loading states específicos
  isFinalizandoVenda: boolean = false;
  isDeletingHistorico: boolean = false;
  overlayCarregandoPdv: boolean = false;
  overlayTextoPdv: string = '';

  constructor(
    private produtoService: ProdutoService,
    private authService: AuthService,
    private pdvService: PontoVendaService,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.pdvService.acessarPaginaPdv().subscribe();
    const usuario = this.authService.getUsuarioLogado();
    const idUsuario = usuario.idUsuario;
    this.usuarioLogin = (usuario as UsuarioDTO)?.login;
    // Restaura venda em andamento (não finalizada) para sobrescrever ao salvar novamente
    const persistedVendaId = Number(localStorage.getItem('pdv.vendaId'));
    if (!isNaN(persistedVendaId) && persistedVendaId > 0) {
      this.vendaId = persistedVendaId;
    }
    // Carrega lista de produtos do usuário
    this.produtoService.listarProdutos(0, 100, idUsuario).subscribe({
      next: (resp: any) => {
        this.produtos = resp?.content ?? [];
        this.produtosFiltrados = this.produtos;
        this.totalRecords = this.produtosFiltrados.length;
        this.currentPage = 0;
        // Carrega histórico após carregar a tela
        this.pdvService.listarHistorico().subscribe({
          next: (lista) => {
            this.historicoVendas = lista ?? [];
            this.aplicarFiltroHistorico();
          }
        });
      }
    });
  }

  aplicarFiltro(): void {
    const f = this.filtro.toLowerCase();
    this.produtosFiltrados = this.produtos.filter(p =>
      (p.nome?.toLowerCase().includes(f) ||
       p.descricao?.toLowerCase().includes(f))
    );
    this.totalRecords = this.produtosFiltrados.length;
    this.currentPage = 0;
  }

  selecionarModoPreco(modo: 'valorInicial' | 'valorTotalDesc' | 'valorTotalFrete'): void {
    this.modoPreco = modo;
    this.recalcularTotais();
    this.recalcularVisualizacao();
  }

  obterPreco(produto: ProdutoDTO): number {
    const fallback = (produto.valor ?? 0);
    if (this.modoPreco === 'valorInicial') {
      return produto.valorInicial ?? fallback;
    }
    if (this.modoPreco === 'valorTotalDesc') {
      return produto.valorTotalDesc ?? fallback;
    }
    // 'valorTotalFrete'
    return produto.valorTotalFrete ?? fallback;
  }

  get produtosPagina(): ProdutoDTO[] {
    const inicio = this.currentPage * this.pageSize;
    return this.produtosFiltrados.slice(inicio, inicio + this.pageSize);
  }

  trocarPagina(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  adicionarNoCaixa(produto: ProdutoDTO): void {
    // Impede adicionar além do estoque disponível
    if ((produto.quantia ?? 0) <= 0) {
      return;
    }
    const existente = this.caixa.find(i => i.idProduto === produto.id);
    if (existente) {
      existente.quantidade += 1;
    } else {
      this.caixa.push({ idProduto: produto.id!, quantidade: 1 , tipoPreco: this.modoPreco});
    }
    // Decrementa o estoque disponível exibido na lista
    produto.quantia = Math.max(0, (produto.quantia ?? 0) - 1);
    this.recalcularTotais();
  }

  // Adiciona ao caixa a quantidade digitada para o produto
  adicionarQuantidadeDigitada(produto: ProdutoDTO): void {
    const id = produto.id!;
    const disponivel = Math.max(0, produto.quantia ?? 0);
    const entradaRaw = this.quantidadeDigitada[id];
    let qtd = Number.isFinite(entradaRaw as any) ? Math.floor(entradaRaw) : 0;
    if (qtd < 1) { return; }
    if (qtd > disponivel) { qtd = disponivel; }
    if (qtd <= 0) { return; }

    const existente = this.caixa.find(i => i.idProduto === id);
    if (existente) {
      existente.quantidade += qtd;
    } else {
      this.caixa.push({ idProduto: id, quantidade: qtd, tipoPreco: this.modoPreco });
    }
    produto.quantia = Math.max(0, disponivel - qtd);
    this.recalcularTotais();
  }

  // Atualiza e sanitiza a quantidade digitada (somente números inteiros >= 1)
  onQuantidadeDigitadaChange(produto: ProdutoDTO, value: any): void {
    const id = produto.id!;
    // Sanitiza removendo não-dígitos e evitando concatenação indevida
    const texto = String(value ?? '').replace(/\D+/g, '');
    if (texto.length === 0) {
      // Permite limpar o campo para digitar novamente
      delete this.quantidadeDigitada[id];
      return;
    }
    let num = Number(texto);
    if (!Number.isFinite(num)) { delete this.quantidadeDigitada[id]; return; }
    const max = Math.max(0, produto.quantia ?? 0);
    if (num > max) { num = max; }
    this.quantidadeDigitada[id] = num;
  }

  // Impede digitar dígitos que resultem em valor acima do disponível
  onQuantidadeDigitadaKeyDownComValidacao(produto: ProdutoDTO, event: KeyboardEvent): void {
    const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End'];
    if (allowed.includes(event.key)) { return; }
    // Somente dígitos
    if (!/^\d$/.test(event.key)) {
      event.preventDefault();
      return;
    }
    const input = event.target as HTMLInputElement;
    const current = input.value ?? '';
    const start = (input.selectionStart ?? current.length);
    const end = (input.selectionEnd ?? current.length);
    const proposed = (current.slice(0, start) + event.key + current.slice(end)).replace(/\D+/g, '');
    const proposedNum = proposed.length > 0 ? Number(proposed) : 0;
    const max = Math.max(0, produto.quantia ?? 0);
    if (proposedNum > max) {
      event.preventDefault();
      return;
    }
  }

  // Clampa e espelha no input (inclusive para colar/pegar via mouse)
  onQuantidadeDigitadaInput(produto: ProdutoDTO, event: Event): void {
    const input = event.target as HTMLInputElement;
    const texto = (input.value || '').replace(/\D+/g, '');
    const max = Math.max(0, produto.quantia ?? 0);
    if (texto.length === 0) {
      // Campo vazio é permitido; mantém disabled do botão
      delete this.quantidadeDigitada[produto.id!];
      input.value = '';
      return;
    }
    let num = Number(texto);
    if (!Number.isFinite(num)) { delete this.quantidadeDigitada[produto.id!]; input.value = ''; return; }
    if (num > max) { num = max; }
    const id = produto.id!;
    this.quantidadeDigitada[id] = num;
    // Reflete de volta no campo para evitar concatenação visual
    input.value = num > 0 ? String(num) : '';
  }

  // Bloqueia teclas não numéricas no input de quantidade
  onQuantidadeDigitadaKeyDown(event: KeyboardEvent): void {
    const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'];
    if (allowed.includes(event.key)) { return; }
    if (!/^[0-9]$/.test(event.key)) {
      event.preventDefault();
    }
  }

  removerDoCaixa(item: CaixaItem): void {
    const idx = this.caixa.indexOf(item);
    if (idx >= 0) {
      // Restaura o estoque disponível para o produto correspondente
      const prod = this.getProdutoById(item.idProduto);
      if (prod) {
        prod.quantia = (prod.quantia ?? 0) + (item.quantidade || 0);
      }
      this.caixa.splice(idx, 1);
      this.recalcularTotais();
    }
  }

  alterarQuantidade(item: CaixaItem, delta: number): void {
    const prod = this.getProdutoById(item.idProduto);
    if (!prod) {
      return;
    }
    if (delta > 0) {
      // Somente incrementa se houver estoque disponível
      if ((prod.quantia ?? 0) > 0) {
        item.quantidade += 1;
        prod.quantia = Math.max(0, (prod.quantia ?? 0) - 1);
      }
    } else if (delta < 0) {
      // Permite decrementar até 1 (para remover totalmente use o botão Remover)
      if (item.quantidade > 1) {
        item.quantidade -= 1;
        prod.quantia = (prod.quantia ?? 0) + 1;
      }
    }
    this.recalcularTotais();
  }

  private recalcularTotais(): void {
    this.totalQuantidade = this.caixa.reduce((s, i) => s + i.quantidade, 0);
    this.totalValor = this.caixa.reduce((s, i) => {
      const prod = this.produtos.find(p => p.id === i.idProduto);
      return s + ((prod ? this.obterPreco(prod) : 0) * i.quantidade);
    }, 0);
    this.atualizarEstadoFinalizacao();
  }

  getProdutoById(idProduto: number): ProdutoDTO | undefined {
    return this.produtos.find(p => p.id === idProduto);
  }

  getNomeProduto(item: CaixaItem): string {
    const p = this.getProdutoById(item.idProduto);
    return p?.nome ?? '';
  }

  getDescricaoProduto(item: CaixaItem): string {
    const p = this.getProdutoById(item.idProduto);
    return p?.descricao ?? '';
  }

  obterPrecoItem(item: CaixaItem): number {
    const p = this.getProdutoById(item.idProduto);
    return p ? this.obterPreco(p) : 0;
  }

  salvarCaixa(): void {
    this.isSaving = true;
    const venda: VendaCaixaDTO = {
      id: this.vendaId > 0 ? this.vendaId : null,
      idUsuario: this.authService.getUsuarioLogado().idUsuario,
      itens: this.caixa,
      totalQuantidade: this.totalQuantidade,
      totalValor: this.totalValor
    };
    this.pdvService.salvarCaixa(venda).subscribe({
      next: (response: number) => {
        this.vendaId = response; // mantém o mesmo id para atualizações subsequentes
        // Persiste o ID da venda em andamento, para sobrescrever caso o usuário saia e retorne
        localStorage.setItem('pdv.vendaId', String(this.vendaId));
        this.atualizarEstadoFinalizacao();
        // Exibe toast de sucesso
        this.isSaving = false; // encerra buffering assim que o toast for apresentado
        this.saveToastText = 'Venda salva com sucesso. ID ' + this.vendaId + ' atualizada.';
        this.showSaveToast = true;
        setTimeout(() => (this.showSaveToast = false), 2000);
      },
      error: () => {
        // Garante que o buffering termine mesmo em caso de erro
        this.isSaving = false;
      }
    });
  }

  finalizarVenda(): void {
    if (!this.finalizarHabilitado || this.isFinalizandoVenda) return;
    this.isFinalizandoVenda = true;
    this.overlayCarregandoPdv = true;
    this.overlayTextoPdv = 'Finalizando venda...';
    this.pdvService.finalizarVenda(this.vendaId).subscribe({
      next: () => {
        // Prepara dados do modal de sucesso ANTES de limpar
        this.lastVendaResumo = {
          id: this.vendaId,
          totalQuantidade: this.totalQuantidade,
          totalValor: this.totalValor
        };
        // Resumo por item da venda (nome, quantidade e subtotal)
        this.lastVendaItensResumo = (this.caixa || []).map((it) => {
          const prod = this.getProdutoById(it.idProduto);
          const nome = prod?.nome ?? '';
          const precoUnit = prod ? this.obterPreco(prod) : 0;
          return {
            nome,
            quantidade: it.quantidade,
            subtotal: precoUnit * it.quantidade
          };
        });
        // Atualiza saldo no perfil (memória e storage) com base no total da venda
        const u = this.authService.getUsuarioLogado();
        try {
          const saldoAtual = Number(u?.saldo || 0);
          const novoSaldo = saldoAtual + (this.totalValor || 0);
          this.authService.adicionarUsuarioLogado({
            ...u,
            saldo: novoSaldo
          } as any);
        } catch {}

        // limpa caixa após finalizar
        this.caixa = [];
        this.vendaId = 0;
        this.recalcularTotais();
        this.finalizarHabilitado = false; // nova venda começará com novo id
        // Abre modal de sucesso via TemplateRef
        if (this.modalVendaSucessoTmpl) {
          const ref = this.modalService.open(this.modalVendaSucessoTmpl, { size: 'lg' });
          const onClose = () => {
            this.saveToastText = 'Saldo atualizado';
            this.showSaveToast = true;
            setTimeout(() => {
              this.showSaveToast = false;
              window.location.reload();
            }, 1000);
          };
          ref.closed.subscribe(onClose);
          ref.dismissed.subscribe(onClose);
        }
        // Atualiza a lista de histórico após finalizar
        this.pdvService.listarHistorico().subscribe({
          next: (lista) => {
            this.historicoVendas = lista ?? [];
            this.aplicarFiltroHistorico();
          }
        });
        // Recarrega o estoque/quantidades da lista de produtos para refletir o backend
        const idUsuario = this.authService.getUsuarioLogado().idUsuario;
        this.produtoService.listarProdutos(0, 100, idUsuario).subscribe({
          next: (resp: any) => {
            this.produtos = resp?.content ?? [];
            this.aplicarFiltro(); // reaplica filtro atual
            this.totalRecords = this.produtosFiltrados.length;
            this.currentPage = 0;
          }
        });
        // Limpa venda em andamento persistida, pois foi finalizada
        localStorage.removeItem('pdv.vendaId');
        this.isFinalizandoVenda = false;
        this.overlayCarregandoPdv = false;
        this.overlayTextoPdv = '';
      },
      error: () => {
        this.isFinalizandoVenda = false;
        this.overlayCarregandoPdv = false;
        this.overlayTextoPdv = '';
      }
    });
  }

  private atualizarEstadoFinalizacao(): void {
    // Regra: não reiniciar a venda quando o caixa ficar vazio.
    // Habilita "Finalizar" apenas quando houver itens e já existir vendaId (>0)
    if (this.vendaId > 0) {
      this.finalizarHabilitado = this.caixa.length > 0;
    } else {
      this.finalizarHabilitado = false;
    }
  }

  abrirVisualizacao(venda: VendaCaixaDTO): void {
    this.vendaParaVisualizar = venda;
    this.itensVisualizacao = (venda.itens || []).map((it) => {
      const prod = this.getProdutoById(it.idProduto);
      const precoUnit = prod ? this.obterPreco(prod) : 0;
      return {
        item: it as CaixaItem,
        produto: prod,
        precoUnit,
        subtotal: precoUnit * (it.quantidade || 0)
      };
    });
    if (this.modalVendaSucessoTmpl) {
      // noop; apenas para manter consistência de ViewChild
    }
  }

  onToggleHistorico(v: any): void {
    if (this.selectedHistoricoId === v.id) {
      this.selectedHistoricoId = null;
      this.vendaParaVisualizar = null;
      this.itensVisualizacao = [];
    } else {
      this.selectedHistoricoId = v.id;
      // Reutiliza lógica para calcular itens de visualização
      const vendaDto: VendaCaixaDTO = {
        id: v.id,
        idUsuario: v.idUsuario,
        itens: v.itens || [],
        totalQuantidade: v.totalQuantidade,
        totalValor: v.totalValor
      } as VendaCaixaDTO;
      this.abrirVisualizacao(vendaDto);
    }
  }

  abrirHistorico(): void {
    if (this.modalHistorico) {
      this.modalService.open(this.modalHistorico, { size: 'xl' });
    }
  }

  aplicarFiltroHistorico(): void {
    const termo = (this.historicoFiltro || '').toLowerCase().trim();
    if (!termo) {
      this.historicoVendasFiltrado = this.historicoVendas;
      return;
    }
    // Match por ID de produto (numérico) ou por nome do produto (texto) em qualquer item das vendas
    const termoNumero = Number(termo);
    const buscarPorIdProduto = !isNaN(termoNumero);
    this.historicoVendasFiltrado = (this.historicoVendas || []).filter((v) => {
      const itens = v.itens || [];
      return itens.some((it: any) => {
        const prod = this.getProdutoById(it.idProduto);
        if (buscarPorIdProduto) {
          return it.idProduto === termoNumero;
        }
        const nome = (prod?.nome || '').toLowerCase();
        return nome.includes(termo);
      });
    });
  }

  private recalcularVisualizacao(): void {
    if (!this.vendaParaVisualizar) return;
    this.itensVisualizacao = (this.vendaParaVisualizar.itens || []).map((it) => {
      const prod = this.getProdutoById(it.idProduto);
      const precoUnit = prod ? this.obterPreco(prod) : 0;
      return {
        item: it as CaixaItem,
        produto: prod,
        precoUnit,
        subtotal: precoUnit * (it.quantidade || 0)
      };
    });
  }

  visualizarHistorico(v: any): void {
    // converte o registro do histórico para o formato esperado (se necessário)
    // assume que v já possui os campos id, idUsuario, itens, totalQuantidade, totalValor
    this.vendaParaVisualizar = {
      id: v.id,
      idUsuario: v.idUsuario,
      itens: v.itens || [],
      totalQuantidade: v.totalQuantidade,
      totalValor: v.totalValor
    } as VendaCaixaDTO;
    this.recalcularVisualizacao();
    if (this.modalVisualizarVenda) {
      this.modalService.open(this.modalVisualizarVenda, { size: 'lg' });
    }
  }

  @ViewChild('modalConfirmDelete') modalConfirmDelete!: TemplateRef<any>;
  vendaParaExcluir: any | null = null;

  abrirModalExcluir(v: any): void {
    if (!v?.id) return;
    this.vendaParaExcluir = v;
    this.modalService.open(this.modalConfirmDelete, { size: 'sm' });
  }

  confirmarExclusao(modalRef: any): void {
    if (!this.vendaParaExcluir?.id) { modalRef.dismiss(); return; }
    const id = this.vendaParaExcluir.id;
    this.isDeletingHistorico = true;
    this.overlayCarregandoPdv = true;
    this.overlayTextoPdv = 'Excluindo registro...';
    this.pdvService.excluirHistorico(id).subscribe({
      next: () => {
        // Remove localmente e re-aplica filtro para refletir na UI
        this.historicoVendas = this.historicoVendas.filter(h => h.id !== id);
        this.aplicarFiltroHistorico();
        this.vendaParaExcluir = null;
        modalRef.close();
        // Recarrega do servidor para garantir consistência
        this.pdvService.listarHistorico().subscribe({
          next: (lista) => {
            this.historicoVendas = lista ?? [];
            this.aplicarFiltroHistorico();
            this.isDeletingHistorico = false;
            this.overlayCarregandoPdv = false;
            this.overlayTextoPdv = '';
          }
        });
      },
      error: () => {
        this.pdvService.listarHistorico().subscribe({
          next: (lista) => this.historicoVendas = lista ?? []
        });
        this.vendaParaExcluir = null;
        modalRef.dismiss();
        this.isDeletingHistorico = false;
        this.overlayCarregandoPdv = false;
        this.overlayTextoPdv = '';
      }
    });
  }

  // Seleção múltipla no histórico
  isTodosHistoricoSelecionados(): boolean {
    if (!this.historicoVendasFiltrado || this.historicoVendasFiltrado.length === 0) return false;
    return this.historicoVendasFiltrado.every(v => this.selecionadosHistoricoVendasIds.has(v.id || -1));
  }

  onToggleSelecionarTodosHistorico(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const checked = !!input?.checked;
    if (!this.historicoVendasFiltrado) return;
    if (checked) {
      this.historicoVendasFiltrado.forEach(v => this.selecionadosHistoricoVendasIds.add(v.id || -1));
    } else {
      this.selecionadosHistoricoVendasIds.clear();
    }
  }

  onToggleCheckboxHistoricoVenda(v: any, event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const checked = !!input?.checked;
    const id = v?.id ?? null;
    if (!id) return;
    if (checked) this.selecionadosHistoricoVendasIds.add(id); else this.selecionadosHistoricoVendasIds.delete(id);
  }

  excluirSelecionadosHistoricoVenda(): void {
    const ids = Array.from(this.selecionadosHistoricoVendasIds.values());
    if (ids.length === 0) return;
    this.isDeletingHistorico = true;
    this.overlayCarregandoPdv = true;
    this.overlayTextoPdv = 'Excluindo registros selecionados...';
    this.pdvService.excluirMultiHistoricos(ids).subscribe({
      next: () => {
        this.historicoVendas = this.historicoVendas.filter(v => !this.selecionadosHistoricoVendasIds.has(v.id || -1));
        this.aplicarFiltroHistorico();
        this.selecionadosHistoricoVendasIds.clear();
        this.isDeletingHistorico = false;
        this.overlayCarregandoPdv = false;
        this.overlayTextoPdv = '';
      },
      error: () => {
        this.isDeletingHistorico = false;
        this.overlayCarregandoPdv = false;
        this.overlayTextoPdv = '';
      }
    });
  }

  abrirModalExcluirSelecionadosVendas(): void {
    if (this.modalConfirmDeleteMultiVendas) {
      this.modalService.open(this.modalConfirmDeleteMultiVendas, { size: 'sm' });
    }
  }

  confirmarExclusaoSelecionadosVendas(modalRef: any): void {
    modalRef.close();
    this.excluirSelecionadosHistoricoVenda();
  }
}


