import {Injectable, NgZone} from '@angular/core';
import {ProdutoService} from './produto.service';
import {HttpClient} from '@angular/common/http';
import {ProdutoDTO} from '../../model/dto/ProdutoDTO';

@Injectable({
  providedIn: 'root'
})
export class ProdutoFunctionsService {

  private resultadoDesconto: number = 0
  // Essa variavel é utilizada para setar a variavel produto.valorDesconto quando envia para a API
  private valorDescontoNumber: number = 0

  constructor(private produtoService: ProdutoService, private zone: NgZone) {
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
  calcularFrete(produto: ProdutoDTO): number {
    let valorFrete = produto.frete;
    return valorFrete;
  }

  // Calcula o valor do desconto de acordo com a porcentagem passada pelo usuário
  calcularDesconto(produto: ProdutoDTO): Promise<void> {
    return new Promise<void>((resolve) => {
      if (!produto.promocao) {
        this.resultadoDesconto = 0
        produto.valorDesconto = 0
        this.valorDescontoNumber = 0;
        resolve();
        return;
      }

      if (produto.promocao) {
        let valorDescontoString: string = produto.valorDesconto.toString()
        valorDescontoString = valorDescontoString.replaceAll('%', '')
        this.valorDescontoNumber = Number(valorDescontoString);

        // Chama o endpoint na API de calcular o desconto passando os valores
        this.produtoService.calcularDesconto(produto.valor, this.valorDescontoNumber).subscribe(
          (resultadoApi: number) => {
            this.resultadoDesconto = resultadoApi;
            resolve();
          },
          (error) => {
            console.error('Erro ao calcular desconto:', error);
            resolve();
          }
        );
      }
    })
  }


  calcularValores(produto: ProdutoDTO): Promise<void> {
    return new Promise((resolve) => {
      // Desconto SIM e Frete SIM
      if (produto.promocao && produto.freteAtivo) {
        this.calcularDesconto(produto).then(() => {
          this.zone.run(() => {
            produto.valorTotalDesc = this.resultadoDesconto
            produto.valorDesconto = this.valorDescontoNumber
            produto.valorTotalFrete = produto.valorTotalDesc + this.calcularFrete(produto)
            produto.frete = this.calcularFrete(produto)
            produto.somaTotalValores = produto.valorTotalDesc + produto.frete
            resolve();
          });
        });
      }
      // Desconto SIM e Frete NÃO
      else if (produto.promocao && !produto.freteAtivo) {
        this.calcularDesconto(produto).then(() => {
          this.zone.run(() => {
            produto.valorTotalDesc = this.resultadoDesconto
            produto.valorDesconto = this.valorDescontoNumber
            produto.frete = 0
            produto.valorTotalFrete = 0
            produto.somaTotalValores = produto.valorTotalDesc
            resolve();
          });
        });
      }
      // Desconto NÃO e Frete SIM
      else if (!produto.promocao && produto.freteAtivo) {
        this.calcularDesconto(produto).then(() => {
          this.zone.run(() => {
            produto.valorTotalDesc = this.resultadoDesconto
            produto.frete = this.calcularFrete(produto)
            produto.valorTotalFrete = produto.valor + this.calcularFrete(produto)
            produto.somaTotalValores = produto.valorTotalFrete
            resolve();
          })
        });
      }
      // Desconto NÃO e Frete NÃO
      else if (!produto.promocao && !produto.freteAtivo) {
        produto.frete = 0
        produto.valorTotalFrete = 0
        this.zone.run(() => {
          this.calcularDesconto(produto).then(() => {
            produto.valorTotalDesc = this.resultadoDesconto
            produto.somaTotalValores = produto.valor
            resolve();
          })
        });
      } else {
        resolve();
      }
    });
  }
}
