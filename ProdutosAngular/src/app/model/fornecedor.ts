import {EnderecoFornecedor} from './endereco-fornecedor';

export interface Fornecedor {
  id: number;
  nome: string;
  nrResidencia: string;
  enderecoFornecedor: EnderecoFornecedor;
}
