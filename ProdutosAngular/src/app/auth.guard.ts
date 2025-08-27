import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from './service/auth/auth.service';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  private static readonly PUBLIC_ROUTES = ['/login', '/cadastrar-usuario', '/sobreTab1', '/sobreTab2'];

  constructor(private auth: AuthService, private router: Router) {}

  private isPublic(path: string): boolean {
    return AuthGuard.PUBLIC_ROUTES.some(p => (path || '').startsWith(p));
  }

  async canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    const path = state.url || this.router.url || '';

    // 1) Rota pública: sempre permite
    if (this.isPublic(path)) {
      return true;
    }
    
    // 2) Se já possui token: só então aplica regra de inatividade
    if (this.auth.existeToken()) {
      const active = this.auth.isActiveWithin(environment.INACTIVITY_WINDOW_MS);
      if (!active) {
        this.auth.logoutPorInatividade(); // <— sinaliza e sai
        return false;
      }
      // Ativo e com token: libera e marca navegação
      this.auth.markNavigation();
      return true;
    }

    // 4) Ativo <30s e sem token: tenta restaurar via refresh cookie (F5)
    const ok = await this.auth.initSessionFromRefresh();
    if (ok && this.auth.existeToken()) {
      this.auth.markNavigation();
      return true;
    }

    // 5) Falhou: limpa e volta ao login
    this.auth.removerChaves();
    return false;
  }
}
