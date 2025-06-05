import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../service/auth/auth.service';
import { Router } from '@angular/router';

export const httpInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<any> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Adiciona o token em todas as requisições
  if (authService.existeToken()) {
    request = addToken(request, authService);
  }

  // Adiciona headers padrão
  request = addDefaultHeaders(request);

  // Log da requisição
  console.log(`Requisição ${request.method} para ${request.url}`);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // Log do erro
      console.error(`Erro na requisição ${request.method} ${request.url}:`, error);

      // Tratamento específico para token expirado
      if (error.status === 401 && 
          error.error?.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado') {
        handleTokenExpired(authService, router);
      }

      return throwError(() => error);
    })
  );
};

function addToken(request: HttpRequest<unknown>, authService: AuthService): HttpRequest<unknown> {
  const token = authService.getToken();
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}

function addDefaultHeaders(request: HttpRequest<unknown>): HttpRequest<unknown> {
  return request.clone({
    setHeaders: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  });
}

function handleTokenExpired(authService: AuthService, router: Router): void {
  if (authService.existeToken()) {
    authService.removerToken();
    authService.adicionarTokenExpirado('true');
    router.navigate(['/login']);
  }
} 