import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { importProvidersFrom } from '@angular/core';

import { routes } from './app.routes';
import {AuthInterceptor} from './auth.interceptor';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

export const appConfig: ApplicationConfig = {
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    provideZoneChangeDetection({ eventCoalescing: true}),
    provideRouter(routes),
    importProvidersFrom(HttpClientModule), provideAnimationsAsync()
  ]
};
