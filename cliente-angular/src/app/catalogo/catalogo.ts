import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { ErrorVisible, describirError } from '../core/api-error';
import { CarritoService } from '../core/carrito.service';
import { Producto } from '../core/models';
import { ProductosService } from '../core/productos.service';

@Component({
  selector: 'app-catalogo',
  imports: [CurrencyPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h2>Catalogo</h2>
    <label>
      Buscar por codigo o nombre
      <input type="search" class="buscar" [value]="filtro()" (input)="alEscribir($event)" />
    </label>

    @if (cargando()) {
      <p class="aviso cargando">Cargando productos...</p>
    }
    @if (error(); as e) {
      <div class="error" role="alert">
        <strong>{{ e.codigo }}</strong>: {{ e.mensaje }}
        <button type="button" class="reintentar" (click)="cargar()">Reintentar</button>
      </div>
    }
    @if (aviso()) {
      <p class="aviso" role="status">{{ aviso() }}</p>
    }

    @if (!cargando() && !error()) {
      <table>
        <thead>
          <tr><th>Codigo</th><th>Nombre</th><th>Precio</th><th>Existencia</th><th></th></tr>
        </thead>
        <tbody>
          @for (p of filtrados(); track p.codigo) {
            <tr>
              <td>{{ p.codigo }}</td>
              <td>{{ p.nombre }}</td>
              <td>{{ p.precio | currency: 'MXN' : 'symbol-narrow' }}</td>
              <td>{{ p.existencia }}</td>
              <td>
                <button type="button" class="agregar" [disabled]="p.existencia === 0" (click)="agregar(p)">Agregar</button>
              </td>
            </tr>
          } @empty {
            <tr><td colspan="5" class="vacio">No hay productos que coincidan.</td></tr>
          }
        </tbody>
      </table>
    }
  `,
})
export class Catalogo implements OnInit {
  private readonly productosApi = inject(ProductosService);
  private readonly carrito = inject(CarritoService);

  readonly productos = signal<Producto[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<ErrorVisible | null>(null);
  readonly aviso = signal('');
  readonly filtro = signal('');

  readonly filtrados = computed(() => {
    const texto = this.filtro().trim().toLowerCase();
    if (!texto) {
      return this.productos();
    }
    return this.productos().filter((p) => p.codigo.toLowerCase().includes(texto) || p.nombre.toLowerCase().includes(texto));
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.productosApi.listar().subscribe({
      next: (lista) => {
        this.productos.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(describirError(err));
        this.cargando.set(false);
      },
    });
  }

  alEscribir(evento: Event): void {
    this.filtro.set((evento.target as HTMLInputElement).value);
  }

  agregar(producto: Producto): void {
    const agregado = this.carrito.agregar(producto);
    this.aviso.set(agregado ? `Agregado: ${producto.nombre}` : `No hay mas existencia de ${producto.nombre}`);
  }
}
