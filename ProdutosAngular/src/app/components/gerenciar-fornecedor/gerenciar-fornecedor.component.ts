import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {GeolocalizacaoService} from '../../service/geolocalizacao/geolocalizacao.service';
import {EnderecoFornecedor} from '../../model/endereco-fornecedor';
import {FormsModule} from '@angular/forms';
import {Fornecedor} from '../../model/fornecedor';

@Component({
  selector: 'app-gerenciar-fornecedor',
  templateUrl: './gerenciar-fornecedor.component.html',
  imports: [
    FormsModule
  ],
  styleUrls: ['./gerenciar-fornecedor.component.css']
})
export class GerenciarFornecedorComponent implements OnInit {
  @ViewChild('modalMsgAddFornecedor') modalMsgAddFornecedor!: TemplateRef<any>;
  @ViewChild('nomeFornecedor') nomeFornecedor!: string;

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

  fornecedor: Fornecedor = {
    nome: '',
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


  constructor(
    private geolocalizacaoService: GeolocalizacaoService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void { }

  buscarEnderecoPorCep(cep: string): void {
    // Remove caracteres não numéricos do CEP
    cep = cep.replace(/\D/g, '');

    if (cep.length !== 8) {
      alert('CEP inválido. Por favor, digite um CEP com 8 dígitos.');
      return;
    }

    // Chama o serviço que se comunica com o backend
    this.geolocalizacaoService.obterEnderecoViaCEP(cep).subscribe(
      (endereco: EnderecoFornecedor) => {
        if (!endereco || !endereco.cep) {
          alert('CEP não encontrado.');
          return;
        }

        // Preenche os campos com os dados retornados pelo backend
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

    this.fornecedor = {
      nome: this.nomeFornecedor,
      ...this.endereco,
    }

    // Lógica para enviar os dados do fornecedor para o backend
    // ...

    // Abre o modal de sucesso
    this.modalService.open(this.modalMsgAddFornecedor);
  }
}
