import {Component, OnInit, ViewChild} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {HttpClientModule, HttpResponse} from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from './service/auth/auth.service';
import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {filter, firstValueFrom, Observable, of} from 'rxjs';
import {Usuario} from './model/usuario';
import {UsuarioService} from './service/usuario/usuario.service';
import {DeviceService} from './service/device/device.service';
import {environment} from '../environments/environment';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, FormsModule, HttpClientModule, NgbModule, NgIf, NgClass],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Sistema de Produtos';

  constructor(private authService: AuthService,
              private router: Router,
              private usuarioService: UsuarioService,
              private deviceService: DeviceService) {
  }

  mostrarPerfil: boolean = false;
  usuarioLogado!: Usuario;
  numeroVisitas: number = 0;
  private observableVisitas: Observable<HttpResponse<number>> = of(new HttpResponse<number>({body: 0}));
  temaEscuro: boolean = false;
  isMobileOrTablet: boolean = false;
  menuOpen: boolean = false;

  ngOnInit() {

    //this.carregarGoogleMapsScript();

    // OBSERVER DE SELEÇÃO DO MENU PARA SABER SE ESTÁ ACESSANDO POR CELULAR/COMPUTADOR
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
      // Se voltar para desktop, garante que o menu mobile seja fechado.
      if (!this.isMobileOrTablet) {
        this.menuOpen = false;
      }
    });

    // CARREGAR MODO CLAR/ESCURO E ACESSAR ENDPOINT DE CONTADOR DE IPs
    this.carregarTema();
    this.observableVisitas = this.usuarioService.addNovoAcessoIp()
    this.observableVisitas.subscribe((response: HttpResponse<number>) => {
      this.numeroVisitas = response.body !== null ? response.body : 0;
    });

    // CHECA SE HÁ USUÁRIO LOGADO PARA REDIRECIONAR PARA AS ROTAS AUTENTICADAS OU NÃO
    this.usuarioLogado = this.authService.getUsuarioLogado()
    this.router.events.pipe(
      filter((event)  => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.usuarioLogado = this.authService.getUsuarioLogado()
      const rotaAtual = this.router.url;

      // CHECAGEM PARA EXIBIÇÃO DO PERFIL NO CANTO SUPERIOR ESQUERDO DA TELA
      if (!this.authService.existeToken()) {
          this.mostrarPerfil = false;
      } else if (this.authService.existeToken()) {
        this.mostrarPerfil = !(rotaAtual.includes('login') || rotaAtual.includes('cadastrar-usuario')
        || rotaAtual.includes('sobreTab1') || rotaAtual.includes('sobreTab2'));
      }
    });
  }

  // CARREGAR TEMA CLARO/ESCURO
  carregarTema() {
    const temaEscuro = localStorage.getItem('temaEscuro');
    if (temaEscuro === 'true') {
      this.temaEscuro = true;
      document.body.classList.add('dark-mode');
      console.log('carregou escuro')
    } else {
      this.temaEscuro = false;
      document.body.classList.remove('dark-mode');
      console.log('carregou claro')
    }
  }

  // ALTERNA ENTRE TEMAS CLARO/ESCURO
  alternarTema() {
    this.temaEscuro = !this.temaEscuro
    localStorage.setItem('temaEscuro', this.temaEscuro.toString());
    if (this.temaEscuro) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
  }

  // FAZ LOGOUT DO USUÁRIO
  logout() {
    this.authService.logout()
  }

  abrirFecharMenu() {
    this.menuOpen = !this.menuOpen;
  }

  carregarGoogleMapsScript() {
    const script = document.createElement('script');
    script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}`;
    script.async = true;
    script.defer = true;
    document.head.appendChild(script);
  }

}
