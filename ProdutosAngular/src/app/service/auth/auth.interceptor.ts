import { Injectable } from '@angular/core';
import {
  HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, Subject, from, throwError } from 'rxjs';
import { catchError, finalize, switchMap, take, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  private refreshResult$ = new Subject<boolean>();

  // Endpoints de AUTH do BACKEND (evitar loop)
  private static readonly AUTH_ENDPOINTS = [
    `${environment.API_URL}/auth/realizarLogin`,
    `${environment.API_URL}/auth/refresh`,
    `${environment.API_URL}/auth/logout`,
  ];

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // 1) Não intercepta chamadas de auth do backend (evita loop). Ainda manda cookies.
    if (this.isAuthEndpoint(req.url)) {
      return next.handle(req.clone({ withCredentials: true }));
    }

    // 2) Só trata chamadas ao seu backend (não mandar Authorization para terceiros)
    if (!this.isApiRequest(req.url)) {
      return next.handle(req);
    }

    // 3) Anexa Authorization se houver token + withCredentials
    const withAuth = this.addAuthHeaderIfPresent(req);

    return next.handle(withAuth).pipe(
      // Marca atividade em respostas OK (mantém sessão viva enquanto usa o app)
      tap(() => this.auth.markUserActivity()),
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse && (err.status === 401 || err.status === 403)) {

          // Refresh concorrente: espera e reenvia
          if (this.isRefreshing) {
            return this.refreshResult$.pipe(
              take(1),
              switchMap(ok => {
                if (ok) {
                  const retried = this.addAuthHeaderIfPresent(req);
                  return next.handle(retried);
                }
                this.auth.removerChaves();
                return throwError(() => err);
              })
            );
          }

          // Inicia refresh único
          this.isRefreshing = true;
          return from(this.auth.refreshToken()).pipe(
            switchMap(ok => {
              this.refreshResult$.next(ok);
              if (ok) {
                const retried = this.addAuthHeaderIfPresent(req);
                return next.handle(retried);
              } else {
                this.auth.removerChaves();
                return throwError(() => err);
              }
            }),
            finalize(() => (this.isRefreshing = false))
          );
        }

        // Outros erros seguem o fluxo normal
        return throwError(() => err);
      })
    );
  }

  private addAuthHeaderIfPresent(req: HttpRequest<any>): HttpRequest<any> {
    const token = this.auth.getAccessToken();
    if (!token) {
      return req.clone({ withCredentials: true });
    }
    return req.clone({
      withCredentials: true,
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  private isAuthEndpoint(url: string): boolean {
    return AuthInterceptor.AUTH_ENDPOINTS.some(ep => url.startsWith(ep));
  }

  private isApiRequest(url: string): boolean {
    return url.startsWith(environment.API_URL);
  }
}
