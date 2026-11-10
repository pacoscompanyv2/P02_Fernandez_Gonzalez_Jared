import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'catalogo' },
  { path: 'catalogo', loadComponent: () => import('./catalogo/catalogo').then((m) => m.Catalogo) },
  { path: 'carrito', loadComponent: () => import('./carrito/carrito').then((m) => m.Carrito) },
  { path: 'venta', loadComponent: () => import('./venta/venta').then((m) => m.Venta) },
  { path: 'consulta', loadComponent: () => import('./consulta/consulta').then((m) => m.Consulta) },
  { path: '**', redirectTo: 'catalogo' },
];
