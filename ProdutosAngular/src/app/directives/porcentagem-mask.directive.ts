import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appPorcentagemMask]'  // A diretiva será aplicada aos elementos que utilizarem esse atributo
})
export class PorcentagemMaskDirective {

  constructor(private elemnt: ElementRef) {}

  // Evento de escuta para o input de entrada do usuário
  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const input = this.elemnt.nativeElement;
    let value = input.value;

    // Remove todos os caracteres não numéricos, exceto o ponto
    value = value.replace(/[^0-9]/g, '');

    // Converte o valor para um número inteiro
    let numericValue = parseInt(value, 10);

    // Verifica se o valor é um número válido (NaN)
    if (isNaN(numericValue)) {
      // Caso o valor seja NaN, não altere o campo ou defina um valor padrão
      input.value = '';
      return;
    }

    // Limita o valor entre 0 e 100
    if (numericValue > 100) {
      numericValue = 100;
    }
    if (numericValue < 0) {
      numericValue = 0;
    }

    // Adiciona o símbolo '%' ao final
    input.value = numericValue + '%';
  }
}
