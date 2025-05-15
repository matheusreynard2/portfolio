// src/app/components/geolocalizacao/geolocalizacao.component.ts
import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Geolocalizacao } from '../../model/geolocalizacao';
import { GeocodingResult } from '../../model/endereco-geolocalizacao';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { GoogleMapsLoaderService } from '../../service/geolocalizacao/google-maps-loader.service';
import {DeviceService} from '../../service/device/device.service';
import { FormsModule } from '@angular/forms';
import {NgxMaskDirective, NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import {Fornecedor} from '../../model/fornecedor';
import {EnderecoFornecedor} from '../../model/endereco-fornecedor';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {firstValueFrom} from 'rxjs';
import {GoogleMap, MapMarker} from '@angular/google-maps';
import {MatCard, MatCardContent} from '@angular/material/card';
import {FornecedorService} from '../../service/fornecedor/fornecedor.service';

@Component({
  selector: 'app-geolocalizacao2',
  templateUrl: './geolocalizacao2.component.html',
  imports: [
    //MapMarker,
    //GoogleMap,
    //NgIf,
    //MatCard,
    //MatCardContent,
    //NgOptimizedImage,
    RouterLink,
    FormsModule,
    NgxMaskDirective,
    NgOptimizedImage,
    NgIf,
    GoogleMap,
    MapMarker,
    MatCard,
    MatCardContent
  ],
  providers: [
    provideNgxMask() // Esta linha é essencial!
  ],
  styleUrl: './geolocalizacao2.component.css'
})
export class Geolocalizacao2Component implements OnInit {

  @ViewChild('modalConsent', {static: true}) modalConsent!: TemplateRef<any>;
  @ViewChild('modalMsgAviso') modalMsgAviso!: TemplateRef<any>;

  enderecoGeolocalizacao: GeocodingResult | null = null;
  carregando = false; // Alterado para false inicialmente
  erro: string | null = null;
  mapsCarregado = false;
  carregandoEndereco = false;
  isMobileOrTablet: boolean = false;
  latitude: number = 0;
  longitude: number = 0;
  carregandoCep: boolean = false;
  carregandoTexto: string = '';
  apiLoaded = false;
  mensagemModal: string = '';
  obteveCoordenadas: boolean = false;

  endereco: EnderecoFornecedor = {
    cep: '',
    logradouro: '',
    complemento: '',
    unidade: '',
    bairro: '',
    localidade: '',
    uf: '',
    estado: '',
    regiao: '',
    ibge: '',
    gia: '',
    ddd: '',
    siafi: '',
    erro: ''
  };

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
      animation: (typeof google !== 'undefined' && google.maps && google.maps.Animation)
        ? google.maps.Animation.DROP
        : null
    }
  };

  constructor(
    private geoService: GeolocalizacaoService,
    private modalService: NgbModal,
    private router: Router,
    private mapsLoaderService: GoogleMapsLoaderService,
    private deviceService: DeviceService,
    private fornecedorService: FornecedorService
  ) {
  }

  ngOnInit(): void {
    this.fornecedorService.acessarPaginaFornecedor().subscribe();
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });
    this.carregarGoogleMaps().then(r => this.mapsCarregado = true);
  }

  async carregarGoogleMaps(): Promise<void> {
    try {
      // Verifica se o Google Maps já está disponível globalmente
      if (typeof google === 'undefined' || !google.maps) {
        await this.mapsLoaderService.loadGoogleMaps();
      }

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

  async buscarEnderecoPorCep(cep: string): Promise<void> {
    // Remover caracteres não numéricos do CEP
    const cepFormatado = cep.replace(/\D/g, '');

    if (cepFormatado.length !== 8) {
      this.mensagemModal = "CEP inválido. Por favor, digite um CEP com 8 dígitos.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Ativa estado de carregamento
    this.carregando = true;
    this.carregandoTexto = 'Buscando endereço...';

    // Primeira chamada (obter endereço)
    this.endereco = await firstValueFrom(
      this.geoService.obterEnderecoViaCEP(cepFormatado)
    );

    // Verifica erro
    if (this.endereco.erro === "true") {
      this.carregando = false;
      this.mensagemModal = "CEP não encontrado.";
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    // Atualiza o endereço
    this.endereco = {
      ...this.endereco,
      complemento: this.endereco.complemento || '',
      unidade: this.endereco.unidade || '',
      ibge: this.endereco.ibge || '',
      gia: this.endereco.gia || '',
      siafi: this.endereco.siafi || ''
    };

    this.carregandoTexto = 'Obtendo coordenadas geográficas...';

    // Segunda chamada (obter coordenadas) - usando try/catch para gerenciar a flag
    try {
      const coordenadas = await firstValueFrom(
        this.geoService.obterCoordenadasPorCEP(cepFormatado)
      );
      // Se chegou aqui, significa que obteve as coordenadas com sucesso
      this.obteveCoordenadas = true;
      this.latitude = coordenadas.latitude;
      this.longitude = coordenadas.longitude;
      this.mensagemModal = "Localização encontrada!"
      this.geoService.obterEnderecoDetalhado(this.latitude, this.longitude).subscribe({
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
    } catch (coordError) {
      // Falha ao obter coordenadas
      this.obteveCoordenadas = false;
      this.mensagemModal = "Não foi possível obter as coordenadas para este CEP.";
    } finally {
      // Desativa carregamento independentemente do resultado da segunda chamada
      this.carregando = false;
      this.modalService.open(this.modalMsgAviso);
    }
  }
}
