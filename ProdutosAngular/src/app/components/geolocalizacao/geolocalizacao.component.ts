// src/app/components/geolocalizacao/geolocalizacao.component.ts
import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Geolocalizacao } from '../../model/geolocalizacao';
import { EnderecoGeolocalizacao, GeocodingResult } from '../../model/endereco-geolocalizacao';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { GoogleMapsLoaderService } from './google-maps-loader.service';
import { GoogleMap, MapMarker } from '@angular/google-maps';
import { NgIf } from '@angular/common';
import { catchError, finalize, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-geolocalizacao',
  templateUrl: './geolocalizacao.component.html',
  imports: [
    MapMarker,
    GoogleMap,
    NgIf
  ],
  styleUrl: './geolocalizacao.component.css'
})
export class GeolocalizacaoComponent implements OnInit {

  @ViewChild('modalConsent', { static: true }) modalConsent!: TemplateRef<any>;

  geoInfo: Geolocalizacao | null = null;
  enderecoGeolocalizacao: GeocodingResult | null = null;
  carregando = true;
  erro: string | null = null;
  consentimentoFornecido = false;
  mapsCarregado = false;
  carregandoEndereco = false;

  // Propriedades para o mapa
  center: google.maps.LatLngLiteral = {lat: 0, lng: 0};
  zoom = 16; // Zoom maior para visualizar melhor o endereço específico
  mapOptions: google.maps.MapOptions = {
    mapTypeId: 'roadmap',
    zoomControl: true,
    scrollwheel: false,
    disableDoubleClickZoom: true,
    maxZoom: 20,
    minZoom: 4,
  };
  marker = {
    position: {lat: 0, lng: 0},
    title: '',
    options: {
      draggable: false,
      animation: google.maps.Animation.DROP
    }
  };

  constructor(
    private geoService: GeolocalizacaoService,
    private modalService: NgbModal,
    private router: Router,
    private mapsLoaderService: GoogleMapsLoaderService
  ) { }

  ngOnInit(): void {
    // Já tenta carregar o Google Maps no início
    this.carregarGoogleMaps();

    // Abre o modal de consentimento ao inicializar o componente
    this.exibirModalConsentimento();
  }

  carregarGoogleMaps(): void {
    // Vamos verificar se o Google Maps já está disponível
    if (window.google && window.google.maps) {
      this.mapsCarregado = true;
      return;
    }

    // Se não estiver disponível, carregamos explicitamente
    this.mapsLoaderService.loadGoogleMaps()
      .then(() => {
        this.mapsCarregado = true;
      })
      .catch(error => {
        this.erro = `Erro ao carregar o Google Maps: ${error}`;
      });
  }

  exibirModalConsentimento(): void {
    const modalRef = this.modalService.open(this.modalConsent, {
      backdrop: 'static',
      keyboard: false,
      centered: true
    });
  }

  fornecerConsentimento(): void {
    this.consentimentoFornecido = true;
    this.modalService.dismissAll();

    // Agora já podemos carregar as informações de geolocalização
    this.carregarGeolocalizacao();
  }

  recusarConsentimento(): void {
    this.modalService.dismissAll();
    this.router.navigate(['/login']);
  }

  carregarGeolocalizacao(): void {
    this.carregando = true;
    this.carregandoEndereco = true;

    this.geoService.obterGeolocalizacaoUsuario()
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
