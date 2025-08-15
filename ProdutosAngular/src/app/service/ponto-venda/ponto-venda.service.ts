import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { VendaCaixaDTO } from '../../model/dto/VendaCaixaDTO';
import { HttpBaseService } from '../base/http-base.service';
import { catchError, Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';


@Injectable({ providedIn: 'root' })
export class PontoVendaService extends HttpBaseService {
  private readonly pdvUrl = `${environment.API_URL}/pdv`;

  constructor(
    private http: HttpClient,
    authService: AuthService,
    router: Router
  ) { 
    super(authService, router);
  }

  salvarCaixa(venda: VendaCaixaDTO): Observable<number> {
    return this.http.post<number>(`${this.pdvUrl}/salvarCaixa`, venda)
    .pipe(catchError(error => this.catchErrorTokenExpirado(error)));;
  }

  finalizarVenda(vendaId: number): Observable<void> {
    return this.http.post<void>(`${this.pdvUrl}/finalizarVenda/${vendaId}`, {})
    .pipe(catchError(error => this.catchErrorTokenExpirado(error)));;
  }

  acessarPaginaPdv(): Observable<any> {
    return this.http.get<any>(`${this.pdvUrl}/acessarPaginaPdv`)
        .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }

  listarHistorico(): Observable<any[]> {
    return this.http.get<any[]>(`${this.pdvUrl}/listarHistorico`)
      .pipe(catchError(error => this.catchErrorTokenExpirado(error)));
  }
 
}

