import {Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../service/auth/auth.service';
import { MatCardModule } from '@angular/material/card';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

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

  constructor(private authService: AuthService, private router: Router,
              private modalService: NgbModal) {}

  ngOnInit() {
    // Mostrar MSG Token expirado
    if (this.authService.existeTokenExpirado()) {
      setTimeout(() => {
        this.modalService.open(this.modalMsgToken, {size: 'lg'});
      }, 100);
    }
    this.authService.removerTokenExpirado()
  }

  onSubmit() {
    this.authService.realizarLogin(this.usuario).subscribe({
      next: (response: UsuarioDTO) => {
        if (response && response.token) {
          this.authService.logout(); // Limpa todos os dados do storage
          this.authService.adicionarUsuarioLogado(response);
          this.authService.adicionarToken(response.token);
          this.authService.removerTokenExpirado();
          this.router.navigate(['/produtos']);
        }
      },
      error: (error) => {
        // Tratamento do erro 401 CREDENCIAIS INVALIDAS
        if (error.status === 401) {
          const errorBody = error.error;
          if (errorBody.erro === 'CREDENCIAIS_INVALIDAS') {
            setTimeout(() => {
              this.modalService.open(this.modalMsgCredenciais, {size: 'lg'});
            }, 100);
          }
        }
      }
    })
  }

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }

  cadastrarNovoUsuario() {
    this.router.navigate(['/cadastrar-usuario']);
  }
}
