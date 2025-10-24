import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {NgbModule, NgbToastModule} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from './service/auth/auth.service';
import {CommonModule, DecimalPipe} from '@angular/common';
import {filter, firstValueFrom} from 'rxjs';
import {UsuarioService} from './service/usuario/usuario.service';
import {DeviceService} from './service/device/device.service';
import {environment} from '../environments/environment';
import {UsuarioDTO} from './model/dto/UsuarioDTO';

interface NavLink {
  label: string;
  routerLink?: string;
  external?: boolean;
  children?: NavLink[];
  secondaryRow?: boolean;
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
  navigationHistory: { label: string; route: string }[] = [];
  private readonly maxHistoryItems = 5;
  private readonly routeLabels: Record<string, string> = {
    '/login': 'Início',
    '/cadastrar-usuario': 'Cadastrar usuário',
    '/sobreTab1': 'Sobre o Dev',
    '/sobreTab2': 'Infos Sistema',
    '/geoloc': 'Localizar fornecedor',
    '/geoloc2': 'Geolocalização',
    '/addfornecedor': 'Cadastrar fornecedor',
    '/listarfornecedores': 'Listar fornecedores',
    '/pdv': 'Ponto de venda',
    '/editar-usuario': 'Configurações do usuário',
    '/relatorios': 'Relatório financeiro',
  };
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

  carregando: boolean = false;
  erro: string | null = null;

  ngOnInit() {
    this.authService.initActivityMonitor();
  
    const evaluateRedirect = () => {
      if (this.authService.isRefreshing()) return;
  
      const hasToken = this.authService.existeToken();
      const active = this.authService.isActiveWithin(environment.INACTIVITY_WINDOW_MS);

      if (hasToken && !active) {
        this.authService.logout();;
      }
    };
  
    this.authService.initSessionFromRefresh().finally(() => evaluateRedirect());

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
    this.verificarAcessos();
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(event => {
      const navigationEnd = event as NavigationEnd;
      this.authService.markNavigation();
      this.verificarExibicaoPerfil();
      this.updateNavigationHistory(navigationEnd.urlAfterRedirects);
      evaluateRedirect();
      this.closeNavigation();
    });

    this.authService.onActivity$().subscribe(() => evaluateRedirect());

  }

  private verificarAcessos() {
    this.usuarioService.addNovoAcessoIp().subscribe({
      next: (novoAcesso: boolean) => {
        this.usuarioService.getAllAcessosIp().subscribe({
          next: (acessos) => {
            const incremento = typeof novoAcesso === 'boolean' && novoAcesso ? 1 : 0;
            this.numeroVisitas = (acessos ?? 0) + incremento;
          }
        });
      }
    });
  }

  private verificarTema() {
    const temaEscuro = localStorage.getItem('temaEscuro');
    this.temaEscuro = temaEscuro === 'true';
  }

  private verificarExibicaoPerfil() {
    this.usuarioLogado = this.authService.getUsuarioLogado();
    this.mostrarPerfil = this.authService.existeToken() && !!this.usuarioLogado;
  }

  async adicionarContabilizarAcessos(): Promise<void> {
    this.carregando = true;
    this.erro = null;
    try {
      const acessoRegistrado = await firstValueFrom(this.usuarioService.addNovoAcessoIp());
      if (typeof acessoRegistrado === 'boolean') {
        const acessos = await firstValueFrom(this.usuarioService.getAllAcessosIp());
        this.numeroVisitas = (acessos ?? 0) + (acessoRegistrado ? 1 : 0);
      } else {
        const acessos = await firstValueFrom(this.usuarioService.getAllAcessosIp());
        this.numeroVisitas = acessos ?? 0;
      }
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
    this.mostrarPerfil = false;
    this.usuarioLogado = {} as UsuarioDTO;
    this.navigationHistory = [];
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

  handleNavInteraction(route?: string) {
    if (route === '/login' && this.authService.existeToken()) {
      this.logout();
      return;
    }

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

  get primaryNavLinks(): NavLink[] {
    return this.navLinks.filter(link => !link.secondaryRow);
  }

  get secondaryNavLinks(): NavLink[] {
    return this.navLinks.filter(link => link.secondaryRow);
  }

  private updateNavigationHistory(url: string) {
    const route = url.split('?')[0];
    const label = this.getLabelForRoute(route);

    if (!label) {
      return;
    }

    const existingIndex = this.navigationHistory.findIndex(item => item.route === route);
    if (existingIndex !== -1) {
      this.navigationHistory.splice(existingIndex, 1);
    }

    this.navigationHistory.push({label, route});

    if (this.navigationHistory.length > this.maxHistoryItems) {
      this.navigationHistory = this.navigationHistory.slice(-this.maxHistoryItems);
    }
  }

  private getLabelForRoute(route: string): string | null {
    if (!route) {
      return null;
    }

    const labelFromNav = this.findLabelInNavLinks(route, this.navLinks);
    if (labelFromNav) {
      return labelFromNav;
    }

    if (this.routeLabels[route]) {
      return this.routeLabels[route];
    }

    return null;
  }

  private findLabelInNavLinks(route: string, links: NavLink[]): string | null {
    for (const link of links) {
      if (link.routerLink === route) {
        return link.label;
      }

      if (link.children?.length) {
        const childLabel = this.findLabelInNavLinks(route, link.children);
        if (childLabel) {
          return childLabel;
        }
      }
    }

    return null;
  }
}

