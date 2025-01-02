import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProdutoService } from '../../service/produto.service';
import {FormsModule} from '@angular/forms';
import {Produto} from '../../model/produto';
import {CurrencyPipe, NgOptimizedImage} from '@angular/common';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-add-produto',
  templateUrl: './add-produto.component.html',
  imports: [
    FormsModule,
    CurrencyPipe,
    NgOptimizedImage
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
  modalRef!: NgbModalRef;

  private router: Router = new Router();
  private modalService: NgbModal = new NgbModal();

  constructor(
    private produtoService: ProdutoService) {
  }

  // Método que é chamado ao clicar no botão Submit do formulário HTML ao criar um produto
  onSubmit() {
    this.produtoService.adicionarProduto(this.produto).subscribe({
      next: (produtoAdicionado: Produto) => {
        // Atribui o Produto retornado (produtoAdicionado) ao novoProduto
        this.novoProduto = produtoAdicionado
      }
    })
  }

  // Método que navega para a página de listagem de produtos
  gotoUserList() {
    this.router.navigate(['/produtos']);
  }

  // Método que abre o modal de mensagem de sucesso após cadastrar um produto
  msgAddProduto(modalMsg: any) {
    const modalRef = this.modalService.open(modalMsg);
    // Espera 0.3 segundos para setar o produto no modal por que ele chama no HTML o submit e click ao mesmo tempo
    // E se chamar os dois eventos ao mesmo tempo, o submit não consegue gravar no banco de dados
    // E depois que seta o produto no modal, ele chama e abre o modal
    setTimeout(() => {
      modalRef.componentInstance.produto = this.novoProduto;
    }, 300);
  }

}
