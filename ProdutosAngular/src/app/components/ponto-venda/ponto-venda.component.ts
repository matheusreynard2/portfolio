import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule, NgbModal, NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { ProdutoDTO } from '../../model/dto/ProdutoDTO';
import { ProdutoService } from '../../service/produto/produto.service';
import { AuthService } from '../../service/auth/auth.service';
import { PontoVendaService } from '../../service/ponto-venda/ponto-venda.service';
import { CaixaItemDTO } from '../../model/dto/CaixaItemDTO';
import { VendaCaixaDTO } from '../../model/dto/VendaCaixaDTO';

interface CaixaItem extends CaixaItemDTO {}

@Component({
  selector: 'app-ponto-venda',
  standalone: true,
  imports: [CommonModule, NgIf, NgForOf, FormsModule, HttpClientModule, NgbModule, NgbToastModule],
  templateUrl: './ponto-venda.component.html',
  styleUrls: ['./ponto-venda.component.css']
})
export class PontoVendaComponent implements OnInit {
  @ViewChild('modalVendaSucessoTmpl') modalVendaSucessoTmpl!: TemplateRef<any>;
  @ViewChild('modalVisualizarVenda') modalVisualizarVenda!: TemplateRef<any>;
  vendaId: number = 0;
  produtos: ProdutoDTO[] = [];
  produtosFiltrados: ProdutoDTO[] = [];
  caixa: CaixaItem[] = [];
  totalQuantidade = 0;
  totalValor = 0;
  salvarHabilitado = true;
  finalizarHabilitado = false; // após primeiro salvar, permanece habilitado até finalizar
  filtro = '';
  // Modo de preço selecionado: valorInicial | valorTotalDesc | valorTotalFrete
  modoPreco: 'valorInicial' | 'valorTotalDesc' | 'somaTotalValores' = 'valorInicial';
  historicoVendas: any[] = [];
  vendaParaVisualizar: VendaCaixaDTO | null = null;
  itensVisualizacao: { item: CaixaItem; produto?: ProdutoDTO; precoUnit: number; subtotal: number }[] = [];
  // Toast de salvar
  showSaveToast: boolean = false;
  saveToastText: string = '';
  // Modal de sucesso ao finalizar
  lastVendaResumo: { id: number; totalQuantidade: number; totalValor: number } | null = null;

  constructor(
    private produtoService: ProdutoService,
    private authService: AuthService,
    private pdvService: PontoVendaService,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.pdvService.acessarPaginaPdv().subscribe();
    const idUsuario = this.authService.getUsuarioLogado().idUsuario;
    // Carrega lista de produtos do usuário
    this.produtoService.listarProdutos(0, 100, idUsuario).subscribe({
      next: (resp: any) => {
        this.produtos = resp?.content ?? [];
        this.produtosFiltrados = this.produtos;
        // Carrega histórico após carregar a tela
        this.pdvService.listarHistorico().subscribe({
          next: (lista) => this.historicoVendas = lista ?? []
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
  }

  selecionarModoPreco(modo: 'valorInicial' | 'valorTotalDesc' | 'somaTotalValores'): void {
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
    return produto.somaTotalValores ?? fallback;
  }

  adicionarNoCaixa(produto: ProdutoDTO): void {
    const existente = this.caixa.find(i => i.idProduto === produto.id);
    if (existente) {
      existente.quantidade += 1;
    } else {
      this.caixa.push({ idProduto: produto.id!, quantidade: 1 , tipoPreco: this.modoPreco});
    }
    this.recalcularTotais();
    // estado de finalização será recalculado
  }

  removerDoCaixa(item: CaixaItem): void {
    const idx = this.caixa.indexOf(item);
    if (idx >= 0) {
      this.caixa.splice(idx, 1);
      this.recalcularTotais();
      // estado de finalização será recalculado
    }
  }

  alterarQuantidade(item: CaixaItem, delta: number): void {
    item.quantidade = Math.max(1, item.quantidade + delta);
    this.recalcularTotais();
    // estado de finalização será recalculado
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
        this.atualizarEstadoFinalizacao();
        // Exibe toast de sucesso
        this.saveToastText = 'Venda salva com sucesso. ID ' + this.vendaId + ' atualizada.';
        this.showSaveToast = true;
        setTimeout(() => (this.showSaveToast = false), 2000);
      }
    });
  }

  finalizarVenda(): void {
    if (!this.finalizarHabilitado) return;
    this.pdvService.finalizarVenda(this.vendaId).subscribe({
      next: () => {
        // Prepara dados do modal de sucesso ANTES de limpar
        this.lastVendaResumo = {
          id: this.vendaId,
          totalQuantidade: this.totalQuantidade,
          totalValor: this.totalValor
        };
        // limpa caixa após finalizar
        this.caixa = [];
        this.vendaId = 0;
        this.recalcularTotais();
        this.finalizarHabilitado = false; // nova venda começará com novo id
        // Abre modal de sucesso via TemplateRef
        if (this.modalVendaSucessoTmpl) {
          this.modalService.open(this.modalVendaSucessoTmpl, { size: 'lg' });
        }
        // Atualiza a lista de histórico após finalizar
        this.pdvService.listarHistorico().subscribe({
          next: (lista) => this.historicoVendas = lista ?? []
        });
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
}


