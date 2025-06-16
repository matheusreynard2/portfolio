import {EnderecoFornecedorDTO} from './EnderecoFornecedorDTO';

export interface Fornecedor {
  id: number;
  nome: string;
  nrResidencia: string;
  enderecoFornecedor: EnderecoFornecedorDTO;
}
