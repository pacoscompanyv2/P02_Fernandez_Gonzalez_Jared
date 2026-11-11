import { Injectable, computed, signal } from '@angular/core';
import { Producto, VentaRequest } from './models';

export interface ItemCarrito {
  producto: Producto;
  cantidad: number;
}

// limite de cantidad por partida que acepta la API (ItemVentaRequest)
export const MAX_CANTIDAD = 10000;

// Carrito de compras: el estado vive en signals; ninguna regla de negocio (la API decide existencias, descuento y folios).
// Aqui solo se evita pedir mas de la existencia que el catalogo mostro.
@Injectable({ providedIn: 'root' })
export class CarritoService {
  private readonly _items = signal<ItemCarrito[]>([]);
  readonly items = this._items.asReadonly();
  readonly vacio = computed(() => this._items().length === 0);
  readonly cantidadTotal = computed(() => this._items().reduce((suma, i) => suma + i.cantidad, 0));
  // total ESTIMADO (sin descuentos): el total final lo calcula la API
  readonly total = computed(() => Math.round(this._items().reduce((suma, i) => suma + i.producto.precio * i.cantidad, 0) * 100) / 100);

  /** Agrega unidades. Regresa false si la cantidad no es valida o si se pasaria de la existencia. */
  agregar(producto: Producto, cantidad = 1): boolean {
    if (!Number.isInteger(cantidad) || cantidad < 1) {
      return false;
    }
    const actual = this._items().find((i) => i.producto.codigo === producto.codigo);
    const nueva = (actual?.cantidad ?? 0) + cantidad;
    if (nueva > producto.existencia || nueva > MAX_CANTIDAD) {
      return false;
    }
    if (actual) {
      this._items.update((lista) => lista.map((i) => (i.producto.codigo === producto.codigo ? { ...i, cantidad: nueva } : i)));
    } else {
      this._items.update((lista) => [...lista, { producto, cantidad }]);
    }
    return true;
  }

  /** Fija la cantidad de una partida. Regresa false si no existe, no es entera >= 1 o pasa de la existencia. */
  cambiarCantidad(codigo: string, cantidad: number): boolean {
    const actual = this._items().find((i) => i.producto.codigo === codigo);
    if (!actual || !Number.isInteger(cantidad) || cantidad < 1 || cantidad > actual.producto.existencia || cantidad > MAX_CANTIDAD) {
      return false;
    }
    this._items.update((lista) => lista.map((i) => (i.producto.codigo === codigo ? { ...i, cantidad } : i)));
    return true;
  }

  quitar(codigo: string): void {
    this._items.update((lista) => lista.filter((i) => i.producto.codigo !== codigo));
  }

  vaciar(): void {
    this._items.set([]);
  }

  /** Arma el cuerpo de POST /api/v1/ventas con el folio dado. */
  aRequest(folio: string): VentaRequest {
    return {
      folio: folio.trim(),
      items: this._items().map((i) => ({ codigoProducto: i.producto.codigo, cantidad: i.cantidad })),
    };
  }
}
