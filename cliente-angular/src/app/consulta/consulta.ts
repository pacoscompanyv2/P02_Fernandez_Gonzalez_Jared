import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, input, signal } from '@angular/core';
import { ErrorVisible, describirError } from '../core/api-error';
import { VentaResponse } from '../core/models';
import { VentasService } from '../core/ventas.service';

@Component({
  selector: 'app-consulta',
  imports: [CurrencyPipe],
  template: `
    <h2>Consulta de venta</h2>
    <label>
      Folio
      <input type="text" class="folio-entrada" [value]="folioEscrito()" (input)="alEscribir($event)" (keydown.enter)="buscar()" maxlength="30" />
    </label>
    <button type="button" class="buscar" [disabled]="buscando()" (click)="buscar()">Buscar</button>

    @if (error(); as e) {
      <div class="error" role="alert"><strong>{{ e.codigo }}</strong>: {{ e.mensaje }}</div>
    }
    @if (venta(); as v) {
      <div class="ok resultado">
        <p>
          Folio <strong class="folio">{{ v.folio }}</strong> - <strong class="estado">{{ v.estado }}</strong> - Total
          <strong class="total">{{ v.total | currency: 'MXN' : 'symbol-narrow' }}</strong>
        </p>
        <table>
          <thead><tr><th>Codigo</th><th>Producto</th><th>Cantidad</th><th>Precio</th><th>Subtotal</th></tr></thead>
          <tbody>
            @for (p of v.partidas; track p.codigoProducto) {
              <tr>
                <td>{{ p.codigoProducto }}</td>
                <td>{{ p.nombre }}</td>
                <td>{{ p.cantidad }}</td>
                <td>{{ p.precioUnitario | currency: 'MXN' : 'symbol-narrow' }}</td>
                <td>{{ p.subtotal | currency: 'MXN' : 'symbol-narrow' }}</td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    }
  `,
})
export class Consulta implements OnInit {
  private readonly ventasApi = inject(VentasService);

  // el folio puede llegar por la URL (?folio=...) desde la pantalla de venta: app.config usa withComponentInputBinding()
  readonly folio = input<string>('');

  readonly folioEscrito = signal('');
  readonly buscando = signal(false);
  readonly venta = signal<VentaResponse | null>(null);
  readonly error = signal<ErrorVisible | null>(null);

  ngOnInit(): void {
    const inicial = this.folio();
    if (inicial) {
      this.folioEscrito.set(inicial);
      this.buscar();
    }
  }

  alEscribir(evento: Event): void {
    this.folioEscrito.set((evento.target as HTMLInputElement).value);
  }

  buscar(): void {
    const folio = this.folioEscrito().trim();
    this.venta.set(null);
    if (!folio) {
      this.error.set({ status: -1, codigo: 'FOLIO_VACIO', mensaje: 'Escribe el folio de la venta.', detalles: [] });
      return;
    }
    this.buscando.set(true);
    this.error.set(null);
    this.ventasApi.consultar(folio).subscribe({
      next: (venta) => {
        this.venta.set(venta);
        this.buscando.set(false);
      },
      error: (err) => {
        this.error.set(describirError(err));
        this.buscando.set(false);
      },
    });
  }
}
