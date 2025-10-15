// src/app/components/geolocalizacao/geolocalizacao2.component.ts
// src/app/components/geolocalizacao/geolocalizacao2.component.ts
import {
  Component,
  OnInit,
  AfterViewInit,
  OnDestroy,
  TemplateRef,
  ViewChild,
  ElementRef,
  Inject,
  PLATFORM_ID,
  ChangeDetectorRef
} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule, NgIf, NgOptimizedImage, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GeolocalizacaoService } from '../../service/geolocalizacao/geolocalizacao.service';
import { GeocodingResultDTO } from '../../model/dto/EnderecoGeolocalizacaoDTO';
import { EnderecoFornecedorDTO } from '../../model/dto/EnderecoFornecedorDTO';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { FornecedorService } from '../../service/fornecedor/fornecedor.service';
import { GoogleMapsLoaderService } from '../../service/geolocalizacao/google-maps-loader.service';
import { DeviceService } from '../../service/device/device.service';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { Subscription, firstValueFrom } from 'rxjs';
import { GoogleMap, MapMarker } from '@angular/google-maps';
import { GoogleMap as GoogleMapElement } from '@angular/google-maps';

@Component({
  selector: 'app-geolocalizacao2',
  templateUrl: './geolocalizacao2.component.html',
  imports: [
    RouterLink,
    FormsModule,
    NgxMaskDirective,
    NgIf,
    GoogleMap,
    MapMarker
  ],
  providers: [provideNgxMask()],
  styleUrl: './geolocalizacao2.component.css'
})
export class Geolocalizacao2Component implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('modalMsgAviso') modalMsgAviso!: TemplateRef<any>;

  @ViewChild('gm', { read: ElementRef }) mapHostEl?: ElementRef<HTMLElement>;

  private io?: IntersectionObserver; // observer ffseguro
  private destroyed = false;

  enderecoGeolocalizacao: GeocodingResultDTO | null = null;
  carregando = false;
  erro: string | null = null;
  mapsCarregado = false;
  carregandoEndereco = false;
  isMobileOrTablet = false;
  latitude: number | null = null;
  longitude: number | null = null;  
  carregandoCep = false;
  carregandoTexto = '';
  apiLoaded = false;
  mensagemModal = '';
  obteveCoordenadas = false;
  endereco: EnderecoFornecedorDTO = this.criarEnderecoVazio();
  cepBusca = '';
  private enderecoDetalhadoSub?: Subscription;
  mapHeight = '480px';

  // Propriedades para o mapa
  center: google.maps.LatLngLiteral = { lat: 0, lng: 0 };
  zoom = 16;
  mapOptions: google.maps.MapOptions = {
    mapTypeId: 'roadmap',
    zoomControl: true,
    scrollwheel: false,
    disableDoubleClickZoom: true,
    maxZoom: 20,
    minZoom: 4
  };

  marker: any = {
    position: { lat: -23.5505, lng: -46.6333 },
    title: 'Localização',
    options: { draggable: false }
  };

  constructor(
    private geoService: GeolocalizacaoService,
    private modalService: NgbModal,
    private router: Router,
    private mapsLoaderService: GoogleMapsLoaderService,
    private deviceService: DeviceService,
    private fornecedorService: FornecedorService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.fornecedorService.acessarPaginaFornecedor().subscribe();
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });
    // NÃO carregue o Maps aqui; espere o DOM existir (ngAfterViewInit)
  }

  async ngAfterViewInit(): Promise<void> {
    if (!isPlatformBrowser(this.platformId)) return;

    // Garante que o host do <google-map> existe antes de qualquer observer
    const host = this.mapHostEl?.nativeElement ?? null;

    // Carrega Google Maps e só depois configura marker/anim
    await this.carregarGoogleMaps();

    // Cria um IntersectionObserver com guardas de segurança
    if ('IntersectionObserver' in window) {
      if (host instanceof Element) {
        this.io = new IntersectionObserver((entries) => {
          // exemplo: pausar/retomar interações só quando visível
          entries.forEach(e => {
            // e.isIntersecting ? ... : ...
          });
        }, { threshold: 0.05 });
        // 🔒 Só observe se for um Element válido
        this.io.observe(host);
      } else {
        // host não disponível — evita chamar observe(null/undefined)
        // opcional: tentar novamente no próximo tick
        queueMicrotask(() => {
          const lateHost = this.mapHostEl?.nativeElement ?? null;
          if (lateHost instanceof Element) {
            this.io = new IntersectionObserver(() => {}, { threshold: 0.05 });
            this.io.observe(lateHost);
          }
        });
      }
    }
  }

  ngOnDestroy(): void {
    this.destroyed = true;
    if (this.io) {
      this.io.disconnect();
      this.io = undefined;
    }
    if (this.enderecoDetalhadoSub) {
      this.enderecoDetalhadoSub.unsubscribe();
      this.enderecoDetalhadoSub = undefined;
    }
  }

  private async carregarGoogleMaps(): Promise<void> {
    try {
      // Verifica e carrega a API quando necessário
      const hasGoogle = typeof window !== 'undefined'
        && (window as any).google
        && (window as any).google.maps;

      if (!hasGoogle) {
        await this.mapsLoaderService.loadGoogleMaps();
      }

      this.mapsCarregado = true;
      this.apiLoaded = true;

      // Configura animação do marcador com checagem defensiva
      const g: any = (window as any).google;
      if (g?.maps?.Animation) {
        this.marker = {
          ...this.marker,
          options: {
            ...(this.marker.options || {}),
            animation: g.maps.Animation.DROP
          }
        };
      }

      this.cdr.detectChanges();

    } catch (error: any) {
      console.error('Erro ao carregar o Google Maps:', error);
      this.erro = `Erro ao carregar o Google Maps: ${error?.message || error}`;
    }
  }

  private criarEnderecoVazio(): EnderecoFornecedorDTO {
    return {
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
      erro: 'false'
    };
  }

  async buscarEnderecoPorCep(cep: string): Promise<void> {
    const cepFormatado = (cep || '').replace(/\D/g, '');
    if (cepFormatado.length !== 8) {
      this.mensagemModal = 'CEP inválido. Por favor, digite um CEP com 8 dígitos.';
      this.modalService.open(this.modalMsgAviso);
      return;
    }

    this.carregando = true;
    this.carregandoTexto = 'Buscando endereço...';

    try {
      const endereco = await firstValueFrom(this.geoService.obterEnderecoViaCEP(cepFormatado));
      if (endereco.erro === 'true') {
        this.mensagemModal = 'CEP não encontrado.';
        this.modalService.open(this.modalMsgAviso);
        return;
      }

      this.endereco = {
        ...endereco,
        complemento: endereco.complemento ?? '',
        unidade: endereco.unidade ?? '',
        ibge: endereco.ibge ?? '',
        gia: endereco.gia ?? '',
        siafi: endereco.siafi ?? '',
        erro: endereco.erro ?? 'false'
      };
      this.cepBusca = this.endereco.cep;

      this.carregandoTexto = 'Obtendo coordenadas geográficas...';
      this.carregandoEndereco = true;

      const coordenadas = await firstValueFrom(this.geoService.obterCoordenadasPorCEP(cepFormatado));
      this.obteveCoordenadas = true;
      this.latitude = coordenadas.latitude;
      this.longitude = coordenadas.longitude;

      this.center = { lat: this.latitude, lng: this.longitude };
      this.marker = {
        ...this.marker,
        position: { lat: this.latitude, lng: this.longitude },
        title: `CEP ${this.endereco.cep}`
      };

      //this.map?.panTo(this.center)

      this.enderecoDetalhadoSub?.unsubscribe();
      this.enderecoDetalhadoSub = this.geoService.obterEnderecoDetalhado(this.latitude, this.longitude)
        .subscribe({
          next: (enderecoData) => {
            const result = enderecoData?.results?.[0];
            if (result?.geometry?.location) {
              const precise = result.geometry.location;
              this.enderecoGeolocalizacao = result;
              this.center = { lat: precise.lat, lng: precise.lng };
              this.marker = {
                ...this.marker,
                position: { lat: precise.lat, lng: precise.lng },
                title: result.formatted_address
              };
            }
          },
          error: (error) => {
            this.erro = `Erro ao processar dados de localização: ${error.message}`;
          }
        });

    } catch (error: any) {
      this.obteveCoordenadas = false;
      if (error?.status === 400 && error?.error?.erro === 'ERRO_OBTER_CEP') {
        this.mensagemModal = 'Não foi possível obter latitude e longitude para este CEP.';
      } else if (error?.status === 400) {
        this.mensagemModal = 'CEP inválido ou não encontrado.';
      } else {
        this.mensagemModal = 'Erro ao buscar coordenadas. Tente novamente.';
      }
      this.erro = error?.message || 'Erro ao buscar dados do CEP. Tente novamente mais tarde.';
      this.modalService.open(this.modalMsgAviso);
    } finally {
      this.carregando = false;
      this.carregandoEndereco = false;
    }
  }
}
