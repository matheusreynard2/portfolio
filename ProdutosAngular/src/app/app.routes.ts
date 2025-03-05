import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProdutoListComponent } from './components/listar-produto/produto-list.component';
import {AddProdutoComponent} from './components/adicionar-produto/add-produto.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './auth.guard';
import {AddUsuarioComponent} from './components/adicionar-usuario/add-usuario.component';

export const routes: Routes = [
  { path: 'produtos', component: ProdutoListComponent, canActivate: [AuthGuard] },
  { path: 'addproduto', component: AddProdutoComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'cadastrar-usuario', component: AddUsuarioComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
