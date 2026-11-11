import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { CarritoService } from '../core/carrito.service';
import { VentaResponse } from '../core/models';
import { Venta } from './venta';

const P1 = { codigo: 'P1', nombre: 'Playera Deportiva', precio: 150, existencia: 20 };
const VENTA: VentaResponse = {
  folio: 'F-1',
  estado: 'CERRADA',
  partidas: [{ codigoProducto: 'P1', nombre: 'Playera Deportiva', cantidad: 2, precioUnitario: 150, subtotal: 300 }],
  total: 300,
};

describe('Venta (formulario)', () => {
  let http: HttpTestingController;
  let carrito: CarritoService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Venta],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
    carrito = TestBed.inject(CarritoService);
  });

  afterEach(() => http.verify());

  async function abrir() {
    const fixture = TestBed.createComponent(Venta);
    fixture.detectChanges();
    await fixture.whenStable();
    return fixture;
  }

  async function refrescar(fixture: Awaited<ReturnType<typeof abrir>>) {
    await fixture.whenStable();
    fixture.detectChanges();
  }

  function escribirFolio(fixture: Awaited<ReturnType<typeof abrir>>, folio: string) {
    const entrada = (fixture.nativeElement as HTMLElement).querySelector('input[formControlName="folio"]') as HTMLInputElement;
    entrada.value = folio;
    entrada.dispatchEvent(new Event('input'));
  }

  function enviar(fixture: Awaited<ReturnType<typeof abrir>>) {
    ((fixture.nativeElement as HTMLElement).querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
  }

  it('con el carrito vacio no hay formulario', async () => {
    const el = (await abrir()).nativeElement as HTMLElement;
    expect(el.querySelector('form')).toBeNull();
    expect(el.querySelector('.vacio')).not.toBeNull();
  });

  it('el boton esta deshabilitado hasta que hay folio', async () => {
    carrito.agregar(P1, 2);
    const fixture = await abrir();
    const el = fixture.nativeElement as HTMLElement;
    expect((el.querySelector('button.enviar') as HTMLButtonElement).disabled).toBe(true);
    escribirFolio(fixture, 'F-1');
    await refrescar(fixture);
    expect((el.querySelector('button.enviar') as HTMLButtonElement).disabled).toBe(false);
  });

  it('un folio de solo espacios no es valido', async () => {
    carrito.agregar(P1, 1);
    const fixture = await abrir();
    escribirFolio(fixture, '   ');
    await refrescar(fixture);
    expect((fixture.nativeElement as HTMLElement).querySelector('button.enviar')?.hasAttribute('disabled')).toBe(true);
  });

  it('registra la venta, muestra la confirmacion de la API y vacia el carrito', async () => {
    carrito.agregar(P1, 2);
    const fixture = await abrir();
    escribirFolio(fixture, 'F-1');
    await refrescar(fixture);
    enviar(fixture);
    const req = http.expectOne('/api/v1/ventas');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ folio: 'F-1', items: [{ codigoProducto: 'P1', cantidad: 2 }] });
    req.flush(VENTA, { status: 201, statusText: 'Created' });
    await refrescar(fixture);
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.estado')?.textContent).toBe('CERRADA');
    expect(el.querySelector('.folio')?.textContent).toBe('F-1');
    expect(carrito.vacio()).toBe(true);
  });

  it('un folio repetido (409) muestra el codigo y conserva el carrito', async () => {
    carrito.agregar(P1, 2);
    const fixture = await abrir();
    escribirFolio(fixture, 'F-1');
    await refrescar(fixture);
    enviar(fixture);
    http
      .expectOne('/api/v1/ventas')
      .flush({ status: 409, codigo: 'FOLIO_DUPLICADO', mensaje: 'Ya existe una venta con el folio F-1' }, { status: 409, statusText: 'Conflict' });
    await refrescar(fixture);
    const alerta = (fixture.nativeElement as HTMLElement).querySelector('.error') as HTMLElement;
    expect(alerta.textContent).toContain('FOLIO_DUPLICADO');
    expect(carrito.items().length).toBe(1);
  });

  it('un 400 de validacion lista los detalles por campo', async () => {
    carrito.agregar(P1, 2);
    const fixture = await abrir();
    escribirFolio(fixture, 'F-2');
    await refrescar(fixture);
    enviar(fixture);
    http.expectOne('/api/v1/ventas').flush(
      { status: 400, codigo: 'VALIDACION', mensaje: 'La solicitud no es valida', detalles: [{ campo: 'items[0].cantidad', mensaje: 'La cantidad debe ser mayor a cero' }] },
      { status: 400, statusText: 'Bad Request' },
    );
    await refrescar(fixture);
    expect((fixture.nativeElement as HTMLElement).querySelector('.error')?.textContent).toContain('items[0].cantidad');
  });

  it('existencia insuficiente (409) se muestra tal cual la manda la API', async () => {
    carrito.agregar(P1, 2);
    const fixture = await abrir();
    escribirFolio(fixture, 'F-3');
    await refrescar(fixture);
    enviar(fixture);
    http
      .expectOne('/api/v1/ventas')
      .flush({ status: 409, codigo: 'EXISTENCIA_INSUFICIENTE', mensaje: 'Existencia insuficiente para P1: disponible 1' }, { status: 409, statusText: 'Conflict' });
    await refrescar(fixture);
    expect((fixture.nativeElement as HTMLElement).querySelector('.error')?.textContent).toContain('Existencia insuficiente');
  });

  it('sin conexion con la API (status 0) muestra un mensaje claro', async () => {
    carrito.agregar(P1, 1);
    const fixture = await abrir();
    escribirFolio(fixture, 'F-4');
    await refrescar(fixture);
    enviar(fixture);
    http.expectOne('/api/v1/ventas').error(new ProgressEvent('error'));
    await refrescar(fixture);
    expect((fixture.nativeElement as HTMLElement).querySelector('.error')?.textContent).toContain('No se pudo conectar');
  });
});
