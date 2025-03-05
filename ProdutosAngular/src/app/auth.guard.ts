import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import {AuthService} from './service/auth/auth.service';

@Injectable({
  providedIn: 'root'
})

export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

    // Usando o método existeToken para verificar a autenticação
    if (this.authService.existeToken()) {
      return true;  // Se o usuário estiver autenticado, permite o acesso
    } else {
      this.router.navigate(['/login']);  // Se não estiver autenticado, redireciona para a página de login
      return false;
    }
  }
}
