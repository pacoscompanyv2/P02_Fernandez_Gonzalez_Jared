import { Injectable, computed, signal } from '@angular/core';
import { Producto } from './models';

export interface ItemCarrito {
  producto: Producto;
  cantidad: number;
}

// Version minima de la base (integrante 1): lo que el catalogo y el menu necesitan.
// El integrante 2 la completa: limite por existencia, quitar, cambiar cantidad, vaciar, total y armado de la solicitud.
@Injectable({ providedIn: 'root' })
export class CarritoService {
  private readonly _items = signal<ItemCarrito[]>([]);
  readonly items = this._items.asReadonly();
  readonly cantidadTotal = computed(() => this._items().reduce((suma, i) => suma + i.cantidad, 0));

  /** Agrega unidades del producto. Regresa false si no se pudo agregar. */
  agregar(producto: Producto, cantidad = 1): boolean {
    if (!Number.isInteger(cantidad) || cantidad < 1) {
      return false;
    }
    const actual = this._items().find((i) => i.producto.codigo === producto.codigo);
    if (actual) {
      this._items.update((lista) =>
        lista.map((i) => (i.producto.codigo === producto.codigo ? { ...i, cantidad: i.cantidad + cantidad } : i)),
      );
    } else {
      this._items.update((lista) => [...lista, { producto, cantidad }]);
    }
    return true;
  }
}
