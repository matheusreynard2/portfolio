import {Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../service/auth/auth.service';
import { MatCardModule } from '@angular/material/card';
import { Usuario } from '../../model/usuario';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    NgbModule,
    NgOptimizedImage,
    MatCardModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  usuario: Usuario = {
    idUsuario: 0,
    login: '',
    senha: '',
    token: '',
    imagem: '',
    email: '',
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
      next: (response: Map<string, any>) => {
        if (response.has('usuario')) {
          let usuario: Usuario;
          usuario = <Usuario>response.get('usuario');
          this.authService.removerUsuarioLogado()
          this.authService.adicionarUsuarioLogado(usuario)
          this.authService.removerToken()
          this.authService.adicionarToken(usuario.token)
          this.authService.removerTokenExpirado()
          this.router.navigate(['/produtos'])

        }
      },
      error: (error) => {
        // Tratamento do erro 401 CREDENCIAIS INVALIDAS
        if (error.status === 401 && error.error && error.error.msgCredenciaisInvalidas) {
          setTimeout(() => {
            this.modalService.open(this.modalMsgCredenciais, {size: 'lg'});
          }, 100);
        } else {
          console.error('Erro ao realizar login:', error);
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
