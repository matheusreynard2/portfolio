// src/app/service/geolocalizacao/geolocalizacao.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Geolocalizacao } from '../../model/geolocalizacao';
import { EnderecoGeolocalizacao } from '../../model/endereco-geolocalizacao';

@Injectable({
  providedIn: 'root'
})
export class GeolocalizacaoService {

  private localizacaoUrl: string;
  private apiUrl = environment.API_URL;

  constructor(private http: HttpClient) {
    this.localizacaoUrl = this.apiUrl + '/localizacao';
  }

  obterGeolocalizacaoUsuario(): Observable<Geolocalizacao> {
    return this.http.get<Geolocalizacao>(this.localizacaoUrl + "/localizarIp");
  }

  obterEnderecoDetalhado(lat: number, lng: number): Observable<any> {
    let params = new HttpParams()
      .set('lat', lat.toString())
      .set('lng', lng.toString());

    return this.http.get<any>(this.localizacaoUrl + "/enderecoDetalhado", { params });
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
}
