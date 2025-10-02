export interface TransacaoItemDTO {
    idProduto: number;
    nome: string;
    descricao?: string;
    precoUnit: number;
    quantidade: number;
    subtotal: number;
  }