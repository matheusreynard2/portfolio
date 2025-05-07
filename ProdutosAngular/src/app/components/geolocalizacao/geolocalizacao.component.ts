// Corrigindo o componente geolocalizacao.component.ts

import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Geolocalizacao } from '../../model/geolocalizacao';
import { EnderecoGeolocalizacao, GeocodingResult } from '../../model/endereco-geolocalizacao';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { GoogleMapsLoaderService } from '../../service/geolocalizacao/google-maps-loader.service';
import { GoogleMap, MapMarker } from '@angular/google-maps';
import {NgIf, NgOptimizedImage} from '@angular/common';
import { catchError, finalize, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import {MatCard, MatCardContent} from '@angular/material/card';
import {DeviceService} from '../../service/device/device.service';
import { FormsModule } from '@angular/forms';
import {FornecedorService} from '../../service/fornecedor/fornecedor.service';

@Component({
  selector: 'app-geolocalizacao',
  templateUrl: './geolocalizacao.component.html',
  imports: [
    MapMarker,
    GoogleMap,
    NgIf,
    MatCard,
    MatCardContent,
    NgOptimizedImage,
    RouterLink,
    FormsModule
  ],
  styleUrl: './geolocalizacao.component.css'
})
export class GeolocalizacaoComponent implements OnInit {

  @ViewChild('modalConsent', { static: true }) modalConsent!: TemplateRef<any>;

  // Nova propriedade para armazenar o IP digitado pelo usuário
  ipDigitado: string = '';

  geoInfo: Geolocalizacao | null = null;
  enderecoGeolocalizacao: GeocodingResult | null = null;
  carregando = false;
  erro: string | null = null;
  consentimentoFornecido = false;
  mapsCarregado = false;
  carregandoEndereco = false;
  isMobileOrTablet: boolean = false;

  // Propriedades para o mapa
  center: google.maps.LatLngLiteral = {lat: -23.5505, lng: -46.6333}; // Coordenadas padrão (São Paulo)
  zoom = 16;
  mapOptions: google.maps.MapOptions = {
    mapTypeId: 'roadmap',
    zoomControl: true,
    scrollwheel: false,
    disableDoubleClickZoom: true,
    maxZoom: 20,
    minZoom: 4,
  };
  marker: any = {
    position: {lat: -23.5505, lng: -46.6333},
    title: 'Localização',
    options: {
      draggable: false
      // Não incluir a animação aqui
    }
  };

  // Importante: Adicione esta propriedade para verificar se a API do Google Maps está disponível
  apiLoaded = false;

  constructor(
    private geoService: GeolocalizacaoService,
    private mapsLoaderService: GoogleMapsLoaderService,
    private deviceService: DeviceService,
    private fornecedorService: FornecedorService
  ) { }

  ngOnInit(): void {
    // USA O ENDPOINT ABAIXO PARA VERIFICAR SE O TOKEN EXPIROU E LIBERAR ACESSO A PAGINA OU NÃO
    this.fornecedorService.acessarPaginaFornecedor().subscribe();

    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });

    // Carrega a API do Google Maps
    this.carregarGoogleMaps();
  }

  async carregarGoogleMaps(): Promise<void> {
    try {
      // Verifica se o Google Maps já está disponível globalmente
      if (typeof google === 'undefined' || !google.maps) {
        await this.mapsLoaderService.loadGoogleMaps();
      }

      // Após o carregamento, configura a animação do marcador
      this.mapsCarregado = true;
      this.apiLoaded = true;

      // Só configura a animação após garantir que o Google Maps está disponível
      if (google && google.maps && google.maps.Animation) {
        this.marker.options.animation = google.maps.Animation.DROP;
      }

    } catch (error) {
      console.error('Erro ao carregar o Google Maps:', error);
      this.erro = `Erro ao carregar o Google Maps: ${error}`;
    }
  }

  // Método para buscar geolocalização pelo IP digitado
  buscarGeolocalizacaoPorIP(): void {
    if (!this.ipDigitado) {
      this.erro = "Por favor, digite um endereço IP válido.";
      return;
    }

    // TIRA OS ESPAÇOS
    this.ipDigitado = this.ipDigitado.replace(/\s/g, '');

    this.erro = null;
    this.carregando = true;
    this.carregandoEndereco = true;

    this.geoService.obterGeolocalizacaoPorIP(this.ipDigitado)
      .pipe(
        switchMap((dados) => {
          this.geoInfo = dados;

          // Configurar o mapa com as coordenadas básicas primeiro
          const coordenadas = this.geoService.extrairCoordenadas(dados.loc);
          this.center = coordenadas;
          this.marker.position = coordenadas;
          this.marker.title = `${dados.city}, ${dados.region}, ${dados.country}`;

          // Agora buscar o endereço detalhado
          return this.geoService.obterEnderecoDetalhado(coordenadas.lat, coordenadas.lng)
            .pipe(
              catchError(error => {
                console.error('Erro ao obter endereço detalhado:', error);
                return of(null as any);
              })
            );
        }),
        finalize(() => {
          this.carregando = false;
          this.carregandoEndereco = false;
        })
      )
      .subscribe({
        next: (enderecoData) => {
          if (enderecoData && enderecoData.results && enderecoData.results.length > 0) {
            this.enderecoGeolocalizacao = enderecoData.results[0];

            // Atualizar o mapa com as coordenadas mais precisas, se disponíveis
            if (this.enderecoGeolocalizacao?.geometry && this.enderecoGeolocalizacao.geometry.location) {
              const preciseLoc = this.enderecoGeolocalizacao.geometry.location;
              this.center = { lat: preciseLoc.lat, lng: preciseLoc.lng };
              this.marker.position = { lat: preciseLoc.lat, lng: preciseLoc.lng };
              this.marker.title = this.enderecoGeolocalizacao.formatted_address;
            }
          }
        },
        error: (error) => {
          this.erro = `Erro ao processar dados de localização: ${error.message}`;
          this.carregando = false;
          this.carregandoEndereco = false;
        }
      });
  }

  // Método para extrair componentes específicos do endereço
  getAddressComponent(type: string): string {
    if (!this.enderecoGeolocalizacao || !this.enderecoGeolocalizacao.address_components) {
      return 'N/A';
    }

    const component = this.enderecoGeolocalizacao.address_components.find(comp =>
      comp.types.includes(type)
    );

    return component ? component.long_name : 'N/A';
  }
}
