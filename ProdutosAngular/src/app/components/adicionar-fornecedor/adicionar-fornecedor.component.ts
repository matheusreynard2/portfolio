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
import { EmptyDadosEmpresa } from '../../model/templates/EmptyDadosEmpresa';
import { EmptyEnderecoFornecedor } from '../../model/templates/EmptyEnderecoFornecedor';

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
  cepFornecedor: string = '';
  logradouroFornecedor: string = '';
  complementoFornecedor: string = '';
  bairroFornecedor: string = '';
  localidadeFornecedor: string = '';
  ufFornecedor: string = '';
  estadoFornecedor: string = '';
  nrResidenciaFornecedor: string = '';
  adicionouFornecedor: boolean = false;
  mensagemErro: string = '';
  isLoading: boolean = false; // Variável para controlar o loading

  // Propriedades para o formulário de CNPJ
  cnpjEmpresa: string = '';
  isLoadingCNPJ: boolean = false;

  emptyDadosEmpresa!: EmptyDadosEmpresa;
  emptyEnderecoFornecedor!: EmptyEnderecoFornecedor;

  endereco: EnderecoFornecedorDTO = {
    ...this.emptyEnderecoFornecedor
  };

  dadosEmpresa: DadosEmpresaDTO = {
    ...this.emptyDadosEmpresa
  };

  novoFornecedor: FornecedorDTO = {
    id: 0,
    idUsuario: 0,
    nome: '',
    enderecoFornecedor: this.endereco,
    produtos: [],
    dadosEmpresa: this.dadosEmpresa
  };

  informarCNPJ: boolean = true;
  formularioFornecedorHabilitado: boolean = false;
  cnpjBuscado: boolean = false;
  cepBuscado: boolean = false;
  cnpjCadastradoComSucesso: boolean = false; // Nova variável para controlar se o CNPJ foi cadastrado
  formularioCNPJVisivel: boolean = true; // Nova variável para controlar a visibilidade do formulário de CNPJ
  botoesFormulario1Bloqueados: boolean = false; // Controla se os botões do primeiro formulário estão bloqueados
  mensagemContinuacaoFluxo: string = ''; // Mensagem a ser exibida na continuação do fluxo
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
    if (this.dadosEmpresa) {
      this.dadosEmpresa = {
        ...this.emptyDadosEmpresa
      };
    }
  }

  buscarEnderecoPorCep(cep: string): void {
    // Remover caracteres não numéricos do CEP
    const cepFormatado = cep.replace(/\D/g, '');

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
    // Remover caracteres não numéricos do CNPJ
    const cnpjLimpo = cnpj.replace(/\D/g, '');

    // Remover mensagem de sucesso do fornecedor ao buscar novo CNPJ
    this.fornecedorCadastradoComSucesso = false;

    // Ativar loading
    this.isLoadingCNPJ = true;

    // Chama o serviço que se comunica com o backend
    this.fornecedorService.obterEmpresaViaCNPJ(cnpjLimpo).subscribe({
      next: (empresa: DadosEmpresaDTO) => {
        // Desativar loading
        this.isLoadingCNPJ = false;
        
        // Verificar se a empresa foi encontrada (verificando se tem dados essenciais)
        if (!empresa || !empresa.razaoSocial || empresa.razaoSocial.trim() === '') {
          this.cnpjBuscado = false;
          this.mensagemErro = "CNPJ não encontrado.";
          this.modalService.open(this.modalMsgAviso);
          return;
        }
        
        // Marcar que o CNPJ foi buscado com sucesso
        this.cnpjBuscado = true;
        
        // Preenche os campos com os dados retornados pelo backend
        this.dadosEmpresa = empresa;
      },
      error: (error: any) => {
        // Desativar loading
        this.isLoadingCNPJ = false;
        this.cnpjBuscado = false;
        
        // Verificar se é erro 404 (não encontrado) ou outros erros
        if (error.status === 404) {
          this.mensagemErro = "Nenhuma empresa cadastrada com esse CNPJ.";
        } else {
          this.mensagemErro = "Erro ao buscar CNPJ. Tente novamente.";
        }
        this.modalService.open(this.modalMsgAviso);
      }
    });
  }

  onSubmit(): void {
    
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
      enderecoFornecedor: {
        ...this.endereco,
        cep: cepFormatado, // Garante que o CEP seja enviado apenas com números
        nrResidencia: this.nrResidenciaFornecedor
      },
      produtos: [],
      dadosEmpresa: this.dadosEmpresa
    }
  }

  onInformarCNPJChange() {
    // Se mudou para "sim" (informar CNPJ), resetar ao estado inicial da página
    if (this.informarCNPJ) {
      this.resetarTodosOsCamposAoEstadoInicial();
      return;
    }
    
    // Se mudou para "não", apenas resetar dados específicos do CNPJ
    this.resetDadosEmpresa();
    this.cnpjEmpresa = '';
    this.formularioFornecedorHabilitado = false;
    this.cnpjBuscado = false;
    this.cepBuscado = false;
    this.cnpjCadastradoComSucesso = false;
    this.formularioCNPJVisivel = true;
    this.fornecedorCadastradoComSucesso = false;
    this.botoesFormulario1Bloqueados = false;
    this.mensagemContinuacaoFluxo = '';
  }

  cadastrarCNPJ() {
    // ALTERAÇÃO 1: Verificar se o CNPJ foi buscado quando o radio button está selecionado
    if (this.informarCNPJ && !this.cnpjBuscado) {
      this.mensagemErro = "Por favor, clique em 'Buscar Empresa' antes de prosseguir.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // ALTERAÇÃO 2: Bloquear os 3 botões e exibir mensagem de continuação
    this.botoesFormulario1Bloqueados = true;

    // Continuar com a lógica existente
    this.cnpjCadastradoComSucesso = true;
    this.formularioCNPJVisivel = false;
    this.formularioFornecedorHabilitado = true;
  }

  limparFormularioCNPJ() {
    // Resetar completamente ambos os formulários ao estado inicial
    this.resetarTodosOsCamposAoEstadoInicial();
  }

  limparFormularioFornecedor(): void {
    // Resetar completamente ambos os formulários ao estado inicial
    this.resetarTodosOsCamposAoEstadoInicial();
  }

  resetarTodosOsCamposAoEstadoInicial() {
    // Resetar dados do CNPJ e empresa
    this.cnpjEmpresa = '';
    this.resetDadosEmpresa();
    this.cnpjBuscado = false;
    this.cnpjCadastradoComSucesso = false;
    this.formularioCNPJVisivel = true;
    this.isLoadingCNPJ = false;
    this.botoesFormulario1Bloqueados = false; // Resetar bloqueio dos botões
    this.mensagemContinuacaoFluxo = ''; // Limpar mensagem de continuação
    
    // Resetar dados do fornecedor
    this.nomeFornecedor = '';
    this.nrResidenciaFornecedor = '';

    if (this.endereco) {
      this.endereco = {
        ...this.emptyEnderecoFornecedor,
      };
    }
    
    this.cepBuscado = false;
    this.isLoading = false;
    this.formularioFornecedorHabilitado = false;
    this.fornecedorCadastradoComSucesso = false;
    
    // Resetar radio button para estado inicial
    this.informarCNPJ = true;
    
    // Resetar mensagens de erro
    this.mensagemErro = '';
  }

  resetarTelaAoEstadoInicial() {
    this.resetarTodosOsCamposAoEstadoInicial();
    this.resetDadosEmpresa();
    this.informarCNPJ = true;
    this.formularioFornecedorHabilitado = false;
    this.cnpjBuscado = false;
    this.cepBuscado = false;
    this.cnpjCadastradoComSucesso = false;
    this.formularioCNPJVisivel = true;
    this.botoesFormulario1Bloqueados = false;
    this.mensagemContinuacaoFluxo = '';
    this.fornecedorCadastradoComSucesso = false;
  }

  // Getter para validação de CNPJ (14 dígitos)
  get isCnpjValido(): boolean {
    if (!this.cnpjEmpresa) return false;
    const cnpjLimpo = this.cnpjEmpresa.replace(/\D/g, '');
    return cnpjLimpo.length === 14;
  }

  // Getter para validação de CEP (8 dígitos)
  get isCepValido(): boolean {
    if (!this.endereco?.cep) return false;
    const cepLimpo = this.endereco.cep.replace(/\D/g, '');
    return cepLimpo.length === 8;
  }

}
