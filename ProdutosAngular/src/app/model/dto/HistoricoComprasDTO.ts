import { CompraDTO } from './CompraDTO';

export interface HistoricoComprasDTO {
  id: number | null;
  idUsuario: number;
  compras: CompraDTO[];
  quantidadeTotal: number;
  valorTotal: number;
  dataCompra: string; 
}