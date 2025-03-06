import {Component, OnInit, ViewChild} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from './service/auth/auth.service';
import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {filter, firstValueFrom, Observable, of} from 'rxjs';
import {Usuario} from './model/usuario';
import {UsuarioService} from './service/usuario/usuario.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, FormsModule, HttpClientModule, NgbModule, NgIf, NgClass, NgOptimizedImage],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Sistema de Produtos';

  constructor(private authService: AuthService, private router: Router, private usuarioService: UsuarioService) { }

  mostrarPerfil: boolean = false;
  usuarioLogado!: Usuario;
  numeroVisitas: number = 0;
  private observableVisitas: Observable<number> = of(0)

  ngOnInit() {
    this.observableVisitas = this.usuarioService.addNovoAcessoIp()
    this.numeroVisitas
    this.observableVisitas.subscribe((valor: number) => {
      this.numeroVisitas = valor;
    });

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
        this.mostrarPerfil = !(rotaAtual.includes('login') || rotaAtual.includes('cadastrar-usuario'));
      }
    });
  }

  logout() {
    this.authService.logout()
  }
}
