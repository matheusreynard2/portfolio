import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { GeolocalizacaoDTO } from '../../model/dto/GeolocalizacaoDTO';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { EnderecoGeolocalizacaoDTO } from '../../model/dto/EnderecoGeolocalizacaoDTO';
import { EnderecoFornecedorDTO } from '../../model/dto/EnderecoFornecedorDTO';
import { HttpBaseService } from '../base/http-base.service';
import { LatitudeLongitudeDTO } from '../../model/dto/LatitudeLongitudeDTO';

@Injectable({
  providedIn: 'root'
})
export class GeolocalizacaoService extends HttpBaseService {

  private localizacaoUrl: string;
  private apiUrl = environment.API_URL;

  constructor(private http: HttpClient, authService: AuthService, router: Router) {
    super(authService, router);
    this.localizacaoUrl = this.apiUrl + '/localizacao';
  }

  obterGeolocalizacaoPorIP(ipAddress: string): Observable<GeolocalizacaoDTO> {
    return this.http.get<GeolocalizacaoDTO>(this.localizacaoUrl + "/localizarIp/" + ipAddress);
  }

  obterEnderecoDetalhado(lat: number, lng: number): Observable<EnderecoGeolocalizacaoDTO> {
    let params = new HttpParams()
      .set('lat', lat.toString())
      .set('lng', lng.toString());

    return this.http.get<EnderecoGeolocalizacaoDTO>(this.localizacaoUrl + "/enderecoDetalhado", {params});
  }

  // Método auxiliar para extrair coordenadas do campo 'loc'
  extrairCoordenadas(loc: string): { lat: number, lng: number } {
    const [lat, lng] = loc.split(',').map(coord => parseFloat(coord.trim()));
    return { lat, lng };
  }

  // OBTEM ENDEREÇO DETALHADO DE LOCALIZAÇÃO ATRAVÉS DO CEP
  obterEnderecoViaCEP(cep: string): Observable<EnderecoFornecedorDTO> {
    // Remove caracteres não numéricos do CEP
    cep = cep.replace(/\D/g, '');
    // Chama o endpoint do backend que fará a integração com a API de CEP
    return this.http.get<EnderecoFornecedorDTO>(this.localizacaoUrl + "/consultarCEP/" + cep);
  }

  // OBTEM LATITUDE/LONGITUDE ATRAVÉS DO CEP
  obterCoordenadasPorCEP(cep: string): Observable<LatitudeLongitudeDTO> {
    return this.http.get<LatitudeLongitudeDTO>(this.localizacaoUrl + "/obterCoordenadas/" + cep, {
        params: {cep: cep.replace(/\D/g, '')}
      }
    );
  }
}
