import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProdutoService } from '../../service/produto.service';
import { Produto } from '../../model/produto';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-add-produto',
  templateUrl: './add-produto.component.html',
  imports: [
    FormsModule
  ],
  styleUrls: ['./add-produto.component.css']
})
export class AddProdutoComponent {

  // Objeto de produto que será preenchido pelo formulário
  produto = {
    nome: '',
    descricao: '',
    valor: 0,
    quantia: 0
  };

  private router: Router = new Router();

  constructor(
    private produtoService: ProdutoService) {
  }

  onSubmit() {
    const novoCadastro = this.produtoService.adicionarProduto(this.produto).subscribe();
    if (novoCadastro != null) {
      this.exibirAlerta("Produto " + this.produto.nome + " cadastrado com sucesso!");
      this.gotoUserList()
    }
  }

  gotoUserList() {
    this.router.navigate(['/produtos']);
  }

  exibirAlerta(mensagem: string) {
    window.alert(mensagem)
  }
}
