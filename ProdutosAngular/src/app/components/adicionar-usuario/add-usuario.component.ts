import {Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Usuario} from '../../model/usuario';
import {Router} from '@angular/router';
import {UsuarioService} from '../../service/usuario/usuario.service';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeviceService} from '../../service/device/device.service';

@Component({
  selector: 'app-add-usuario',
  imports: [
    FormsModule,
    NgOptimizedImage,
    NgIf
  ],
  templateUrl: './add-usuario.component.html',
  styleUrl: './add-usuario.component.css'
})
export class AddUsuarioComponent implements OnInit {

  addUsuario: Usuario = {
    id: 0,
    login: '',
    senha: '',
    token: '',
    imagem: '',
    nome: '',
    linkedin: '',
    whatsapp: '',
    endereco:''
  };

  private modalService: NgbModal = new NgbModal();
  usuarioNovo!: Usuario;
  adicionouUsuario: boolean = false;
  @ViewChild('modalMsgLoginExistente') modalMsgLoginExistente: any
  @ViewChild('modalMsgAddUser') modalMsgAddUser: any
  @ViewChild('foto-perfil') fotoPerfil: any
  imagemFile: File = new File([], '', {})
  isMobileOrTablet: boolean = false;

  constructor(private usuarioService: UsuarioService, private router: Router, private deviceService: DeviceService) {}

  ngOnInit() {
    this.adicionouUsuario = false;
    this.deviceService.isMobileOrTablet.subscribe(isMobile => {
      this.isMobileOrTablet = isMobile;
    });
  }

  onSubmit() {
    // Adiciona no banco depois chama a mensagem de sucesso de adição de usuario "msgAddUsuario"
    this.usuarioService.adicionarUsuario(this.addUsuario, this.imagemFile).subscribe({
      next: (usuarioAdicionado: any) => {
        this.usuarioNovo = usuarioAdicionado
        this.adicionouUsuario = true
        this.msgAddUsuario(this.modalMsgAddUser)
      },
      error: (error) => {
        // Se o erro for de login repetido, exibe a mensagem de erro.
        if (error.status === 409 && error.error === 'Login já cadastrado no banco de dados.') {
          setTimeout(() => {
            this.modalService.open(this.modalMsgLoginExistente, {size: 'sm'});
          }, 100);
        }
      }
    })
  }

  // Função que abre o modal de mensagem de sucesso após cadastrar um usuário
  msgAddUsuario(modalMsgAddUser: any) {
    if (this.adicionouUsuario) {
      setTimeout(() => {
        const modalRef = this.modalService.open(modalMsgAddUser);
        modalRef.componentInstance.addUsuario = this.usuarioNovo;
      }, 100);
    }
  }

  voltarTelaLogin() {
    this.router.navigate(['/login']);
  }

  onFileChange(event: any) {
    this.imagemFile = event.target.files[0];
  }

}
