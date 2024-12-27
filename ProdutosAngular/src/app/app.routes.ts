import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProdutoListComponent } from './components/listar/produto-list.component';
import {AddProdutoComponent} from './components/adicionar/add-produto.component';

export const routes: Routes = [
  { path: 'produtos', component: ProdutoListComponent },
  { path: 'addproduto', component: AddProdutoComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
