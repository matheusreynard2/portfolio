import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import { environment } from '../../../environments/environment';
import { Geolocalizacao } from '../../model/geolocalizacao';
import {AuthService} from '../auth/auth.service';
import {Router} from '@angular/router';
import {EnderecoFornecedor} from '../../model/endereco-fornecedor';

@Injectable({
  providedIn: 'root'
})
export class GeolocalizacaoService {

  private localizacaoUrl: string;
  private apiUrl = environment.API_URL;

  constructor(private http: HttpClient, private authService: AuthService, private router: Router) {
    this.localizacaoUrl = this.apiUrl + '/localizacao';
  }

  // Se o token expirou, remove o token
  public verificarRedirecionar() {
    if (this.authService.existeToken()) {
      this.authService.removerToken()
      this.authService.adicionarTokenExpirado('true')
      this.router.navigate(['/login']);
    }
  }

  obterGeolocalizacaoPorIP(ipAddress: string): Observable<Geolocalizacao> {
    return this.http.get<Geolocalizacao>(this.localizacaoUrl + "/localizarIp/" + ipAddress).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        else if (error.status === 403)
          this.verificarRedirecionar()
        return throwError(error);
      })
    )
  }

  obterEnderecoDetalhado(lat: number, lng: number): Observable<any> {
    let params = new HttpParams()
      .set('lat', lat.toString())
      .set('lng', lng.toString());

    return this.http.get<any>(this.localizacaoUrl + "/enderecoDetalhado", { params }).pipe(
      // Aqui fazemos o tratamento do erro 401 para TOKEN EXPIRADO
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && error.error.message === 'Tempo limite de conexão com o sistema excedido. TOKEN Expirado')
          this.verificarRedirecionar()
        else if (error.status === 403)
          this.verificarRedirecionar()
        return throwError(error);
      })
    )
  }

  // Método auxiliar para extrair coordenadas do campo 'loc'
  extrairCoordenadas(loc: string): {lat: number, lng: number} {
    if (!loc) {
      return {lat: 0, lng: 0};
    }

    const coords = loc.split(',');
    if (coords.length !== 2) {
      return {lat: 0, lng: 0};
    }

    return {
      lat: parseFloat(coords[0]),
      lng: parseFloat(coords[1])
    };
  }

  obterEnderecoViaCEP(cep: string): Observable<EnderecoFornecedor> {
    // Remove caracteres não numéricos do CEP
    cep = cep.replace(/\D/g, '');
    // Chama o endpoint do backend que fará a integração com a API de CEP
    return this.http.get<EnderecoFornecedor>(this.localizacaoUrl + "/consultarCEP/" + cep);
  }

}
