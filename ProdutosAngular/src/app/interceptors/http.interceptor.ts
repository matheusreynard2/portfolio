// http.interceptor.ts (funcional)
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../service/auth/auth.service';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export const httpInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<any> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // ❗ Não injeta Authorization aqui (isso é do AuthInterceptor)
  // Apenas headers padrão leves
  request = addDefaultHeaders(request);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // Lida apenas com 440 (sessão expirada no servidor)
      if (isApiRequest(request.url) && error.status === 440) {
        authService.adicionarTokenExpirado('true');
        // Limpa estado e navega para /login (removerChaves já faz navigate)
        authService.removerChaves();
        return throwError(() => error);
      }
      // Não trata 401/403 aqui (deixa para o AuthInterceptor)
      return throwError(() => error);
    })
  );
};

function addDefaultHeaders(request: HttpRequest<unknown>): HttpRequest<unknown> {
  const headers: Record<string, string> = { Accept: 'application/json' };

  // Define Content-Type só quando a requisição TEM body e não for FormData/Blob
  const hasBody = ['POST', 'PUT', 'PATCH'].includes(request.method.toUpperCase());
  const isFormData = typeof FormData !== 'undefined' && request.body instanceof FormData;
  const isBlob = typeof Blob !== 'undefined' && request.body instanceof Blob;

  if (hasBody && !isFormData && !isBlob) {
    headers['Content-Type'] = 'application/json';
  }

  return request.clone({ setHeaders: headers });
}

function isApiRequest(url: string): boolean {
  return url.startsWith(environment.API_URL);
}
