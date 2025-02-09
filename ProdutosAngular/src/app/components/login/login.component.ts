import {Component, OnInit, ViewChild} from '@angular/core';
import {Usuario} from '../../model/usuario';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../service/auth/auth.service';
import {Router} from '@angular/router';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    NgbModule,
    NgOptimizedImage
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  usuario: Usuario = {
    id: 0,
    login: '',
    senha: '',
    token: '',
    imagem: ''
  };

  @ViewChild('modalMsgToken') modalMsgToken: any
  @ViewChild('modalMsgCredenciais') modalMsgCredenciais: any

  constructor(private authService: AuthService, private router: Router,
              private modalService: NgbModal) {}

  ngOnInit() {
    // Mostrar MSG Token expirado
    if (this.authService.existeTokenExpirado() && !this.authService.existeToken()) {
      setTimeout(() => {
        this.modalService.open(this.modalMsgToken, {size: 'lg'});
      }, 100);
    }
    this.authService.removerToken()
    this.authService.removerTokenExpirado()
  }

  onSubmit() {

    this.authService.realizarLogin(this.usuario).subscribe({
      next: (response: Map<string, any>) => {
        if (response.has('usuario')) {
          let usuario: Usuario;
          usuario = <Usuario>response.get('usuario');
          this.usuario = usuario
          this.authService.adicionarToken(usuario.token)
          this.authService.adicionarUsuarioLogado(usuario)
          console.log("token " + this.authService.existeToken() + ", tokenExpirado " + !this.authService.existeTokenExpirado())
          this.router.navigate(['/produtos']);
        }
      },
      error: (error) => {
        if (error.status === 401 && error.error.message === 'Credenciais inválidas.') {
          setTimeout(() => {
            this.modalService.open(this.modalMsgCredenciais, {size: 'lg'});
          }, 100);
        }
      }
    })
  }

  cadastrarNovoUsuario() {
    this.router.navigate(['/cadastrar-usuario']);
  }
}
