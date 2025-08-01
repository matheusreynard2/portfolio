import {FornecedorDTO} from './FornecedorDTO';

export interface ProdutoDTO {
  id: number | null; // null para novos produtos, number para produtos existentes
  idUsuario: number;
  nome: string;
  imagem?: any;
  fornecedor?: FornecedorDTO;
  valor: number;
  promocao: boolean;
  valorTotalDesc: number;
  somaTotalValores: number;
  valorTotalFrete: number;
  descricao: string;
  frete: number;
  valorInicial: number;
  quantia: number;
  freteAtivo: boolean;
  valorDesconto: number;
}
