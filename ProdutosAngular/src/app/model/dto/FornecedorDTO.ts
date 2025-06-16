import {EnderecoFornecedorDTO} from './EnderecoFornecedorDTO';
import {ProdutoDTO} from './ProdutoDTO';

export interface FornecedorDTO {
  id: number;
  idUsuario: number;
  nome: string;
  nrResidencia: string;
  enderecoFornecedor: EnderecoFornecedorDTO;
  produtos: ProdutoDTO[];
}
