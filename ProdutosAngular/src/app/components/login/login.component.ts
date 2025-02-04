import {Component, OnInit, ViewChild} from '@angular/core';
import {Usuario} from '../../model/usuario';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../service/auth/auth.service';
import {Router} from '@angular/router';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {ProdutoService} from '../../service/produto/produto.service';
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
    token: ''
  };

  @ViewChild('modalMsgToken') modalMsgToken: any
  @ViewChild('modalMsgCredenciais') modalMsgCredenciais: any

  constructor(private authService: AuthService, private router: Router,
              private produtoService: ProdutoService, private modalService: NgbModal) {}

  ngOnInit() {
    // Mostrar MSG Token expirado
    this.produtoService.tokenObservable.subscribe((tokenExpirado: any) => {
      if (tokenExpirado) {
        setTimeout(() => {
          this.modalService.open(this.modalMsgToken, { size: 'lg' });
        }, 100);
      }
    })

    // Exibir MSG de credenciais invalidas
    this.authService.credenciaisObservable.subscribe((credenciaisInvalidas) => {
      if (credenciaisInvalidas) {
        setTimeout(() => {
          this.modalService.open(this.modalMsgCredenciais, { size: 'lg' });
        }, 100);
      }
    })
  }

  onSubmit() {
    this.authService.realizarLogin(this.usuario)
  }

  cadastrarNovoUsuario() {
    this.router.navigate(['/cadastrar-usuario']);
  }
}
