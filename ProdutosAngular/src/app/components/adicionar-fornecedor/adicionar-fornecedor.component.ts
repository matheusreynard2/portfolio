import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { EnderecoFornecedorDTO } from '../../model/dto/EnderecoFornecedorDTO';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FornecedorService } from '../../service/fornecedor/fornecedor.service';
import { NgOptimizedImage, CommonModule } from '@angular/common';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';
import { AuthService } from '../../service/auth/auth.service';
import { Router } from '@angular/router';
import { DadosEmpresaDTO } from '../../model/dto/DadosEmpresaDTO';

@Component({
  selector: 'adicionar-fornecedor',
  templateUrl: './adicionar-fornecedor.component.html',
  imports: [
    FormsModule,
    NgOptimizedImage,
    ReactiveFormsModule,
    NgxMaskDirective,
    CommonModule
  ],
  providers: [provideNgxMask()],
  styleUrls: ['./adicionar-fornecedor.component.css']
})
export class AdicionarFornecedorComponent implements OnInit {
  @ViewChild('modalMsgAddFornecedor') modalMsgAddFornecedor!: TemplateRef<any>;
  @ViewChild('modalMsgAviso') modalMsgAviso!: TemplateRef<any>;

  nomeFornecedor: string = '';
  nrResidenciaFornecedor: string = '';
  adicionouFornecedor: boolean = false;
  mensagemErro: string = '';
  isLoading: boolean = false; // Variável para controlar o loading

  // Propriedades para o formulário de CNPJ
  cnpjEmpresa: string = '';
  isLoadingCNPJ: boolean = false;

