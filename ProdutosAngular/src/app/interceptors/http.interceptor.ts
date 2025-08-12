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
  const token = authService.getToken();

  if (token) {
    request = addToken(request, authService);
  }
  
  request = addDefaultHeaders(request);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // Log do erro
      console.error(`Erro na requisição ${request.method} ${request.url}:`, error);
      
      // Tratamento específico para token expirado
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
        handleTokenExpired(authService, router);
      }
      return throwError(() => error);
    })
  );
};

function isPublicEndpoint(url: string): boolean {
  const publicEndpoints = [
    '/api/auth/realizarLogin',
    '/api/auth/refresh',
    '/api/auth/logout',
    '/api/usuarios/addNovoAcessoIp',
    '/api/usuarios/getAllAcessosIp',
    '/api/usuarios/adicionarUsuario'
  ];
  
  return publicEndpoints.some(endpoint => url.includes(endpoint));
}

function addToken(request: HttpRequest<unknown>, authService: AuthService): HttpRequest<unknown> {
  const token = authService.getToken();
  
  if (token && token.trim() !== '') {
    const requestWithToken = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return requestWithToken;
  }
  return request;
}

function addDefaultHeaders(request: HttpRequest<unknown>): HttpRequest<unknown> {
  const headers: { [key: string]: string } = {};
  
  // Sempre adiciona headers básicos para CORS
  headers['Accept'] = 'application/json';
  
  // Se a requisição for FormData, NÃO define Content-Type
  // O Angular define automaticamente Content-Type como multipart/form-data para FormData
  if (!(request.body instanceof FormData)) {
    // Só define Content-Type como application/json se NÃO for FormData
    headers['Content-Type'] = 'application/json';
  }
  
  return request.clone({
    setHeaders: headers
  });
}

function handleTokenExpired(authService: AuthService, router: Router): void { 
  if (authService.existeToken()) {
    authService.removerToken();
    authService.adicionarTokenExpirado('true');
    router.navigate(['/login']);
  } else {
    authService.adicionarTokenExpirado('true');
    router.navigate(['/login']);
  }
} 