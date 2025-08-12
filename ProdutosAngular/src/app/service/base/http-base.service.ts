import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class HttpBaseService {
  constructor(
    protected authService: AuthService,
    protected router: Router
  ) { }

  protected catchErrorTokenExpirado(error: HttpErrorResponse): Observable<never> {
    
    const message = (
      (error?.error && typeof error.error === 'string' ? error.error : null) ||
      error?.error?.error?.message ||
      error?.error?.mensagem ||
      error?.error?.message ||
      error?.message
    );

    if (
      error.status === 401 &&
      message &&
      (message.includes('TOKEN Expirado') || message.includes('Tempo limite de conexão'))
    ) {
      this.handleTokenExpired();
    }
    return throwError(() => error);
  }

  protected handleTokenExpired(): void {
    console.log('=== HttpBaseService handleTokenExpired ===');
    console.log('Token existe?', this.authService.existeToken());
    
    if (this.authService.existeToken()) {
      console.log('Removendo token...');
      this.authService.removerToken();
      console.log('Marcando token como expirado...');
      this.authService.adicionarTokenExpirado('true');
      console.log('Navegando para /login...');
      this.router.navigate(['/login']);
    } else {
      console.log('Token não existe, marcando como expirado e navegando...');
      this.authService.adicionarTokenExpirado('true');
      this.router.navigate(['/login']);
    }
    console.log('=====================================');
  }

  protected createFormData(data: any, file?: File, jsonParamName: string = 'json'): FormData {
    const formData = new FormData();

    // Para @ModelAttribute, precisamos enviar cada campo individualmente
    Object.keys(data).forEach(key => {
      const value = data[key];
      if (value !== null && value !== undefined) {
        if (typeof value === 'object' && !Array.isArray(value)) {
          formData.append(key, JSON.stringify(value));
        } else if (Array.isArray(value)) {
          // Para arrays, enviar como JSON
          formData.append(key, JSON.stringify(value));
        } else {
          formData.append(key, value.toString());
        }
      }
    });

    if (file) {
      formData.append('imagemFile', file);
    }
    return formData;
  }

  protected createProdutoFormData(produto: any, file?: File): FormData {
    const formData = new FormData();
  
    formData.append('produtoJson', JSON.stringify(produto));
  
    if (file) {
      formData.append('imagemFile', file);
    }
  
    return formData;
  }

  protected createUsuarioFormData(usuario: any, file?: File): FormData {
    const formData = new FormData();

    formData.append('usuarioJson', JSON.stringify(usuario));

    // Enviar o arquivo de imagem se existir
    if (file) {
      formData.append('imagemFile', file);
    }

    return formData;
  }
} 