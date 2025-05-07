import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { EnderecoFornecedor } from '../../model/endereco-fornecedor';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Fornecedor } from '../../model/fornecedor';
import { FornecedorService } from '../../service/fornecedor/fornecedor.service';
import { NgOptimizedImage } from '@angular/common';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'adicionar-fornecedor',
  templateUrl: './adicionar-fornecedor.component.html',
  imports: [
    FormsModule,
    NgOptimizedImage,
    ReactiveFormsModule,
    NgxMaskDirective
  ],
  providers: [provideNgxMask()],
  styleUrls: ['./adicionar-fornecedor.component.css']
})
export class AdicionarFornecedorComponent implements OnInit {
  @ViewChild('modalMsgAddFornecedor') modalMsgAddFornecedor!: TemplateRef<any>;

  nomeFornecedor: string = '';
  nrResidenciaFornecedor: string = '';
  adicionouFornecedor: boolean = false;

  endereco: EnderecoFornecedor = {
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
    siafi: ''
  };

  novoFornecedor: Fornecedor = {
    id: 0,
    nome: '',
    nrResidencia: '',
    enderecoFornecedor: this.endereco
  };

  // Opções da máscara de CEP
  cepMaskOptions = {
    mask: '00000-000',
    showMaskTyped: true
  };

  constructor(
    private geolocalizacaoService: GeolocalizacaoService,
    private modalService: NgbModal,
    private fornecedorService: FornecedorService
  ) { }

  ngOnInit() {
    // ENDPOINT PARA VERIFICAR TOKEN E LIBERAR ACESSO
    this.fornecedorService.acessarPaginaFornecedor().subscribe();
  }

  buscarEnderecoPorCep(cep: string): void {
    // Remover caracteres não numéricos do CEP
    const cepLimpo = cep.replace(/\D/g, '');

    if (cepLimpo.length !== 8) {
      alert('CEP inválido. Por favor, digite um CEP com 8 dígitos.');
      return;
    }

    // Chama o serviço que se comunica com o backend
    this.geolocalizacaoService.obterEnderecoViaCEP(cepLimpo).subscribe(
      (endereco: EnderecoFornecedor) => {
        if (!endereco || !endereco.cep) {
          alert('CEP não encontrado.');
          return;
        }

        // Preenche os campos com os dados retornados pelo backend
        this.endereco.cep = endereco.cep; // Preserva o CEP formatado
        this.endereco.logradouro = endereco.logradouro || '';
        this.endereco.complemento = endereco.complemento || '';
        this.endereco.unidade = endereco.unidade || '';
        this.endereco.bairro = endereco.bairro || '';
        this.endereco.localidade = endereco.localidade || '';
        this.endereco.uf = endereco.uf || '';
        this.endereco.estado = endereco.estado || '';
        this.endereco.regiao = endereco.regiao || '';
        this.endereco.ibge = endereco.ibge || '';
        this.endereco.gia = endereco.gia || '';
        this.endereco.ddd = endereco.ddd || '';
        this.endereco.siafi = endereco.siafi || '';
      },
      (error: any) => {
        console.error('Erro ao buscar CEP:', error);
        alert('Erro ao buscar o CEP. Por favor, tente novamente.');
      }
    );
  }

  onSubmit(): void {
    this.gerarDadosFornecedor();
    // Adiciona o produto no banco depois chama a mensagem de sucesso de adição de produto "msgAddProduto"
    this.fornecedorService.adicionarFornecedor(this.novoFornecedor).subscribe({
      next: (fornecedorAdicionado: Fornecedor) => {
        this.novoFornecedor = fornecedorAdicionado;
        this.adicionouFornecedor = true;
        this.modalService.open(this.modalMsgAddFornecedor);
      }
    });
  }

  gerarDadosFornecedor() {
    // Certifique-se de que o CEP está limpo antes de enviar
    const cepLimpo = this.endereco.cep.replace(/\D/g, '');

    this.novoFornecedor = {
      id: 0,
      nome: this.nomeFornecedor,
      nrResidencia: this.nrResidenciaFornecedor,
      enderecoFornecedor: {
        ...this.endereco,
        cep: cepLimpo // Garante que o CEP seja enviado apenas com números
      }
    }
  }
}
