import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ExportarRelatorioPayload {
  titulo: string;
  colunas: string[];
  linhas: Array<Record<string, any>>;
  paisagem: boolean;
  rodapeDireita?: string;
  colunasDetalhes?: string[];
  linhasDetalhadas?: string[][][];
}

@Injectable({ providedIn: 'root' })
export class RelatoriosService {
  private readonly relatorioUrl = `${environment.API_URL}/relatorios`;

  constructor(private readonly http: HttpClient) {}

  exportarPdf(payload: ExportarRelatorioPayload): Observable<Blob> {
    return this.http.post(`${this.relatorioUrl}/pdf`, payload, {
      responseType: 'blob'
    });
  }
}

