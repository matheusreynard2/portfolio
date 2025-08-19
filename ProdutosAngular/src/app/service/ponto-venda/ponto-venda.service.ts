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
    return this.http.post<number>(`${this.pdvUrl}/salvarCaixa`, venda);
  }

  finalizarVenda(vendaId: number): Observable<void> {
    return this.http.post<void>(`${this.pdvUrl}/finalizarVenda/${vendaId}`, {});
  }

  acessarPaginaPdv(): Observable<any> {
    return this.http.get<any>(`${this.pdvUrl}/acessarPaginaPdv`);
  }

  listarHistorico(): Observable<any[]> {
    return this.http.get<any[]>(`${this.pdvUrl}/listarHistorico`);
  }

  excluirHistorico(idHistorico: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.pdvUrl}/deletarHistorico/${idHistorico}`);
  }
 
}

