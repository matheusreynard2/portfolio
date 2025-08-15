import { CaixaItemDTO } from './CaixaItemDTO';

export interface VendaCaixaDTO {
  id: number | null;
  idUsuario: number;
  itens: CaixaItemDTO[];
  totalQuantidade: number;
  totalValor: number;
}


