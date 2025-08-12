import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private refreshing = false;

  constructor(private auth: AuthService, private http: HttpClient, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.auth.getAccessToken();
    const withCredReq = token
      ? req.clone({ withCredentials: true, setHeaders: { Authorization: `Bearer ${token}` } })
      : req.clone({ withCredentials: true });

    return next.handle(withCredReq).pipe(
      catchError((err: any) => {
        const isLogin = req.url.includes('/auth/realizarLogin');
        const isRefresh = req.url.includes('/auth/refresh');
        const isLogout = req.url.includes('/auth/logout');

        if (err instanceof HttpErrorResponse && err.status === 401 && !this.refreshing && !isLogin && !isRefresh && !isLogout) {
          this.refreshing = true;
          return this.http.post<{ accessToken: string }>(`${environment.API_URL}/auth/refresh`, {}, { withCredentials: true })
            .pipe(
              switchMap(res => {
                this.auth.setAccessToken(res.accessToken);
                const retry = req.clone({
                  withCredentials: true,
                  setHeaders: { Authorization: `Bearer ${res.accessToken}` }
                });
                this.refreshing = false;
                return next.handle(retry);
              }),
              catchError(refreshErr => {
                this.refreshing = false;
                // Refresh falhou: marcar token expirado e redirecionar para login
                this.auth.setAccessToken(null);
                this.auth.adicionarTokenExpirado('true');
                // Marca que vamos exibir modal de logout após refresh de página
                this.router.navigate(['/login']);
                return throwError(() => refreshErr);
              })
            );
        }
        return throwError(() => err);
      })
    );
  }
}


