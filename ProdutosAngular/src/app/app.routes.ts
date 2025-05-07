import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProdutoListComponent } from './components/listar-produto/produto-list.component';
import {AddProdutoComponent} from './components/adicionar-produto/add-produto.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './auth.guard';
import {AddUsuarioComponent} from './components/adicionar-usuario/add-usuario.component';
import {SobreTab1Component} from './components/sobre/sobreTab1.component';
import {SobreTab2Component} from './components/sobre/sobreTab2.component';
import {GeolocalizacaoComponent} from './components/geolocalizacao/geolocalizacao.component';
import {Geolocalizacao2Component} from './components/geolocalizacao/geolocalizacao2.component';
import {GerenciarFornecedorComponent} from './components/gerenciar-fornecedor/gerenciar-fornecedor.component';

export const routes: Routes = [
  { path: 'produtos', component: ProdutoListComponent, canActivate: [AuthGuard] },
  { path: 'addproduto', component: AddProdutoComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'cadastrar-usuario', component: AddUsuarioComponent },
  { path: 'sobreTab1', component: SobreTab1Component },
  { path: 'sobreTab2', component: SobreTab2Component },
  { path: 'geoloc', component: GeolocalizacaoComponent, canActivate: [AuthGuard]  },
  { path: 'geoloc2', component: Geolocalizacao2Component, canActivate: [AuthGuard]  },
  { path: 'fornecedor', component: GerenciarFornecedorComponent, canActivate: [AuthGuard]  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
