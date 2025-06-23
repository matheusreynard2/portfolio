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
        this.cnpjBuscado = false;
        this.cepBuscado = false;
        this.isLoading = false;
        this.isLoadingCNPJ = false;
        this.mensagemErro = '';
        
        // Inicializar cnpjEmpresa com o CNPJ dos dados da empresa se existir
        this.cnpjEmpresa = this.fornecedorAtualizar.dadosEmpresa?.cnpj || '';
        
        this.abrirTelaEdicao(modalEditar);
      },
      error: (error) => {
        this.fornecedorAtualizar = fornecedor;
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
    // Validações obrigatórias
    if (!this.fornecedorAtualizar.nome || !this.fornecedorAtualizar.nome.trim()) {
      this.mensagemErro = "O nome do fornecedor é obrigatório.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    if (!this.fornecedorAtualizar.enderecoFornecedor.cep || !this.fornecedorAtualizar.enderecoFornecedor.cep.trim()) {
      this.mensagemErro = "O CEP é obrigatório.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Validar se o CEP tem pelo menos 8 dígitos
    const cepLimpo = this.fornecedorAtualizar.enderecoFornecedor.cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      this.mensagemErro = "CEP deve ter 8 dígitos.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Validar se logradouro está preenchido (campo básico de endereço)
    if (!this.fornecedorAtualizar.enderecoFornecedor.logradouro || !this.fornecedorAtualizar.enderecoFornecedor.logradouro.trim()) {
      this.mensagemErro = "O logradouro é obrigatório.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Validar se cidade está preenchida (campo básico de endereço)
    if (!this.fornecedorAtualizar.enderecoFornecedor.localidade || !this.fornecedorAtualizar.enderecoFornecedor.localidade.trim()) {
      this.mensagemErro = "A cidade é obrigatória.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Validar se UF está preenchida (campo básico de endereço)
    if (!this.fornecedorAtualizar.enderecoFornecedor.uf || !this.fornecedorAtualizar.enderecoFornecedor.uf.trim()) {
      this.mensagemErro = "A UF é obrigatória.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Dados da empresa são opcionais - não há validação obrigatória
    // Se não há dados de empresa, pode prosseguir normalmente

    this.fornecedorService.atualizarFornecedor(
      this.fornecedorAtualizar.id, 
      this.authService.getUsuarioLogado().idUsuario,
      this.fornecedorAtualizar
    ).subscribe({
      next: (fornecedorAtualizado: FornecedorDTO) => {
        modal.close();
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
    if (!cep) {
      this.mensagemErro = "Por favor, digite um CEP.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Remover caracteres não numéricos do CEP
    const cepFormatado = cep.replace(/\D/g, '');

    if (cepFormatado.length !== 8) {
      this.mensagemErro = "CEP inválido. Por favor, digite um CEP com 8 dígitos.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

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
        const nrResidenciaAtual = this.fornecedorAtualizar.enderecoFornecedor?.nrResidencia;
        
        this.fornecedorAtualizar.enderecoFornecedor = {
          ...endereco,
          id: idOriginalEndereco, // Preservar o ID original para evitar criação de novo registro
          nrResidencia: nrResidenciaAtual || '', // Preservar número da residência atual
          // Garantir que nenhum campo seja undefined
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
    if (!cnpj) {
      this.mensagemErro = "Por favor, digite um CNPJ.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Remover caracteres não numéricos do CNPJ
    const cnpjLimpo = cnpj.replace(/\D/g, '');

    if (cnpjLimpo.length !== 14) {
      this.mensagemErro = "CNPJ inválido. Por favor, digite um CNPJ com 14 dígitos.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

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
    // PRESERVAR O ID ORIGINAL: Resetar dadosEmpresa mas manter o ID existente
    const idOriginal = this.fornecedorAtualizar.dadosEmpresa?.id;
    this.fornecedorAtualizar.dadosEmpresa = {
      id: idOriginal, // Preservar o ID original
      cnpj: '',
      razaoSocial: '',
      nomeFantasia: '',
      situacaoCadastral: '',
      cnaeFiscal: '',
      porte: '',
      capitalSocial: '',
      dataInicioAtividade: '',
      descricaoNaturezaJuridica: '',
      email: '',
      dddTelefone1: '',
      telefone1: '',
      dddTelefone2: '',
      telefone2: '',
      bairro: '',
      cep: '',
      complemento: '',
      logradouro: '',
      municipio: '',
      numero: '',
      pais: '',
      uf: '',
      codigoNaturezaJuridica: '',
      dataSituacaoCadastral: '',
      cnaesSecundarios: [],
      qsa: []
    };
  }

  limparCamposCEP() {
    this.cepBuscado = false;
    // PRESERVAR O ID ORIGINAL: Resetar enderecoFornecedor mas manter o ID existente
    const idOriginalEndereco = this.fornecedorAtualizar.enderecoFornecedor?.id;
    this.fornecedorAtualizar.enderecoFornecedor = {
      id: idOriginalEndereco, // Preservar o ID original
      nrResidencia: '',
      cep: '',
      logradouro: '',
      complemento: '',
      unidade: '',
      bairro: '',
      localidade: '',
      uf: '',
      estado: '',
      regiao: '',
      ibge: '',
      gia: '',
      ddd: '',
      siafi: '',
      erro: ''
    };
  }

}
