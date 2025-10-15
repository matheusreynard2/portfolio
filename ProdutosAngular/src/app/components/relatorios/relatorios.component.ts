import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CurrencyPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import { AuthService } from '../../service/auth/auth.service';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { TransacaoEconomicaDTO } from '../../model/dto/TransacaoEconomicaDTO';
import { TransacaoItemDTO } from '../../model/dto/TransacaoItemDTO';
import { CompraDTO } from '../../model/dto/CompraDTO';
import { RelatoriosService, ExportarRelatorioPayload } from '../../service/relatorios/relatorios.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-relatorios',
  standalone: true,
  imports: [CommonModule, NgIf, NgForOf, CurrencyPipe, DatePipe, MatPaginatorModule],
  providers: [CurrencyPipe, DatePipe],
  templateUrl: './relatorios.component.html',
  styleUrl: './relatorios.component.css'
})
export class RelatoriosComponent implements OnInit {
  transacoes: TransacaoEconomicaDTO[] = [];
  sortColumn: 'tipo' | 'id' | 'data' | 'totalQuantidade' | 'totalValor' | 'saldoApos' = 'data';
  sortDirection: 'asc' | 'desc' = 'desc';
  // Saldo atual do usuário (pós todas as transações já consolidadas)
  saldoAtualUsuario = 0;
  // Saldo base anterior ao primeiro registro do histórico (derivado)
  saldoBaseHistorico = 0;
  pageSize = 10;
  currentPage = 0;
  totalRecords = 0;
  private cacheTransacoesOrdenadas: TransacaoEconomicaDTO[] = [];
  private cacheSortSignature = '';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private currency: CurrencyPipe,
    private date: DatePipe,
    private relatoriosService: RelatoriosService
  ) {}

  ngOnInit(): void {
    const usuario = this.auth.getUsuarioLogado();
    this.saldoAtualUsuario = usuario?.saldo || 0;
    const idUsuario = usuario?.idUsuario;
    if (!idUsuario) return;

    // Busca histórico de compras e vendas e mescla em uma linha do tempo local
    Promise.all([
      this.http.get<any[]>(`${environment.API_URL}/compras/listarHistoricoCompras/${idUsuario}`).toPromise(),
      this.http.get<any[]>(`${environment.API_URL}/pdv/listarHistorico`).toPromise(),
    ]).then(([compras, vendas]) => {
      const mapCompras = (compras || []).map((c: any) => this.mapCompra(c));
      const mapVendas = (vendas || []).map((v: any) => this.mapVenda(v));
      const todas = [...mapCompras, ...mapVendas]
        .sort((a, b) => new Date(a.data).getTime() - new Date(b.data).getTime());
      this.transacoes = todas;
      this.totalRecords = this.transacoes.length;
      this.recalcularSaldoBaseHistorico();
    }).catch(() => {});
  }
  
  private mapCompra(transacao: any): TransacaoEconomicaDTO {
    const itens: TransacaoItemDTO[] = (transacao?.compras || []).map((compra: CompraDTO) => ({
      idProduto: compra?.produto?.id,
      nome: compra?.produto?.nome,
      descricao: compra?.produto?.descricao,
      precoUnit: compra?.valorUnitarioCompra || 0,
      quantidade: compra?.quantidadeComprada || 0,
      subtotal: compra?.valorTotalCompra || 0,
    }));
    return {
      id: transacao?.id || 0,
      tipo: 'COMPRA',
      data: transacao?.dataCompra,
      totalQuantidade: transacao?.quantidadeTotal || 0,
      totalValor: transacao?.valorTotal || 0,
      itens,
    };
  }

  private mapVenda(transacao: any): TransacaoEconomicaDTO {
    const itens: TransacaoItemDTO[] = (transacao?.itens || []).map((venda: any) => ({
      idProduto: venda?.idProduto,
      nome: '',
      descricao: '',
      precoUnit: 0, // Preço unit é calculado no front no PDV, aqui mantemos subtotal apenas
      quantidade: venda?.quantidade || 0,
      subtotal: 0,
    }));
    return {
      id: Number(transacao?.id || 0),
      tipo: 'VENDA',
      data: transacao?.dataVenda,
      totalQuantidade: transacao?.totalQuantidade || 0,
      totalValor: transacao?.totalValor || 0,
      itens,
    };
  }

  getSaldoAposTransacao(index: number): number {
    const anteriores = this.transacoes.slice(0, index + 1);
    // Começa no saldo base (antes do primeiro registro do histórico)
    let saldo = this.saldoBaseHistorico;
    // Assumimos que a ordem está por data ascendente
    for (const t of anteriores) {
      const valor = Number(t.totalValor) || 0;
      saldo += t.tipo === 'VENDA' ? valor : -valor;
    }
    return saldo;
  }

  get transacoesPaginadas(): TransacaoEconomicaDTO[] {
    // Exibição em ordem definida pela ordenação escolhida
    const inicio = this.currentPage * this.pageSize;
    const listaOrdenada = this.obterTransacoesOrdenadas();
    return listaOrdenada.slice(inicio, inicio + this.pageSize);
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  setSort(column: 'tipo' | 'id' | 'data' | 'totalQuantidade' | 'totalValor' | 'saldoApos'): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = column === 'data' ? 'desc' : 'asc';
    }
    this.cacheSortSignature = '';
    this.currentPage = 0;
  }

  private compararTransacoes(a: TransacaoEconomicaDTO, b: TransacaoEconomicaDTO): number {
    let valorA: any;
    let valorB: any;
    switch (this.sortColumn) {
      case 'tipo':
        valorA = a.tipo;
        valorB = b.tipo;
        break;
      case 'id':
        valorA = a.id;
        valorB = b.id;
        break;
      case 'data':
        valorA = new Date(a.data).getTime();
        valorB = new Date(b.data).getTime();
        break;
      case 'totalQuantidade':
        valorA = a.totalQuantidade;
        valorB = b.totalQuantidade;
        break;
      case 'totalValor':
        valorA = Number(a.totalValor) || 0;
        valorB = Number(b.totalValor) || 0;
        break;
      case 'saldoApos':
        valorA = this.getSaldoAposTransacao(this.transacoes.indexOf(a));
        valorB = this.getSaldoAposTransacao(this.transacoes.indexOf(b));
        break;
    }
    if (valorA < valorB) return this.sortDirection === 'asc' ? -1 : 1;
    if (valorA > valorB) return this.sortDirection === 'asc' ? 1 : -1;
    return 0;
  }

  private obterTransacoesOrdenadas(): TransacaoEconomicaDTO[] {
    const assinatura = `${this.sortColumn}_${this.sortDirection}_${this.transacoes.length}`;
    if (this.cacheSortSignature === assinatura) {
      return this.cacheTransacoesOrdenadas;
    }
    this.cacheTransacoesOrdenadas = [...this.transacoes].sort((a, b) => this.compararTransacoes(a, b));
    this.cacheSortSignature = assinatura;
    return this.cacheTransacoesOrdenadas;
  }

  getSaldoAposTransacaoGlobal(indexOnPage: number): number {
    const listaOrdenada = this.cacheTransacoesOrdenadas;
    const globalIndex = this.currentPage * this.pageSize + indexOnPage;
    const transacao = listaOrdenada[globalIndex];
    if (!transacao) {
      return this.saldoBaseHistorico;
    }
    const indiceOriginal = this.transacoes.indexOf(transacao);
    return this.getSaldoAposTransacao(indiceOriginal);
  }

  getSortClass(column: 'tipo' | 'id' | 'data' | 'totalQuantidade' | 'totalValor' | 'saldoApos'): string {
    if (this.sortColumn !== column) return 'inactive';
    return this.sortDirection === 'asc' ? 'asc' : 'desc';
  }

  private recalcularSaldoBaseHistorico(): void {
    const efeitoTotal = (this.transacoes || []).reduce((acumulado, t) => {
      const valor = Number(t.totalValor) || 0;
      const efeito = t.tipo === 'VENDA' ? valor : -valor;
      return acumulado + efeito;
    }, 0);
    // saldoBase = saldoAtual - efeitoTotalDoHistorico
    this.saldoBaseHistorico = (Number(this.saldoAtualUsuario) || 0) - efeitoTotal;
  }

  baixarPdf() {
    const colunas = ['ID', 'TIPO', 'DATA', 'QUANTIDADE', 'VALOR TRANSAÇÃO', 'SALDO FINAL'];
    const listaOrdenada = this.obterTransacoesOrdenadas();
    const totalRegistros = listaOrdenada.length;
    const linhas = listaOrdenada.map((t) => ({
      'ID': t.id,
      'TIPO': t.tipo === 'VENDA' ? 'Venda' : 'Compra',
      'DATA': this.date.transform(t.data, 'dd/MM/yyyy HH:mm', undefined, 'pt-BR'),
      'QUANTIDADE': t.totalQuantidade,
      'VALOR TRANSAÇÃO': this.currency.transform(Number(t.totalValor) || 0, 'BRL', 'symbol', '1.2-2', 'pt-BR'),
      'SALDO FINAL': this.currency.transform(
        this.getSaldoAposTransacao(this.transacoes.indexOf(t)),
        'BRL',
        'symbol',
        '1.2-2',
        'pt-BR'
      )
    }));

    const payload: ExportarRelatorioPayload = {
      titulo: 'Relatório financeiro',
      colunas,
      linhas,
      paisagem: true,
      rodapeDireita: totalRegistros > 0 ? `Total de registros: ${totalRegistros}` : undefined
    };

    this.relatoriosService.exportarPdf(payload).subscribe((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'relatorio-financeiro-pdf.pdf';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
