import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('bearerToken');

    // Verifica as requisições que não precisam de headers
    if (req.url.includes('/realizarLogin') || req.url.includes('/adicionarUsuario')) {
      return next.handle(req);
    }

    if (token) {
      const clonedRequest = req.clone({
        setHeaders: {
          Authorization: 'Bearer ' + token
        }
      });
      return next.handle(clonedRequest);
    }

    return next.handle(req); // Caso não tenha o token, apenas continua com a requisição original
  }
}
