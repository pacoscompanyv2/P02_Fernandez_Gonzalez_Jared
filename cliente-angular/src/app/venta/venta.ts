import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ErrorVisible, describirError } from '../core/api-error';
import { CarritoService } from '../core/carrito.service';
import { VentaResponse } from '../core/models';
import { VentasService } from '../core/ventas.service';

@Component({
  selector: 'app-venta',
  imports: [ReactiveFormsModule, CurrencyPipe, RouterLink],
  template: `
    <h2>Registrar venta</h2>

    @if (confirmacion(); as v) {
      <div class="ok" role="status">
        <p>
          Venta <strong class="folio">{{ v.folio }}</strong> registrada: <strong class="estado">{{ v.estado }}</strong>.
          Total: <strong class="total">{{ v.total | currency: 'MXN' : 'symbol-narrow' }}</strong>
        </p>
        <ul>
          @for (p of v.partidas; track p.codigoProducto) {
            <li>{{ p.cantidad }} x {{ p.nombre }} ({{ p.codigoProducto }}): {{ p.subtotal | currency: 'MXN' : 'symbol-narrow' }}</li>
          }
        </ul>
        <a [routerLink]="['/consulta']" [queryParams]="{ folio: v.folio }">Consultar esta venta</a> |
        <a routerLink="/catalogo">Nueva venta</a>
      </div>
    }

    @if (carrito.vacio() && !confirmacion()) {
      <p class="vacio">No hay nada que vender. <a routerLink="/catalogo">Ir al catalogo</a></p>
    }

    @if (!carrito.vacio()) {
      <p>Se venderan {{ carrito.cantidadTotal() }} unidades en {{ carrito.items().length }} partidas (total estimado {{ carrito.total() | currency: 'MXN' : 'symbol-narrow' }}).</p>
      <form [formGroup]="form" (ngSubmit)="enviar()" novalidate>
        <label>
          Folio
          <input type="text" formControlName="folio" maxlength="30" autocomplete="off" />
        </label>
        @if (form.controls.folio.touched && form.controls.folio.invalid) {
          <p class="error campo" role="alert">El folio es obligatorio (maximo 30 caracteres).</p>
        }
        <button type="submit" class="enviar" [disabled]="!puedeEnviar()">Registrar venta</button>
      </form>
    }

    @if (error(); as e) {
      <div class="error" role="alert">
        <strong>{{ e.codigo }}</strong>: {{ e.mensaje }}
        @if (e.detalles.length > 0) {
          <ul>
            @for (d of e.detalles; track d.campo + d.mensaje) {
              <li>{{ d.campo }}: {{ d.mensaje }}</li>
            }
          </ul>
        }
      </div>
    }
  `,
})
export class Venta {
  protected readonly carrito = inject(CarritoService);
  private readonly ventasApi = inject(VentasService);
  private readonly fb = inject(FormBuilder);

  readonly form = this.fb.nonNullable.group({
    folio: ['', [Validators.required, Validators.maxLength(30), Validators.pattern(/\S/)]],
  });
  readonly enviando = signal(false);
  readonly error = signal<ErrorVisible | null>(null);
  readonly confirmacion = signal<VentaResponse | null>(null);

  puedeEnviar(): boolean {
    return this.form.valid && !this.carrito.vacio() && !this.enviando();
  }

  enviar(): void {
    if (!this.puedeEnviar()) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set(null);
    this.confirmacion.set(null);
    this.ventasApi.registrar(this.carrito.aRequest(this.form.controls.folio.value)).subscribe({
      next: (venta) => {
        this.confirmacion.set(venta);
        this.carrito.vaciar();
        this.form.reset();
        this.enviando.set(false);
      },
      error: (err) => {
        // el carrito se conserva para corregir el folio o la cantidad y reintentar
        this.error.set(describirError(err));
        this.enviando.set(false);
      },
    });
  }
}
