import {Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../service/auth/auth.service';
import { MatCardModule } from '@angular/material/card';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { DeviceService } from '../../service/device/device.service';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    NgbModule,
    NgOptimizedImage,
    MatCardModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  providers: [provideNgxMask()]
})
export class LoginComponent implements OnInit {

  usuario: UsuarioDTO = {
    idUsuario: 0,
    login: '',
    senha: '',
    token: '',
    email: '',
    imagem: ''
  };

  @ViewChild('modalMsgToken') modalMsgToken: any
  @ViewChild('modalMsgCredenciais') modalMsgCredenciais: any
  mostrarSenha = false;
  isMobile: boolean = false;

  // Propriedade para alternar as imagens de fundo
  bgImgIndex = 0;
  private bgImgInterval: any;

  constructor(private authService: AuthService, private router: Router,
              private modalService: NgbModal, private deviceService: DeviceService) {}

  ngOnInit() {
    this.deviceService.isMobileOrTablet.subscribe(isMobile => this.isMobile = isMobile);
    // Mostrar MSG Token expirado
    if (this.authService.existeTokenExpirado()) {
      setTimeout(() => {
        this.modalService.open(this.modalMsgToken, {size: 'lg'});
      }, 100);
    }
    this.authService.removerTokenExpirado();

    // Alternância das imagens de fundo
    this.bgImgInterval = setInterval(() => {
      this.bgImgIndex = this.bgImgIndex === 0 ? 1 : 0;
    }, 5000);
  }

  ngOnDestroy() {
    if (this.bgImgInterval) {
      clearInterval(this.bgImgInterval);
    }
  }

  onSubmit() {
    this.authService.realizarLogin(this.usuario).subscribe({
      next: (res) => {
        // recebe accessToken no corpo e cookie HttpOnly de refresh via withCredentials
        this.authService.setAccessToken(res.accessToken);
        this.authService.adicionarUsuarioLogado(res.usuario);
        this.authService.removerTokenExpirado();
        this.router.navigate(['/produtos']);
      },
      error: (error) => {
        if (error.status === 401 && error?.error?.erro === 'CREDENCIAIS_INVALIDAS') {
          setTimeout(() => this.modalService.open(this.modalMsgCredenciais, { size: 'lg' }), 100);
        }
      }
    });
  }

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }

  cadastrarNovoUsuario() {
    this.router.navigate(['/cadastrar-usuario']);
  }
}
