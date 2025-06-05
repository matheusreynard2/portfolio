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
  ) {}

  protected handleError(error: HttpErrorResponse): Observable<never> {
    if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado') {
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

  protected createFormData(data: any, file?: File): FormData {
    const formData = new FormData();
    formData.append('json', JSON.stringify(data));
    if (file) {
      formData.append('file', file);
    }
    return formData;
  }
} 