import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {
  formatarValor(valor: number): string {
    return valor.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }

  formatarCEP(cep: string): string {
    return cep.replace(/\D/g, '');
  }

  validarCEP(cep: string): boolean {
    const cepFormatado = this.formatarCEP(cep);
    return cepFormatado.length === 8;
  }

  converterImagemParaBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = error => reject(error);
    });
  }

  converterBase64ParaBlob(base64: string, type: string = 'image/jpeg'): Blob {
    const byteString = atob(base64.split(',')[1]);
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    
    for (let i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
    }
    
    return new Blob([ab], { type });
  }

  calcularValorComDesconto(valor: number, desconto: number): number {
    return valor * (1 - desconto / 100);
  }

  calcularValorTotal(valorUnitario: number, quantidade: number): number {
    return valorUnitario * quantidade;
  }
} 