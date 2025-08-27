import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProdutoService } from '../../service/produto/produto.service';
import { AuthService } from '../../service/auth/auth.service';
import { ProdutoDTO } from '../../model/dto/ProdutoDTO';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-comprar-produtos',
  templateUrl: './comprar-produtos.component.html',
  styleUrls: ['./comprar-produtos.component.css'],
  imports: [CommonModule, FormsModule, NgIf, NgForOf, CurrencyPipe, MatPaginatorModule]
})
export class ComprarProdutosComponent implements OnInit {
  private todosProdutos: ProdutoDTO[] = [];
  produtosFiltrados: ProdutoDTO[] = [];
  carregando = false;
  erro: string | null = null;

  produtoSelecionado: ProdutoDTO | null = null;
  quantidadeCompra: number | null = null;
  precoCompra: number | null = null;

  // Filtros e paginação
  searchNome: string = '';
  searchFornecedor: string = '';
  totalRecords: number = 0;
  currentPage: number = 0;
  pageSize: number = 5;

  constructor(private produtoService: ProdutoService, private auth: AuthService) {}

  ngOnInit(): void {
    const idUsuario = this.auth.getUsuarioLogado()?.idUsuario;
    if (!idUsuario) {
      this.todosProdutos = [];
      this.produtosFiltrados = [];
      return;
    }
    this.carregarProdutos(idUsuario);
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

  selecionarProduto(produto: ProdutoDTO): void {
    this.produtoSelecionado = produto;
    // zera campos ao trocar seleção
    this.quantidadeCompra = null;
    // Preenche o preço de compra com o valor unitário do produto (editável)
    this.precoCompra = (produto?.valorInicial ?? 0);
  }

  get totalCompra(): number {
    const q = this.quantidadeCompra ?? 0;
    const p = this.precoCompra ?? 0;
    return q * p;
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


