import {Injectable} from '@angular/core';
import {Produto} from '../model/produto';
import {ProdutoService} from './produto.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProdutoFunctionsService {

  private resultadoDesconto: number = 0
  // Essa variavel é utilizada para setar a variavel produto.valorDesconto quando envia para a API
  private valorDescontoNumber: number = 0;

  constructor(private http: HttpClient, private produtoService: ProdutoService) {
  }

  // Função chamada ao mudar de valor na ComboBox de Promoção no ngModel
  selecionarPromocao(selecionouPromocao: boolean): boolean {
    if (selecionouPromocao) {
      return true;
    } else {
      return false;
    }
  }

  // Função chamada quando troca o valor da ComboBox Frete Ativo para saber se o Produto tem frete ou não
  ativarFrete(ativouFrete: boolean): boolean {
    if (ativouFrete) {
      return true;
    } else {
      return false;
    }
  }

  // CALCULO DE VALOR DE FRETE
  calcularFrete(): number {
    let valorFrete = 0;
    return valorFrete;
  }

  // Calcula o valor do desconto de acordo com a porcentagem passada pelo usuário
  calcularDesconto(produto: Produto) {

    if (!produto.promocao) {
      this.resultadoDesconto = 0
      produto.valorDesconto = 0
      this.valorDescontoNumber = 0;
    }

    if (produto.promocao) {
      let valorDescontoString: string = produto.valorDesconto.toString()
      valorDescontoString = valorDescontoString.replaceAll('%', '')
      this.valorDescontoNumber = Number(valorDescontoString);
      // Chama o endpoint na API de calcular o desconto passando os valores
      this.produtoService.calcularDesconto(produto.valor, this.valorDescontoNumber).subscribe(
        (resultadoApi: number) => {
          this.resultadoDesconto = resultadoApi;
        })
    }
  }

  calcularValores(produto: Produto) {

    // Desconto SIM e Frete SIM
    if (produto.promocao && produto.freteAtivo) {
      this.calcularDesconto(produto)
      produto.valorTotalFrete = produto.valorTotalDesc + this.calcularFrete()
      produto.frete = this.calcularFrete()

      // Espera 0.2 segundos pra executar pois a chamada da API é assincrona e demora um pouco
      // pra retornar o resultado e poder atualizar a variável resultadoDesconto
      setTimeout(() => {
        produto.valorTotalDesc = this.resultadoDesconto
        produto.valorDesconto = this.valorDescontoNumber
        produto.somaTotalValores = produto.valorTotalDesc + produto.frete
      }, 200);

    }

    // Desconto SIM e Frete NÃO
    if (produto.promocao && !produto.freteAtivo) {
      produto.frete = 0
      produto.valorTotalFrete = 0
      this.calcularDesconto(produto)

      // Espera 0.2 segundos pra executar pois a chamada da API é assincrona e demora um pouco
      // pra retornar o resultado e poder atualizar a variável resultadoDesconto
      setTimeout(() => {
        produto.valorTotalDesc = this.resultadoDesconto
        produto.valorDesconto = this.valorDescontoNumber
        produto.somaTotalValores = produto.valorTotalDesc
      }, 200);
    }

    // Desconto NÃO e Frete SIM
    if (!produto.promocao && produto.freteAtivo) {
      produto.valorTotalFrete = produto.valor + this.calcularFrete()
      this.calcularDesconto(produto)
      produto.valorTotalDesc = this.resultadoDesconto
      produto.frete = this.calcularFrete()

      produto.somaTotalValores = produto.valorTotalFrete
    }

    // Desconto NÃO e Frete NÃO
    if (!produto.promocao && !produto.freteAtivo) {
      produto.frete = 0
      produto.valorTotalFrete = 0
      this.calcularDesconto(produto)
      produto.valorTotalDesc = this.resultadoDesconto

      produto.somaTotalValores = produto.valor
    }

  }
}
