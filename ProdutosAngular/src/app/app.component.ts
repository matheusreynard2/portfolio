import {Component, OnInit} from '@angular/core';
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
  secondaryRow?: boolean;
}

interface BreadcrumbItem {
  label: string;
  url?: string;
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
    {label: 'Relatório financeiro', routerLink: '/relatorios', secondaryRow: true},
    {label: 'Sobre', routerLink: '/sobreTab1', secondaryRow: true},
    {label: 'Swagger', routerLink: 'https://www.sistemaprodify.com/swagger-ui/index.html', external: true, secondaryRow: true},
  ];
  breadcrumbTrail: BreadcrumbItem[] = [{label: 'Início', url: '/login'}];
  private readonly breadcrumbConfig: Record<string, BreadcrumbItem[]> = {
    '/login': [{label: 'Início', url: '/login'}],
    '/produtos': [
      {label: 'Início', url: '/login'},
      {label: 'Gerenciar Produtos'},
      {label: 'Listar Produtos'}
    ],
    '/addproduto': [
      {label: 'Início', url: '/login'},
      {label: 'Gerenciar Produtos'},
      {label: 'Cadastrar Produtos'}
    ],
    '/comprar-produtos': [
      {label: 'Início', url: '/login'},
      {label: 'Gerenciar Produtos'},
      {label: 'Comprar Produtos'}
    ],
    '/geoloc': [
      {label: 'Início', url: '/login'},
      {label: 'Gerenciar Fornecedores'},
      {label: 'Localizar Fornecedor'}
    ],
    '/addfornecedor': [
      {label: 'Início', url: '/login'},
      {label: 'Gerenciar Fornecedores'},
      {label: 'Cadastrar Fornecedor'}
    ],
    '/listarfornecedores': [
      {label: 'Início', url: '/login'},
      {label: 'Gerenciar Fornecedores'},
      {label: 'Listar Fornecedores'}
    ],
    '/relatorios': [
      {label: 'Início', url: '/login'},
      {label: 'Relatório Financeiro'}
    ],
    '/sobreTab1': [
      {label: 'Início', url: '/login'},
      {label: 'Sobre'},
      {label: 'Sobre o Dev'}
    ],
    '/sobreTab2': [
      {label: 'Início', url: '/login'},
      {label: 'Sobre'},
      {label: 'Infos Sistema'}
    ],
    '/pdv': [
      {label: 'Início', url: '/login'},
      {label: 'Ponto de Venda'}
    ],
    '/editar-usuario': [
      {label: 'Início', url: '/login'},
      {label: 'Perfil'},
      {label: 'Editar Usuário'}
    ]
  };
  private readonly routeLabelMap: Record<string, string> = {
    '/login': 'Início',
    '/produtos': 'Listar Produtos',
    '/addproduto': 'Cadastrar Produtos',
    '/comprar-produtos': 'Comprar Produtos',
    '/geoloc': 'Localizar Fornecedor',
    '/addfornecedor': 'Cadastrar Fornecedor',
    '/listarfornecedores': 'Listar Fornecedores',
    '/relatorios': 'Relatório Financeiro',
    '/sobreTab1': 'Sobre',
    '/sobreTab2': 'Infos Sistema',
    '/pdv': 'Ponto de Venda',
    '/editar-usuario': 'Editar Usuário'
  };

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
    const configTrail = this.breadcrumbConfig[cleanUrl];
    if (configTrail) {
      this.breadcrumbTrail = configTrail;
      return;
    }

    const segments = cleanUrl.split('/').filter(Boolean);
    if (!segments.length) {
      this.breadcrumbTrail = [{label: 'Início', url: '/login'}];
      return;
    }

    const trail: BreadcrumbItem[] = [{label: 'Início', url: '/login'}];
    let accumulated = '';
    segments.forEach((segment, index) => {
      accumulated += '/' + segment;
      const label = this.routeLabelMap['/' + segment] || segment.replace(/-/g, ' ');
      const formattedLabel = label.charAt(0).toUpperCase() + label.slice(1);
      trail.push({label: formattedLabel, url: index === segments.length - 1 ? accumulated : undefined});
    });

    this.breadcrumbTrail = trail;
  }

  get primaryNavLinks(): NavLink[] {
    return this.navLinks.filter(link => !link.secondaryRow);
  }

  get secondaryNavLinks(): NavLink[] {
    return this.navLinks.filter(link => link.secondaryRow);
  }

  navigateToBreadcrumb(index: number) {
    if (index < 0 || index >= this.breadcrumbTrail.length) {
      return;
    }

    const target = this.breadcrumbTrail[index];
    if (target.url) {
      this.router.navigate([target.url]);
      return;
    }

    const matchedRoute = Object.entries(this.routeLabelMap).find(([, label]) => label === target.label);
    if (matchedRoute) {
      this.router.navigate([matchedRoute[0]]);
    }
  }
}

