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
    const errorMessage = error?.error?.error?.message;

    if (error.status === 401 && errorMessage === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado') {
      this.handleTokenExpired();
    }
    return throwError(() => error);
  }

  protected handleTokenExpired(): void {
    if (this.authService.existeToken()) {
      this.authService.removerToken();
      this.authService.adicionarTokenExpirado('true');
      this.router.navigate(['/login']);
    }
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