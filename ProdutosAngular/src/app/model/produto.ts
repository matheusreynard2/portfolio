export class Produto {
  public id: number;

  public nome: string;

  public descricao: string;

  public frete: number;

  public promocao: boolean;

  public valorTotalDesc: number;

  public valorTotalFrete: number;

  public valor: number;

  public quantia: number;

  public somaTotalValores: number;

  public freteAtivo: boolean;

  public desconto: number;

  constructor(
    id: number,
    nome: string,
    descricao: string,
    frete: number,
    promocao: boolean,
    valorTotalDesc: number,
    valorTotalFrete: number,
    valor: number,
    quantia: number,
    somaTotalValores: number,
    freteAtivo: boolean,
    desconto: number
  ) {
    this.desconto = desconto;
    this.freteAtivo = freteAtivo;
    this.somaTotalValores = somaTotalValores;
    this.quantia = quantia;
    this.valor = valor;
    this.valorTotalFrete = valorTotalFrete;
    this.valorTotalDesc = valorTotalDesc;
    this.promocao = promocao;
    this.frete = frete;
    this.descricao = descricao;
    this.nome = nome;
    this.id = id;
  }
}
