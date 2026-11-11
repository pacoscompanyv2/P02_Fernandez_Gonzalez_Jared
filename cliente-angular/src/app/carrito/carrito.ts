import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CarritoService, ItemCarrito } from '../core/carrito.service';

@Component({
  selector: 'app-carrito',
  imports: [CurrencyPipe, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h2>Carrito</h2>
    @if (carrito.vacio()) {
      <p class="vacio">El carrito esta vacio. <a routerLink="/catalogo">Ir al catalogo</a></p>
    } @else {
      @if (aviso()) {
        <p class="aviso" role="status">{{ aviso() }}</p>
      }
      <table>
        <thead>
          <tr><th>Codigo</th><th>Producto</th><th>Precio</th><th>Cantidad</th><th>Subtotal</th><th></th></tr>
        </thead>
        <tbody>
          @for (i of carrito.items(); track i.producto.codigo) {
            <tr>
              <td>{{ i.producto.codigo }}</td>
              <td>{{ i.producto.nombre }}</td>
              <td>{{ i.producto.precio | currency: 'MXN' : 'symbol-narrow' }}</td>
              <td>
                <button type="button" class="menos" aria-label="Quitar una unidad" (click)="cambiar(i, -1)">-</button>
                <span class="cantidad">{{ i.cantidad }}</span>
                <button type="button" class="mas" aria-label="Agregar una unidad" (click)="cambiar(i, 1)">+</button>
              </td>
              <td>{{ i.producto.precio * i.cantidad | currency: 'MXN' : 'symbol-narrow' }}</td>
              <td><button type="button" class="quitar" (click)="carrito.quitar(i.producto.codigo)">Quitar</button></td>
            </tr>
          }
        </tbody>
      </table>
      <p class="total">
        Total estimado: <strong>{{ carrito.total() | currency: 'MXN' : 'symbol-narrow' }}</strong>
        <small>(el total final lo calcula la API y puede incluir descuentos)</small>
      </p>
      <p>
        <button type="button" class="vaciar" (click)="carrito.vaciar()">Vaciar carrito</button>
        <a routerLink="/venta">Continuar a la venta</a>
      </p>
    }
  `,
})
export class Carrito {
  protected readonly carrito = inject(CarritoService);
  readonly aviso = signal('');

  cambiar(item: ItemCarrito, delta: number): void {
    const nueva = item.cantidad + delta;
    if (nueva < 1) {
      this.aviso.set('La cantidad minima es 1. Usa Quitar para sacar el producto.');
      return;
    }
    const cambiado = this.carrito.cambiarCantidad(item.producto.codigo, nueva);
    this.aviso.set(cambiado ? '' : `No hay mas existencia de ${item.producto.nombre} (maximo ${item.producto.existencia}).`);
  }
}
