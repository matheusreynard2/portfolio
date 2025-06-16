import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { EnderecoFornecedorDTO } from '../../model/EnderecoFornecedorDTO';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FornecedorService } from '../../service/fornecedor/fornecedor.service';
import { NgOptimizedImage, CommonModule } from '@angular/common';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import {FornecedorDTO} from '../../model/dto/FornecedorDTO';
import { AuthService } from '../../service/auth/auth.service';
import { Router } from '@angular/router';

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

  novoFornecedor: FornecedorDTO = {
    id: 0,
    idUsuario: 0,
    nome: '',
    nrResidencia: '',
    enderecoFornecedor: this.endereco,
    produtos: []
  };

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

    // Chama o serviço que se comunica com o backend
    this.geolocalizacaoService.obterEnderecoViaCEP(cepFormatado).subscribe({
      next: (endereco: EnderecoFornecedorDTO) => {
        // Verifica APENAS o campo erro, não verificar o CEP novamente
        if (endereco.erro === "true") {
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
        this.mensagemErro = "Erro ao buscar CEP. Tente novamente.";
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
      produtos: []
    }
  }
}
