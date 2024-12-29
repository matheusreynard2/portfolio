import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProdutoService } from '../../service/produto.service';
import {FormsModule} from '@angular/forms';
import {Produto} from '../../model/produto';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-add-produto',
  templateUrl: './add-produto.component.html',
  imports: [
    FormsModule,
    CurrencyPipe
  ],
  styleUrls: ['./add-produto.component.css']
})

export class AddProdutoComponent {

  // É criado um produto zerado para poder ser acesso pelo formulário HTML, mas as propriedades são alteradas
  // pelos valores inseridos no formulário, então são passadas para o service da API.
  produto: Produto = {
    id: 0,
    nome: '',
    descricao: '',
    valor: 0,
    quantia: 0
  };

  novoProduto!: Produto;

  private router: Router = new Router();

  constructor(
    private produtoService: ProdutoService) {
  }

  // Método que é chamado ao clicar no botão Submit do formulário HTML de criar produto
  onSubmit() {
    this.produtoService.adicionarProduto(this.produto).subscribe({
      next: (produtoAdicionado: Produto) => {
        // Atribui o Produto retornado (produtoAdicionado) ao novoProduto
        this.novoProduto = produtoAdicionado

        // Se o novoProduto, que recebeu o produto que retornou da API, foi criado e adicionado no banco, o ID será diferente de 0
        if (this.novoProduto.id != 0) {
          this.exibirAlerta("Produto " + this.produto.nome + " cadastrado com sucesso!");
          this.gotoUserList()
        }
      }
    })
  }

  // Método que navega para a página de listagem de produtos
  gotoUserList() {
    this.router.navigate(['/produtos']);
  }

  // Método que exibe uma mensagem passada como parâmetro
  exibirAlerta(mensagem: string) {
    window.alert(mensagem)
  }

}
