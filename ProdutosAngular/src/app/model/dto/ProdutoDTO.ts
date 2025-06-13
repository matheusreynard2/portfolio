import {FornecedorDTO} from './FornecedorDTO';

export interface ProdutoDTO {
  id: number;
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
