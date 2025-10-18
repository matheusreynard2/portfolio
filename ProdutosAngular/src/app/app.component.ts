import {Component, HostListener, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {NgbModule, NgbToastModule} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from './service/auth/auth.service';
import {CommonModule} from '@angular/common';
import {filter, firstValueFrom} from 'rxjs';
import {UsuarioService} from './service/usuario/usuario.service';
import {DeviceService} from './service/device/device.service';
import {environment} from '../environments/environment';
import {UsuarioDTO} from './model/dto/UsuarioDTO';
import {DecimalPipe} from '@angular/common';

interface NavLink {
  label: string;
  routerLink?: string;
  external?: boolean;
  children?: NavLink[];
}

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, FormsModule, HttpClientModule, NgbModule, NgbToastModule, DecimalPipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Sistema de Produtos';

  constructor(private authService: AuthService,
              public router: Router,
              private usuarioService: UsuarioService,
              private deviceService: DeviceService) {
  }

  mostrarPerfil: boolean = false;
  usuarioLogado!: UsuarioDTO;
  numeroVisitas: number = 0;
  temaEscuro: boolean = false;
  isMobileOrTablet: boolean = false;
  isNavCollapsed: boolean = true;
  openDropdownIndex: number | null = null;
  navLinks: NavLink[] = [
    {label: 'Início', routerLink: '/login'},
    {
      label: 'Gerenciar produtos',
      children: [
        {label: 'Listar produtos', routerLink: '/produtos'},
        {label: 'Cadastrar produtos', routerLink: '/addproduto'},
        {label: 'Comprar produtos', routerLink: '/comprar-produtos'},
      ],
    },
    {
      label: 'Gerenciar fornecedores',
      children: [
        {label: 'Localizar fornecedor', routerLink: '/geoloc'},
        {label: 'Cadastrar fornecedor', routerLink: '/addfornecedor'},
        {label: 'Listar fornecedores', routerLink: '/listarfornecedores'},
      ],
    },
    {label: 'Ponto de venda', routerLink: '/pdv'},
    {label: 'Relatório financeiro', routerLink: '/relatorios'},
    {label: 'Sobre', routerLink: '/sobreTab1'},
    {label: 'Swagger', routerLink: 'https://www.sistemaprodify.com/swagger-ui/index.html', external: true},
  ];
  breadcrumbTrail: string[] = ['Início'];
  private readonly breadcrumbDefinitions: { pattern: RegExp; trail: string[] }[] = [
    {pattern: /^\/?$/, trail: ['Início']},
    {pattern: /^\/login$/, trail: ['Início']},
    {pattern: /^\/produtos$/, trail: ['Início', 'Gerenciar Produtos', 'Listar Produtos']},
    {pattern: /^\/addproduto$/, trail: ['Início', 'Gerenciar Produtos', 'Cadastrar Produtos']},
    {pattern: /^\/comprar-produtos$/, trail: ['Início', 'Gerenciar Produtos', 'Comprar Produtos']},
    {pattern: /^\/relatorios$/, trail: ['Início', 'Relatórios Financeiros']},
    {pattern: /^\/sobreTab1$/, trail: ['Início', 'Sobre', 'Sobre o Dev']},
    {pattern: /^\/sobreTab2$/, trail: ['Início', 'Sobre', 'Infos Sistema']},
    {pattern: /^\/geoloc$/, trail: ['Início', 'Gerenciar Fornecedores', 'Localizar Fornecedor']},
    {pattern: /^\/addfornecedor$/, trail: ['Início', 'Gerenciar Fornecedores', 'Cadastrar Fornecedor']},
    {pattern: /^\/listarfornecedores$/, trail: ['Início', 'Gerenciar Fornecedores', 'Listar Fornecedores']},
    {pattern: /^\/pdv$/, trail: ['Início', 'Ponto de Venda']},
    {pattern: /^\/editar-usuario$/, trail: ['Início', 'Perfil', 'Editar Usuário']},
  ];

  // Variáveis para contablização de acessos
  carregando: boolean = false;
  erro: string | null = null;

  ngOnInit() {
    this.authService.initActivityMonitor();

    const PUBLIC_ROUTES = ['/login', '/cadastrar-usuario', '/sobreTab1', '/sobreTab2'];
  
    const isPublicRoute = (path: string) =>
      PUBLIC_ROUTES.some(p => (path || '').startsWith(p));
  
    const evaluateRedirect = () => {
      // não avalia durante refresh de sessão
      if (this.authService.isRefreshing()) return;
  
      const hasToken = this.authService.existeToken();
      const active = this.authService.isActiveWithin(environment.INACTIVITY_WINDOW_MS);

      if (hasToken && !active) {
        this.authService.logout();;
      }
    };
  
    // ① Boot: tenta restaurar sessão via refresh cookie e então avalia
    this.authService.initSessionFromRefresh().finally(() => evaluateRedirect());

      // Removido: expiração será tratada pelos próprios components via catch dos endpoints
    // OBSERVER DE SELEÇÃO DO MENU PARA SABER SE ESTÁ ACESSANDO POR CELULAR/COMPUTADOR
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
      if (isMobile) {
        this.isNavCollapsed = true;
      } else {
        this.isNavCollapsed = false;
        this.openDropdownIndex = null;
      }
    });

    this.verificarExibicaoPerfil();
    this.verificarTema();
    this.updateBreadcrumbs(this.router.url);
    this.verificarAcessos();
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.authService.markNavigation();
      this.verificarExibicaoPerfil();
      // Reavalia redirecionamento a cada navegação
      evaluateRedirect();
      this.closeNavigation();
      this.updateBreadcrumbs(this.router.url);
    });

    this.authService.onActivity$().subscribe(() => evaluateRedirect());

  }

  private verificarAcessos() {
    this.usuarioService.addNovoAcessoIp().subscribe({
      next: () => {
        this.usuarioService.getAllAcessosIp().subscribe({
          next: (acessos) => {
            this.numeroVisitas = acessos;
          }
        });
      }
    });
  }

  private verificarTema() {
    const temaEscuro = localStorage.getItem('temaEscuro');
    this.temaEscuro = temaEscuro === 'true';
  }

  // Método separado para verificação do perfil
  private verificarExibicaoPerfil() {
    // Atualizar usuário logado
    this.usuarioLogado = this.authService.getUsuarioLogado();
    const rotaAtual = this.router.url;

    // CHECAGEM PARA EXIBIÇÃO DO PERFIL NO CANTO SUPERIOR ESQUERDO DA TELA
    if (!this.authService.existeToken()) {
      this.mostrarPerfil = false;
    } else {
      const rotasPublicas = ['login', 'cadastrar-usuario', 'sobreTab1', 'sobreTab2'];
      const estaEmRotaPublica = rotasPublicas.some(rota => rotaAtual.includes(rota));

      this.mostrarPerfil = !estaEmRotaPublica;
    }
  }

  async adicionarContabilizarAcessos(): Promise<void> {
    this.carregando = true;
    this.erro = null;
    try {
      const acessoRegistrado = await firstValueFrom(this.usuarioService.addNovoAcessoIp());
      acessoRegistrado
        ? this.numeroVisitas = await firstValueFrom(this.usuarioService.getAllAcessosIp())
        : this.erro = "Não foi possível registrar o acesso atual.";
    } catch (error) {
      console.error('Erro ao processar acessos:', error);
      this.erro = "Ocorreu um erro ao processar os dados de acesso.";
    } finally {
      this.carregando = false;
    }
    return Promise.resolve();
  }

  alternarTema() {
    this.temaEscuro = !this.temaEscuro;
    localStorage.setItem('temaEscuro', this.temaEscuro.toString());
  }

  logout() {
    this.authService.logout();
  }

  toggleNavbar() {
    if (!this.isMobileOrTablet) {
      return;
    }

    this.isNavCollapsed = !this.isNavCollapsed;
    if (this.isNavCollapsed) {
      this.openDropdownIndex = null;
    }
  }

  toggleDropdown(event: Event, index: number) {
    if (this.isMobileOrTablet) {
      event.preventDefault();
      event.stopPropagation();
      this.openDropdownIndex = this.openDropdownIndex === index ? null : index;
    }
  }

  handleNavInteraction() {
    if (this.isMobileOrTablet) {
      this.closeNavigation();
    }
  }

  closeNavigation() {
    if (this.isMobileOrTablet) {
      this.isNavCollapsed = true;
    } else {
      this.isNavCollapsed = false;
    }
    this.openDropdownIndex = null;
  }

  onMouseEnter(index: number) {
    if (!this.isMobileOrTablet) {
      this.openDropdownIndex = index;
    }
  }

  onMouseLeave(index: number) {
    if (!this.isMobileOrTablet && this.openDropdownIndex === index) {
      this.openDropdownIndex = null;
    }
  }

  private updateBreadcrumbs(url: string) {
    const cleanUrl = (url || '').split('?')[0];
    const match = this.breadcrumbDefinitions.find(def => def.pattern.test(cleanUrl));
    if (match) {
      this.breadcrumbTrail = match.trail;
      return;
    }

    const segments = cleanUrl.split('/').filter(Boolean);
    if (!segments.length) {
      this.breadcrumbTrail = ['Início'];
      return;
    }

    const friendlySegments = segments.map(segment => {
      const transformed = segment.replace(/-/g, ' ');
      return transformed.charAt(0).toUpperCase() + transformed.slice(1);
    });

    this.breadcrumbTrail = ['Início', ...friendlySegments];
  }
}

