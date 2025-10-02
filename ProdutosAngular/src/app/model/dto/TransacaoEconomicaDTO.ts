import { TransacaoItemDTO } from "./TransacaoItemDTO";

export type TransacaoTipo = 'COMPRA' | 'VENDA';

export interface TransacaoEconomicaDTO {
    id: number;
    tipo: TransacaoTipo;
    data: string;
    totalQuantidade: number;
    totalValor: number;
    itens: TransacaoItemDTO[];
  }