import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { VendaCaixaDTO } from '../../model/dto/VendaCaixaDTO';
import { HttpBaseService } from '../base/http-base.service';
import { catchError, Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { CompraDTO } from '../../model/dto/CompraDTO';
import { HistoricoComprasDTO } from '../../model/dto/HistoricoComprasDTO';


@Injectable({ providedIn: 'root' })
export class ComprarProdutosService extends HttpBaseService {
  private readonly comprasUrl = `${environment.API_URL}/compras`;

  constructor(
    private http: HttpClient,
    authService: AuthService,
    router: Router
  ) { 
    super(authService, router);
  }

  cadastrarCompras(compras: CompraDTO[]): Observable<HistoricoComprasDTO> {
    return this.http.post<HistoricoComprasDTO>(`${this.comprasUrl}/adicionarCompra`, compras);
  }
 
  getHistoricoCompras(idUsuario: number): Observable<HistoricoComprasDTO[]> {
    return this.http.get<HistoricoComprasDTO[]>(`${this.comprasUrl}/listarHistoricoCompras/${idUsuario}`);
  }
 
  excluirHistoricoCompra(idHistorico: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.comprasUrl}/deletarHistorico/${idHistorico}`);
  }

  excluirMultiHistoricosCompras(ids: number[]): Observable<boolean> {
    const params = new URLSearchParams();
    ids.forEach(id => params.append('ids', String(id)));
    return this.http.delete<boolean>(`${this.comprasUrl}/deletarMultiHistoricosCompra?${params.toString()}`);
  }
 
}

