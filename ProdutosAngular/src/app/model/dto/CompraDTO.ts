import { ProdutoDTO } from "./ProdutoDTO";

export interface CompraDTO {
    id: number | null;
    idUsuario: number;
    produto: ProdutoDTO;
    quantidadeComprada: number;
    valorUnitarioCompra: number;
    valorTotalCompra: number;
  }