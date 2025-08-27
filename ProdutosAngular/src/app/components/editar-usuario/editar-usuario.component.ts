import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgIf, NgOptimizedImage } from '@angular/common';
import { Router } from '@angular/router';
import { MatCard, MatCardContent } from '@angular/material/card';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { UtilsService } from '../../service/utils/utils.service';
import { DeviceService } from '../../service/device/device.service';
import { UsuarioService } from '../../service/usuario/usuario.service';
import { AuthService } from '../../service/auth/auth.service';
import { UsuarioDTO } from '../../model/dto/UsuarioDTO';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-editar-usuario',
  imports: [FormsModule, CommonModule, MatCard, MatCardContent, NgxMaskDirective],
  templateUrl: './editar-usuario.component.html',
  styleUrl: './editar-usuario.component.css',
  providers: [provideNgxMask()]
})
export class EditarUsuarioComponent implements OnInit {
  usuario: UsuarioDTO = {
    idUsuario: 0,
    login: '',
    senha: '',
    token: '',
    email: '',
    imagem: null
  };
  imagemFile: null | File = null;
  mostrarSenha = false;
  atualizarSenha = false;
  isMobile = false;
  avisoTextoModal: string = '';

  @ViewChild('modalSucesso') modalSucesso: any;
  @ViewChild('modalErro') modalErro: any;
  @ViewChild('modalAvisoArquivo') modalAvisoArquivo: any;
  private modalRef?: NgbModalRef;

  constructor(
    private usuarioService: UsuarioService,
    private auth: AuthService,
    private router: Router,
    private modalService: NgbModal,
    private deviceService: DeviceService,
    private utils: UtilsService
  ) {}

  ngOnInit(): void {
    this.deviceService.isMobileOrTablet.subscribe(isMob => this.isMobile = isMob);
    const id = this.auth.getUsuarioLogado()?.idUsuario;
    if (!id) {
      this.router.navigate(['/login']);
      return;
    }
    this.usuarioService.buscarUsuarioPorId(id).subscribe({
      next: (user) => {
        // Não permitir edição do login (apenas e-mail, senha, foto)
        this.usuario = { ...user, login: user.login ?? '' } as UsuarioDTO;
        // Limpa campo de nova senha ao iniciar a página
        this.usuario.senha = '';
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }

  onFileChange(event: any) {
    const file: File = event.target.files && event.target.files[0];
    if (!file) return;
    const valid = this.utils.validarImagem(file);
    if (!valid.ok) {
      const texto = valid.motivo === 'tipo'
        ? 'Tipo de arquivo inválido. Selecione apenas .jpg ou .png.'
        : 'Arquivo muito grande. Tamanho máximo permitido: 20MB.';
      this.showSmallModal(texto);
      try { event.target.value = null; } catch {}
      this.imagemFile = null;
      return;
    }
    this.imagemFile = file;
  }

  private showSmallModal(texto: string) {
    try {
      (this as any).avisoTextoModal = texto;
      this.modalService.open(this.modalAvisoArquivo, { size: 'sm' });
    } catch {
      alert(texto);
    }
  }

  salvar() {
    const id = this.auth.getUsuarioLogado()?.idUsuario ?? 0;
    const payload: UsuarioDTO = {
      ...this.usuario,
      idUsuario: id,
      login: this.usuario.login,
       // bloqueado visualmente; mantido igual
    };
    this.usuarioService.atualizarUsuario(id, payload, this.imagemFile!).subscribe({
      next: (user) => {
        // Atualiza cache do usuário logado
        this.auth.adicionarUsuarioLogado(user);
        // Reflete o saldo imediatamente no perfil
        const current = this.auth.getUsuarioLogado();
        current.saldo = user.saldo;
        current.imagem = user.imagem;
        this.auth.adicionarUsuarioLogado(current);
        this.modalRef = this.modalService.open(this.modalSucesso, { size: 'sm' });
      },
      error: () => {
        this.modalRef = this.modalService.open(this.modalErro, { size: 'sm' });
      }
    });
  }

  onSaldoChange(masked: string) {
    // Converte "R$ 1.234,56" ou "1.234,56" para número 1234.56
    if (masked == null) { this.usuario.saldo = 0 as any; return; }
    const onlyNums = masked.replace(/[^0-9,-.]/g, '').replace(/\./g, '').replace(',', '.');
    const parsed = parseFloat(onlyNums);
    this.usuario.saldo = isNaN(parsed) ? (0 as any) : (parsed as any);
  }

  fecharTemplateModal() {
    try { this.modalRef?.close(); } catch {}
  }
}


