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
import {Fornecedor} from '../../model/fornecedor';
import {FornecedorService} from '../../service/fornecedor/fornecedor.service';
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';

@Component({
  selector: 'app-listar-fornecedor',  // Corrigido para 'app-produto-list', sem a barra inicial
  templateUrl: './listar-fornecedor.component.html',
  styleUrls: ['./listar-fornecedor.component.css'],
  imports: [
    NgForOf,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    NgIf,
    NgxPaginationModule,
    MatPaginatorModule,
    NgOptimizedImage
  ]
})

export class ListarFornecedorComponent implements OnInit {

  listaFornecedores!: FornecedorDTO[];

  // Variáveis de paginação
  currentPage: number = 0;
  pageSize: number = 10;
  totalRecords: number = 0;

  private modalService: NgbModal = new NgbModal();
  @ViewChild('searchBar') searchBar!: ElementRef
  @ViewChild('modalAvisoToken') modalAvisoToken!: ElementRef
  isMobileOrTablet: boolean = false;
  fornecedorExcluido!: FornecedorDTO;

  constructor(private fornecedorService: FornecedorService,
              private authService: AuthService, private deviceService: DeviceService) { }

  ngOnInit() {
    this.fornecedorService.listarFornecedores(this.currentPage, this.pageSize).subscribe(data => {
      //const usuarioLogadoId = this.authService.getUsuarioLogado().id;
      this.listaFornecedores = data.content
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
    });

    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });
  }

  // Função para deletar um produto através do id. Chama o endpoint, e a msg de sucesso
  deletarFornecedor(modalDeletar: any, id: number, fornecedor: FornecedorDTO) {
    this.fornecedorService.deletarFornecedor(id).subscribe({
      next: (response) => {
        if (response.status === 200) {
          this.fornecedorExcluido = fornecedor;
          this.abrirTelaExclusao(modalDeletar);
        }
      }
    });
    this.atualizarLista();
  }

  /* Função para atualizar um produto através do id
  atualizarProduto(janelaEditar: any, id: number, produto: Produto) {
    this.produtoService.atualizarProduto(id, produto, this.imagemFile).subscribe({
      next: (produtoRetornado: Produto) => {
        this.produtoAtualizar = produtoRetornado
        this.calcularValores(this.produtoAtualizar);
        this.abrirTelaEdicao(janelaEditar)
      }
    })
  }*/

  // Função que atualiza a lista de produtos
  atualizarLista(): void {
    this.fornecedorService.listarFornecedores(this.currentPage, this.pageSize).subscribe(data => {
      this.listaFornecedores = data.content
      this.totalRecords = data.totalElements; // Atualiza o total de registros exibidos
    });
  }

  trocarPagina(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.atualizarLista();
  }

/*
  // Função que abre o modal - Janela de edição de produto
  abrirTelaEdicao(janelaEditar: any) {
    this.modalService.open(janelaEditar, { size: 'lg', backdrop: 'static' });
  } */

  // Função que abre o modal - Janela de exclusão de produto
  abrirTelaExclusao(modalExcluir: any) {
    this.modalService.open(modalExcluir);
    this.atualizarLista();
  }
/*
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
  } */

}
