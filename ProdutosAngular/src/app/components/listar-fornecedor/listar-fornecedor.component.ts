import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {NgForOf, NgIf, NgOptimizedImage, CommonModule} from '@angular/common';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {AuthService} from '../../service/auth/auth.service';
import {DeviceService} from '../../service/device/device.service';
import {FornecedorService} from '../../service/fornecedor/fornecedor.service';
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';
import {NgxMaskDirective, provideNgxMask} from 'ngx-mask';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';

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
    NgOptimizedImage,
    CommonModule,
    NgxMaskDirective,
    MatTableModule,
    MatSortModule
  ],
  providers: [provideNgxMask()]
})

export class ListarFornecedorComponent implements OnInit {

  listaFornecedores!: FornecedorDTO[];
  fornecedorAtualizar!: FornecedorDTO;
  fornecedorExcluido!: FornecedorDTO;

  // Variáveis de paginação
  currentPage: number = 0;
  pageSize: number = 10;
  totalRecords: number = 0;

  @ViewChild('searchBar') searchBar!: ElementRef
  @ViewChild('modalAviso') modalAviso!: any;
  isMobileOrTablet: boolean = false;

  constructor(
    private fornecedorService: FornecedorService,
    private authService: AuthService,
    private deviceService: DeviceService,
    private modalService: NgbModal,
    private router: Router
  ) { }

  ngOnInit() {
    this.atualizarLista();
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });
  }

  // Função para atualizar um fornecedor
  atualizarFornecedor(modalEditar: any, id: number, fornecedor: FornecedorDTO) {
    this.fornecedorService.buscarFornecedorPorId(id).subscribe({
      next: (fornecedorCompleto: FornecedorDTO) => {
        this.fornecedorAtualizar = fornecedorCompleto;
        this.abrirTelaEdicao(modalEditar);
      },
      error: (error) => {
        console.error('Erro ao buscar fornecedor:', error);
        this.fornecedorAtualizar = fornecedor;
        this.abrirTelaEdicao(modalEditar);
      }
    });
  }

  // Função que abre o modal de edição
  abrirTelaEdicao(modalEditar: any) {
    this.modalService.open(modalEditar, { size: 'lg', backdrop: 'static' });
  }

  // Função chamada ao salvar as alterações
  onSubmitSalvar(modal: any) {
    this.fornecedorService.atualizarFornecedor(
      this.fornecedorAtualizar.id, 
      this.authService.getUsuarioLogado().idUsuario,
      this.fornecedorAtualizar
    ).subscribe({
      next: () => {
        modal.close();
        this.atualizarLista();
        this.modalService.open(this.modalAviso, {size: 'sm'});
      },
      error: (error) => {
        console.error('Erro ao atualizar fornecedor:', error);
      }
    });
  }

  // Função para deletar um fornecedor
  deletarFornecedor(modalDeletar: any, id: number, fornecedor: FornecedorDTO) {
    this.fornecedorService.deletarFornecedor(id, this.authService.getUsuarioLogado().idUsuario).subscribe({
      next: (response) => {
        if (response.status === 200) {
          this.fornecedorExcluido = fornecedor;
          this.abrirTelaExclusao(modalDeletar);
        }
      }
    });
    this.atualizarLista();
  }

  // Função que abre o modal de exclusão
  abrirTelaExclusao(modalExcluir: any) {
    this.modalService.open(modalExcluir);
    this.atualizarLista();
  }

  // Função que atualiza a lista de fornecedores
  atualizarLista(): void {
    this.fornecedorService.listarFornecedores(
      this.currentPage, 
      this.pageSize, 
      this.authService.getUsuarioLogado().idUsuario
    ).subscribe(data => {
      this.listaFornecedores = data.content;
      this.totalRecords = data.totalElements;
    });
  }

  // Função para trocar de página
  trocarPagina(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.atualizarLista();
  }

}