  endereco: EnderecoFornecedorDTO = {
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

  dadosEmpresa: DadosEmpresaDTO | null = {
    cnpj: '',
    razaoSocial: '',
    nomeFantasia: '',
    cnaeFiscal: '',
    codigoNaturezaJuridica: '',
    descricaoNaturezaJuridica: '',
    situacaoCadastral: '',
    dataSituacaoCadastral: '',
    dataInicioAtividade: '',
    porte: '',
    capitalSocial: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    cep: '',
    municipio: '',
    uf: '',
    pais: '',
    dddTelefone1: '',
    telefone1: '',
    dddTelefone2: '',
    telefone2: '',
    email: '',
    cnaesSecundarios: [],
    qsa: []
  };

  novoFornecedor: FornecedorDTO = {
    id: 0,
    idUsuario: 0,
    nome: '',
    nrResidencia: '',
    enderecoFornecedor: this.endereco,
    produtos: [],
    dadosEmpresa: null
  };

  informarCNPJ: boolean = true;
  formularioFornecedorHabilitado: boolean = false;
  cnpjBuscado: boolean = false;
  cepBuscado: boolean = false;
  cnpjCadastradoComSucesso: boolean = false; // Nova variável para controlar se o CNPJ foi cadastrado
  formularioCNPJVisivel: boolean = true; // Nova variável para controlar a visibilidade do formulário de CNPJ
  fornecedorCadastradoComSucesso: boolean = false; // Nova variável para controlar a mensagem de sucesso do fornecedor

  // Opções da máscara de CEP
  constructor(
    private geolocalizacaoService: GeolocalizacaoService,
    private modalService: NgbModal,
    private fornecedorService: FornecedorService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {
    // ENDPOINT PARA VERIFICAR TOKEN E LIBERAR ACESSO
    this.fornecedorService.acessarPaginaFornecedor().subscribe();

    // Inicializa com um objeto vazio se informarCNPJ for true
    this.resetDadosEmpresa();
  }

  resetDadosEmpresa() {
    if (this.informarCNPJ) {
      this.dadosEmpresa = {
        cnpj: '',
        razaoSocial: '',
        nomeFantasia: '',
        cnaeFiscal: '',
        codigoNaturezaJuridica: '',
        descricaoNaturezaJuridica: '',
        situacaoCadastral: '',
        dataSituacaoCadastral: '',
        dataInicioAtividade: '',
        porte: '',
        capitalSocial: '',
        logradouro: '',
        numero: '',
        complemento: '',
        bairro: '',
        cep: '',
        municipio: '',
        uf: '',
        pais: '',
        dddTelefone1: '',
        telefone1: '',
        dddTelefone2: '',
        telefone2: '',
        email: '',
        cnaesSecundarios: [],
        qsa: []
      };
    } else {
      this.dadosEmpresa = null;
    }
  }

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

    // Remover mensagem de sucesso do fornecedor ao buscar novo CEP
    this.fornecedorCadastradoComSucesso = false;

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

        // Preenche os campos com os dados retornados pelo backend
        this.endereco = {
          ...endereco,
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

    // Remover mensagem de sucesso do fornecedor ao buscar novo CNPJ
    this.fornecedorCadastradoComSucesso = false;

    // Ativar loading
    this.isLoadingCNPJ = true;

    // Chama o serviço que se comunica com o backend
    this.fornecedorService.obterEmpresaViaCNPJ(cnpjLimpo).subscribe({
      next: (empresa: DadosEmpresaDTO) => {
        // Desativar loading
        this.isLoadingCNPJ = false;
        
        // Marcar que o CNPJ foi buscado com sucesso
        this.cnpjBuscado = true;
        
        // Preenche os campos com os dados retornados pelo backend
        this.dadosEmpresa = empresa;
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

  onSubmit(): void {
    // Validar se pelo menos o CEP foi preenchido
    if (!this.endereco.cep.trim()) {
      this.mensagemErro = "Por favor, digite um CEP antes de finalizar o cadastro.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }
    
    this.gerarDadosFornecedor();
    // Adiciona o produto no banco depois chama a mensagem de sucesso de adição de produto "msgAddProduto"
    this.fornecedorService.adicionarFornecedor(this.novoFornecedor).subscribe({
      next: (fornecedorAdicionado: FornecedorDTO) => {
        this.novoFornecedor = fornecedorAdicionado;
        this.adicionouFornecedor = true;
        this.mensagemErro = "" // VOLTA A MSG DO MODAL DE ERROS PARA STRING VAZIA
        this.modalService.open(this.modalMsgAddFornecedor);
        
        // Resetar a tela ao estado inicial após cadastro bem-sucedido
        this.resetarTelaAoEstadoInicial();
      },
      error: (error: any) => {
        this.mensagemErro = "Erro ao cadastrar fornecedor. Tente novamente.";
        this.modalService.open(this.modalMsgAviso);
      }
    });
  }

  gerarDadosFornecedor() {
    // Certifique-se de que o CEP está limpo antes de enviar
    const cepFormatado = this.endereco.cep.replace(/\D/g, '');

    this.novoFornecedor = {
      id: 0,
      idUsuario: this.authService.getUsuarioLogado().idUsuario,
      nome: this.nomeFornecedor,
      nrResidencia: this.nrResidenciaFornecedor,
      enderecoFornecedor: {
        ...this.endereco,
        cep: cepFormatado // Garante que o CEP seja enviado apenas com números
      },
      produtos: [],
      dadosEmpresa: this.dadosEmpresa
    }
  }

  onInformarCNPJChange() {
    this.resetDadosEmpresa();
    this.cnpjEmpresa = '';
    this.formularioFornecedorHabilitado = false;
    this.cnpjBuscado = false;
    this.cepBuscado = false;
    this.cnpjCadastradoComSucesso = false; // Resetar o estado de CNPJ cadastrado
    this.formularioCNPJVisivel = true; // Mostrar o formulário de CNPJ novamente
    this.fornecedorCadastradoComSucesso = false; // Remover mensagem de sucesso do fornecedor
  }

  cadastrarCNPJ() {
    // Se escolheu informar CNPJ, verificar se pelo menos o CNPJ foi preenchido
    if (this.informarCNPJ && !this.cnpjEmpresa.trim()) {
      this.mensagemErro = "Por favor, digite um CNPJ antes de prosseguir.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }
    
    if (!this.informarCNPJ) {
      this.dadosEmpresa = null;
    }
    
    // Marcar que o CNPJ foi cadastrado com sucesso
    this.cnpjCadastradoComSucesso = true;
    this.formularioFornecedorHabilitado = true;
    this.formularioCNPJVisivel = false; // Esconder o formulário de CNPJ
  }

  limparFormularioCNPJ() {
    this.cnpjEmpresa = '';
    this.resetDadosEmpresa();
    this.cnpjBuscado = false;
    this.fornecedorCadastradoComSucesso = false; // Remover mensagem de sucesso do fornecedor
  }

  limparFormularioFornecedor(): void {
    this.nomeFornecedor = '';
    this.nrResidenciaFornecedor = '';
    this.cepBuscado = false;
    this.endereco = {
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
    this.fornecedorCadastradoComSucesso = false; // Remover mensagem de sucesso do fornecedor
  }

  resetarTelaAoEstadoInicial() {
    // Resetar dados do CNPJ
    this.cnpjEmpresa = '';
    this.resetDadosEmpresa();
    this.cnpjBuscado = false;
    this.cnpjCadastradoComSucesso = false;
    this.formularioCNPJVisivel = true;
    
    // Resetar dados do fornecedor
    this.limparFormularioFornecedor();
    this.cepBuscado = false;
    
    // Resetar estados dos formulários
    this.formularioFornecedorHabilitado = false;
    
    // Manter radio button "SIM" selecionado
    this.informarCNPJ = true;
    
    // Ativar mensagem de sucesso do fornecedor
    this.fornecedorCadastradoComSucesso = true;
  }
}
