import {Component, OnInit, ViewChild} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {HttpClientModule, HttpResponse} from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from './service/auth/auth.service';
import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {filter, firstValueFrom, Observable, of} from 'rxjs';
import {UsuarioService} from './service/usuario/usuario.service';
import {DeviceService} from './service/device/device.service';
import {environment} from '../environments/environment';
import { UsuarioDTO } from './model/dto/UsuarioDTO';

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
  usuarioLogado!: UsuarioDTO;
  numeroVisitas: number = 0;
  temaEscuro: boolean = false;
  isMobileOrTablet: boolean = false;
  menuOpen: boolean = false;

  // Variáveis para contablização de acessos
  carregando: boolean = false;
  erro: string | null = null;

  ngOnInit() {
    // OBSERVER DE SELEÇÃO DO MENU PARA SABER SE ESTÁ ACESSANDO POR CELULAR/COMPUTADOR
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
      // Se voltar para desktop, garante que o menu mobile seja fechado.
      if (!this.isMobileOrTablet) {
        this.menuOpen = false;
      }
    });

    this.verificarExibicaoPerfil();
    this.verificarTema();
    this.verificarAcessos();
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.verificarExibicaoPerfil();
    });

    // Adiciona event listeners para os sub-menus mobile
    setTimeout(() => {
      const submenuHeaders = document.querySelectorAll('.mobile-submenu-header');
      submenuHeaders.forEach(header => {
        header.addEventListener('click', (event) => {
          this.toggleSubmenu(event);
        });
      });
    }, 0);
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
    const tema = localStorage.getItem('tema');
    if (tema === 'escuro') {
      document.body.classList.add('tema-escuro');
    } else {
      document.body.classList.remove('tema-escuro');
    }
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
      // Segunda chamada somente se a primeira retornar true
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

  // CARREGAR TEMA CLARO/ESCURO
  carregarTema() {
    const temaEscuro = localStorage.getItem('temaEscuro');
    if (temaEscuro === 'true') {
      this.temaEscuro = true;
      document.body.classList.add('dark-mode');
    } else {
      this.temaEscuro = false;
      document.body.classList.remove('dark-mode');
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
    // Executa o logout
    this.authService.logout()
  }

  abrirFecharMenu() {
    this.menuOpen = !this.menuOpen;
    if (!this.menuOpen) {
      // Fecha todos os sub-menus quando o menu principal é fechado
      const submenus = document.querySelectorAll('.mobile-submenu');
      submenus.forEach(submenu => {
        submenu.classList.remove('active');
      });
    }
  }

  toggleSubmenu(event: Event) {
    const header = event.currentTarget as HTMLElement;
    const submenu = header.closest('.mobile-submenu');
    if (submenu) {
      submenu.classList.toggle('active');
    }
  }

}
