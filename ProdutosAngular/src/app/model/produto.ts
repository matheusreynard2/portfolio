export class Produto {
  constructor(
    public id: number,
    public nome: string,
    public descricao: string,
    public frete: number,
    public promocao: boolean,
    public valorTotalDesc: number,
    public valorTotalFrete: number,
    public valor: number,
    public quantia: number,
    public somaTotalValores: number,
    public freteAtivo: boolean
  ) {}
}
