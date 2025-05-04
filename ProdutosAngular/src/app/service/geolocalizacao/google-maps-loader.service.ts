// src/app/services/google-maps-loader.service.ts
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GoogleMapsLoaderService {
  private loadAPI: Promise<any> | null = null;
  private readonly API_KEY = environment.googleMapsApiKey;
  private readonly API_URL = 'https://maps.googleapis.com/maps/api/js';

  constructor() { }

  loadGoogleMaps(): Promise<any> {
    // Se já iniciou o carregamento, retorna a mesma Promise
    if (this.loadAPI) {
      return this.loadAPI;
    }

    // Verifica se o Google Maps já está carregado
    if (window.google && window.google.maps) {
      return Promise.resolve();
    }

    // Cria uma nova Promise para carregar o script
    this.loadAPI = new Promise((resolve, reject) => {
      // Cria e adiciona o script do Google Maps ao DOM
      const script = document.createElement('script');
      const apiKey = this.API_KEY ? `?key=${this.API_KEY}` : '';

      script.id = 'googleMaps';
      script.src = `${this.API_URL}${apiKey}&loading=async`;
      script.async = true;
      script.defer = true;

      // Adiciona handlers para carregamento e erro
      script.onload = () => {
        resolve(true);
      };

      script.onerror = (error) => {
        reject(new Error('Google Maps falhou ao carregar: ' + error));
      };

      document.body.appendChild(script);
    });

    return this.loadAPI;
  }
}
