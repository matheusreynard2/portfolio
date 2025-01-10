import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProdutoFunctionsService {

  constructor() { }

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

  calcularFrete(): number {
    let valorFrete = 0;
    return valorFrete;
    // CALCULO DE VALOR DE FRETE PELO CEP
  }
}
