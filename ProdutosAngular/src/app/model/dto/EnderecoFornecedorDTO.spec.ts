import { EnderecoFornecedorDTO } from './EnderecoFornecedorDTO';

describe('EnderecoFornecedorDTO', () => {

  const endereco: EnderecoFornecedorDTO = {
    cep: '12345-678',
    logradouro: 'Rua Teste',
    complemento: 'Apto 123',
    unidade: 'Unidade 1',
    bairro: 'Centro',
    localidade: 'São Paulo',
    uf: 'SP',
    estado: 'São Paulo',
    regiao: 'Sudeste',
    ibge: '3550308',
    gia: '1004',
    ddd: '11',
    siafi: '7107',
    erro: 'false'
  };

  it('should create an instance', () => {
    expect(endereco).toBeTruthy();
  });

  it('should have correct properties', () => {
    expect(endereco.cep).toBe('12345-678');
    expect(endereco.logradouro).toBe('Rua Teste');
    expect(endereco.localidade).toBe('São Paulo');
    expect(endereco.uf).toBe('SP');
  });
}); 