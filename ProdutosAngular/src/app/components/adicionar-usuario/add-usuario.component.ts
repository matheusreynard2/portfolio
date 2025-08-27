import {Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {UsuarioService} from '../../service/usuario/usuario.service';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeviceService} from '../../service/device/device.service';
import {MatCard, MatCardContent} from '@angular/material/card';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';
import { UtilsService } from '../../service/utils/utils.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-usuario',
  imports: [
    FormsModule,
    NgOptimizedImage,
    NgIf,
    MatCard,
    MatCardContent,
    CommonModule
  ],
  templateUrl: './add-usuario.component.html',
  styleUrl: './add-usuario.component.css'
})
export class AddUsuarioComponent implements OnInit {

  addUsuario: UsuarioDTO = {
    idUsuario: 0,
    login: '',
    senha: '',
    token: '',
    email: '',
    imagem: null
  };

  private modalService: NgbModal = new NgbModal();
  usuarioNovo!: UsuarioDTO;
  adicionouUsuario: boolean = false;
  @ViewChild('modalMsgLoginExistente') modalMsgLoginExistente: any
  @ViewChild('modalMsgAddUser') modalMsgAddUser: any
  @ViewChild('modalAvisoArquivo') modalAvisoArquivo: any
  imagemFile: File = new File([], '', {})
  isMobileOrTablet: boolean = false;
  mostrarSenha = false;
  mensagemErro: string = '';
  mensagemSucesso: string = '';
  avisoTextoModal: string = '';

  constructor(private usuarioService: UsuarioService, private router: Router, private deviceService: DeviceService, private utils: UtilsService) {}

  ngOnInit() {
    this.adicionouUsuario = false;
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });
  }

  onSubmit() {
    // Adiciona no banco depois chama a mensagem de sucesso de adição de usuario "msgAddUsuario"
    this.usuarioService.adicionarUsuario(this.addUsuario, this.imagemFile).subscribe({
      next: (usuarioAdicionado: UsuarioDTO) => {
        this.usuarioNovo = usuarioAdicionado
        this.adicionouUsuario = true
        this.msgAddUsuario(this.modalMsgAddUser)
      },
      error: (error: HttpErrorResponse) => {
        // Se o erro for de login repetido, exibe a mensagem de erro.
        if (error.status === 409 ) {
          const errorBody = error.error;
          if (errorBody.erro === 'LOGIN_JA_EXISTE') {
            setTimeout(() => {
              this.modalService.open(this.modalMsgLoginExistente, {size: 'sm'});
            }, 100);
          }
        }
      }
    })
  }

  // Função que abre o modal de mensagem de sucesso após cadastrar um usuário
  msgAddUsuario(modalMsgAddUser: any) {
    if (this.adicionouUsuario) {
      setTimeout(() => {
        this.modalService.open(modalMsgAddUser, {size: 'sm'});
      }, 100);
    }
  }

  voltarTelaLogin() {
    this.router.navigate(['/login']);
  }

  onFileChange(event: any) {
    const file: File = event.target.files && event.target.files[0];
    if (!file) return;
    const valid = this.utils.validarImagem(file);
    if (!valid.ok) {
      const texto = valid.motivo === 'tipo'
        ? 'Tipo de arquivo inválido. Selecione apenas .jpg ou .png.'
        : 'Arquivo muito grande. Tamanho máximo permitido: 20MB.';
      this.avisoTextoModal = texto;
      try { this.modalService.open(this.modalAvisoArquivo, { size: 'sm' }); } catch { alert(texto); }
      try { event.target.value = null; } catch {}
      this.imagemFile = new File([], '', {});
      return;
    }
    this.imagemFile = file;
  }

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }

}
