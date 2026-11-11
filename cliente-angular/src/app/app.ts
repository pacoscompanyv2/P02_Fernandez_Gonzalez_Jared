import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CarritoService } from './core/carrito.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header>
      <h1>Ventas TCSW</h1>
      <nav>
        <a routerLink="/catalogo" routerLinkActive="activo">Catalogo</a>
        <a routerLink="/carrito" routerLinkActive="activo">Carrito ({{ carrito.cantidadTotal() }})</a>
        <a routerLink="/venta" routerLinkActive="activo">Venta</a>
        <a routerLink="/consulta" routerLinkActive="activo">Consulta</a>
      </nav>
    </header>
    <main>
      <router-outlet />
    </main>
  `,
  styles: `
    header { padding: .8rem 1.2rem; border-bottom: 1px solid #8884; }
    h1 { margin: 0 0 .4rem; font-size: 1.3rem; }
    nav { display: flex; gap: 1rem; }
    nav a { text-decoration: none; padding: .2rem .1rem; }
    nav a.activo { font-weight: 700; border-bottom: 2px solid currentColor; }
    main { padding: 1rem 1.2rem; max-width: 60rem; }
  `,
})
export class App {
  protected readonly carrito = inject(CarritoService);
}
