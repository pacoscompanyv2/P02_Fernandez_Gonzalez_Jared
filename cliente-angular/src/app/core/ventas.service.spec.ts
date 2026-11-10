import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { VentaRequest, VentaResponse } from './models';
import { VentasService } from './ventas.service';

const SOLICITUD: VentaRequest = { folio: 'F-1', items: [{ codigoProducto: 'P1', cantidad: 2 }] };
const VENTA: VentaResponse = {
  folio: 'F-1',
  estado: 'CERRADA',
  partidas: [{ codigoProducto: 'P1', nombre: 'Playera Deportiva', cantidad: 2, precioUnitario: 150, subtotal: 300 }],
  total: 300,
};

describe('VentasService', () => {
  let servicio: VentasService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servicio = TestBed.inject(VentasService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('registrar hace POST /api/v1/ventas con el cuerpo de la venta', () => {
    let recibida: VentaResponse | undefined;
    servicio.registrar(SOLICITUD).subscribe((v) => (recibida = v));
    const req = http.expectOne('/api/v1/ventas');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(SOLICITUD);
    req.flush(VENTA, { status: 201, statusText: 'Created' });
    expect(recibida?.estado).toBe('CERRADA');
    expect(recibida?.total).toBe(300);
  });

  it('consultar hace GET /api/v1/ventas/{folio}', () => {
    let recibida: VentaResponse | undefined;
    servicio.consultar('F-1').subscribe((v) => (recibida = v));
    http.expectOne('/api/v1/ventas/F-1').flush(VENTA);
    expect(recibida?.folio).toBe('F-1');
  });

  it('un 409 por folio repetido llega con su codigo', () => {
    let error: HttpErrorResponse | undefined;
    servicio.registrar(SOLICITUD).subscribe({ error: (e) => (error = e) });
    http
      .expectOne('/api/v1/ventas')
      .flush({ status: 409, codigo: 'FOLIO_DUPLICADO', mensaje: 'Ya existe una venta con el folio F-1' }, { status: 409, statusText: 'Conflict' });
    expect(error?.status).toBe(409);
    expect(error?.error.codigo).toBe('FOLIO_DUPLICADO');
  });
});
