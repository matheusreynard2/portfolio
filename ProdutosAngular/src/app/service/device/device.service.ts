// src/app/service/device/device.service.ts
import { Injectable } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, map, shareReplay } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  // Um observable que emite o estado atual (mobile/tablet ou desktop)
  isMobileOrTablet: Observable<boolean>;

  constructor(private breakpointObserver: BreakpointObserver) {
    // Inicializa o observable e compartilha o mesmo valor para todos os subscribers
    this.isMobileOrTablet = this.breakpointObserver.observe([
      Breakpoints.Handset,
      Breakpoints.Tablet
    ]).pipe(
      map(result => result.matches),
      shareReplay(1) // Compartilha o último valor emitido com novos subscribers
    );
  }

  // Método para obter o valor síncrono atual (usado em situações onde um observable não é ideal)
  getCurrentDeviceState(): boolean {
    // Verifica se matches é true para handset ou tablet
    return this.breakpointObserver.isMatched([
      Breakpoints.Handset,
      Breakpoints.Tablet
    ]);
  }
}
