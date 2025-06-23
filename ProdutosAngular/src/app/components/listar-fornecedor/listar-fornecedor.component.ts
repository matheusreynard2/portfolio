import {Component, ElementRef, OnInit, ViewChild, TemplateRef} from '@angular/core';
import {NgForOf, NgIf, NgOptimizedImage, CommonModule} from '@angular/common';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {AuthService} from '../../service/auth/auth.service';
import {DeviceService} from '../../service/device/device.service';
import { FornecedorService } from '../../service/fornecedor/fornecedor.service';
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';
import {NgxMaskDirective, provideNgxMask} from 'ngx-mask';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { EnderecoFornecedorDTO } from '../../model/dto/EnderecoFornecedorDTO';
import { DadosEmpresaDTO } from '../../model/dto/DadosEmpresaDTO';
import { EmptyDadosEmpresa } from '../../model/templates/EmptyDadosEmpresa';
import { EmptyEnderecoFornecedor } from '../../model/templates/EmptyEnderecoFornecedor';

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

  @ViewChild('modalMsgExcluir') modalMsgExcluir!: TemplateRef<any>;
  @ViewChild('modalEditar') modalEditar!: TemplateRef<any>;
  @ViewChild('modalAviso') modalAviso!: TemplateRef<any>;
  @ViewChild('modalMsgErro') modalMsgErro!: TemplateRef<any>;
  @ViewChild('modalMsgAviso') modalMsgAviso!: TemplateRef<any>;
  

  listaFornecedores!: FornecedorDTO[];
  fornecedorAtualizar!: FornecedorDTO;
  fornecedorExcluido!: FornecedorDTO;
  emptyDadosEmpresa!: EmptyDadosEmpresa;
  emptyEnderecoFornecedor!: EmptyEnderecoFornecedor;

  // Variáveis de paginação
  currentPage: number = 0;
  pageSize: number = 10;
  totalRecords: number = 0;

  // Variáveis para busca de CNPJ e CEP
  cnpjEmpresa: string = '';
  isLoadingCNPJ: boolean = false;
  isLoading: boolean = false;
  cnpjBuscado: boolean = false;
  cepBuscado: boolean = false;
  mensagemErro: string = '';

  @ViewChild('searchBar') searchBar!: ElementRef
  isMobileOrTablet: boolean = false;

  constructor(
    private fornecedorService: FornecedorService,
    private authService: AuthService,
    private deviceService: DeviceService,
    private modalService: NgbModal,
    private router: Router,
    private geolocalizacaoService: GeolocalizacaoService
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
      next: (fornecedor: FornecedorDTO) => {
        this.fornecedorAtualizar = fornecedor;
        
        // Resetar variáveis de busca
        this.cnpjEmpresa = ''; // Limpar o campo de busca de CNPJ
        this.cnpjBuscado = false;
        this.cepBuscado = false;
        this.isLoading = false;
        this.isLoadingCNPJ = false;
        this.mensagemErro = '';

        
        this.abrirTelaEdicao(modalEditar);
      }
    });
  }

  // Função que abre o modal de edição
  abrirTelaEdicao(modalEditar: any) {
    this.modalService.open(modalEditar, { size: 'xl', backdrop: 'static' });
  }

  // Função para salvar as alterações do fornecedor
  onSubmitSalvar(modal: any) {
    // Validar se o CEP tem pelo menos 8 dígitos
    const cepLimpo = this.fornecedorAtualizar.enderecoFornecedor.cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      this.mensagemErro = "CEP deve ter 8 dígitos.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    this.fornecedorService.atualizarFornecedor(
      this.fornecedorAtualizar.id, 
      this.authService.getUsuarioLogado().idUsuario,
      this.fornecedorAtualizar
    ).subscribe({
      next: (fornecedorAtualizado: FornecedorDTO) => {
        modal.close();
        this.atualizarLista(); // Atualizar a lista após salvar
        this.modalService.open(this.modalAviso);
      },
      error: (error) => {
        console.error('Erro ao atualizar fornecedor:', error);
        this.mensagemErro = "Erro ao atualizar fornecedor. Tente novamente.";
        this.modalService.open(this.modalMsgAviso);
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
      },
      error: (error) => {
        // Tratamento do erro 400 FORNECEDOR POSSUI PRODUTO RELACIONADO
        if (error.status === 400) {
          const errorBody = error.error;
          if (errorBody.erro === 'FORNECEDOR_POSSUI_PRODUTO') {
            setTimeout(() => {
              this.modalService.open(this.modalMsgErro, {size: 'lg'});
            }, 100);
          }
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
  // Métodos de busca copiados do componente de adicionar fornecedor
  buscarEnderecoPorCep(cep: string): void {

    // Remover caracteres não numéricos do CEP
    const cepFormatado = cep.replace(/\D/g, '');

    // Ativar loading
    this.isLoading = true;

    // Chama o serviço que se comunica com o backend
    this.geolocalizacaoService.obterEnderecoViaCEP(cepFormatado).subscribe({
      next: (endereco: EnderecoFornecedorDTO) => {
        // Desativar loading
        this.isLoading = false;
        
        // Marcar que o CEP foi buscado com sucesso
        this.cepBuscado = true;
        
        // Verifica APENAS o campo erro, não verificar o CEP novamente
        if (endereco.erro === "true") {
          this.cepBuscado = false;
          this.mensagemErro = "CEP não encontrado.";
          this.modalService.open(this.modalMsgAviso);
          return;
        }

        // PRESERVAR O ID ORIGINAL: Atualizar apenas os dados, mantendo o ID existente
        const idOriginalEndereco = this.fornecedorAtualizar.enderecoFornecedor?.id;
        
        this.fornecedorAtualizar.enderecoFornecedor = {
          ...endereco,
          id: idOriginalEndereco, // Preservar o ID original para evitar criação de novo registro
          nrResidencia: endereco.nrResidencia || '',
          complemento: endereco.complemento || '',
          unidade: endereco.unidade || '',
          ibge: endereco.ibge || '',
          gia: endereco.gia || '',
          siafi: endereco.siafi || ''
        };
      },
      error: (error: any) => {
        // Desativar loading
        this.isLoading = false;
        this.cepBuscado = false;
        this.mensagemErro = "Erro ao buscar CEP. Tente novamente.";
        this.modalService.open(this.modalMsgAviso);
      }
    });
  }

  buscarEmpresaPorCNPJ(cnpj: string): void {
    // Remover caracteres não numéricos do CNPJ
    const cnpjLimpo = cnpj.replace(/\D/g, '');

    // Ativar loading
    this.isLoadingCNPJ = true;

    // Chama o serviço que se comunica com o backend
    this.fornecedorService.obterEmpresaViaCNPJ(cnpjLimpo).subscribe({
      next: (empresa: DadosEmpresaDTO) => {
        // Desativar loading
        this.isLoadingCNPJ = false;
        
        // Marcar que o CNPJ foi buscado com sucesso
        this.cnpjBuscado = true;
        
        // PRESERVAR O ID ORIGINAL: Atualizar apenas os dados, mantendo o ID existente
        const idOriginal = this.fornecedorAtualizar.dadosEmpresa?.id;
        this.fornecedorAtualizar.dadosEmpresa = {
          ...empresa,
          id: idOriginal // Preservar o ID original para evitar criação de novo registro
        };
      },
      error: (error: any) => {
        // Desativar loading
        this.isLoadingCNPJ = false;
        this.cnpjBuscado = false;
        this.mensagemErro = "Erro ao buscar CNPJ. Tente novamente.";
        this.modalService.open(this.modalMsgAviso);
      }
    });
  }

  limparCamposCNPJ() {
    this.cnpjEmpresa = '';
    this.cnpjBuscado = false;
    
    const idOriginal = this.fornecedorAtualizar.dadosEmpresa?.id;
    this.fornecedorAtualizar.dadosEmpresa = {
      ...this.emptyDadosEmpresa,
      id: idOriginal
    };
    this.fornecedorAtualizar.dadosEmpresa.id = idOriginal;

  }

  limparCamposCEP() {
    this.cepBuscado = false;
    
    // Limpa todos os campos garantindo que o ID seja preservado
    const idOriginalEndereco = this.fornecedorAtualizar.enderecoFornecedor?.id;
    this.fornecedorAtualizar.enderecoFornecedor = {
      ...this.emptyEnderecoFornecedor,
      id: idOriginalEndereco
    };
    this.fornecedorAtualizar.enderecoFornecedor.id = idOriginalEndereco;
  }

  // Getter para validação de CNPJ (14 dígitos)
  get isCnpjValido(): boolean {
    if (!this.cnpjEmpresa) return false;
    const cnpjLimpo = this.cnpjEmpresa.replace(/\D/g, '');
    return cnpjLimpo.length === 14;
  }

  // Getter para validação de CEP (8 dígitos)
  get isCepValido(): boolean {
    if (!this.fornecedorAtualizar?.enderecoFornecedor?.cep) return false;
    const cepLimpo = this.fornecedorAtualizar.enderecoFornecedor.cep.replace(/\D/g, '');
    return cepLimpo.length === 8;
  }

  // Getter para validação dos campos obrigatórios
  get isCamposObrigatoriosValidos(): boolean {
    if (!this.fornecedorAtualizar) return false;
    
    const nomeValido = this.fornecedorAtualizar.nome.trim();
    const cepValido = this.isCepValido;
    const logradouroValido = this.fornecedorAtualizar.enderecoFornecedor.logradouro?.trim();
    const cidadeValida = this.fornecedorAtualizar.enderecoFornecedor.localidade.trim();
    const ufValida = this.fornecedorAtualizar.enderecoFornecedor.uf.trim();

    return !!(nomeValido && cepValido && logradouroValido && cidadeValida && ufValida);
  }

}
