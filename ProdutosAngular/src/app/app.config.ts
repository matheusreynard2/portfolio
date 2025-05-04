import {APP_INITIALIZER, ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { importProvidersFrom } from '@angular/core';

import { routes } from './app.routes';
import {AuthInterceptor} from './auth.interceptor';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {environment} from '../environments/environment';
import {GoogleMapsModule} from '@angular/google-maps';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

// Função opcional para pré-carregar o Google Maps durante a inicialização da aplicação
function initGoogleMaps(): () => Promise<any> {
  return () => {
    // Só inicia o carregamento, mas não espera terminar para iniciar a aplicação
    return new Promise<void>((resolve) => {
      // Verifica se já está carregado
      if (window.google && window.google.maps) {
        resolve();
        return;
      }

      // Carrega de forma assíncrona
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}&loading=async`;
      script.async = true;
      script.defer = true;

      script.onload = () => resolve();
      script.onerror = () => {
        console.error('Falha ao carregar o Google Maps API');
        resolve(); // Resolve mesmo com erro para não bloquear a inicialização da aplicação
      };

      document.head.appendChild(script);
    });
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    importProvidersFrom(GoogleMapsModule),
    importProvidersFrom(NgbModule),
    {
      provide: APP_INITIALIZER,
      useFactory: initGoogleMaps,
      multi: true
    },
    provideRouter(routes),
    provideZoneChangeDetection({ eventCoalescing: true}),
    importProvidersFrom(HttpClientModule), provideAnimationsAsync()
  ]
};
