import { EnderecoFornecedor } from './endereco-fornecedor';

describe('EnderecoFornecedor', () => {
  it('should create an object implementing the interface', () => {
    const endereco: EnderecoFornecedor = {
      cep: '01001-000',
      logradouro: 'Praça da Sé',
      complemento: 'lado ímpar',
      unidade: '',
      bairro: 'Sé',
      localidade: 'São Paulo',
      uf: 'SP',
      estado: 'São Paulo',
      regiao: 'Sudeste',
      ibge: '3550308',
      gia: '1004',
      ddd: '11',
      siafi: '7107',
      erro: ''
    };

    expect(endereco).toBeTruthy();
  });
});
