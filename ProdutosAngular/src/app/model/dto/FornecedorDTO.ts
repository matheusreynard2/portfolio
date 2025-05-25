import {EnderecoFornecedor} from '../endereco-fornecedor';
import {ProdutoDTO} from './ProdutoDTO';

export interface FornecedorDTO {
  id: number;
  nome: string;
  nrResidencia: string;
  enderecoFornecedor: EnderecoFornecedor;
  produtos: ProdutoDTO[];
}
