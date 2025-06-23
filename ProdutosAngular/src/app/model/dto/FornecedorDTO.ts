import { DadosEmpresaDTO } from './DadosEmpresaDTO';
import {EnderecoFornecedorDTO} from './EnderecoFornecedorDTO';
import {ProdutoDTO} from './ProdutoDTO';

export interface FornecedorDTO {
  id: number;
  idUsuario: number;
  nome: string;
  enderecoFornecedor: EnderecoFornecedorDTO;
  produtos: ProdutoDTO[];
  dadosEmpresa: DadosEmpresaDTO;
}
