import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Producto } from './models';
import { ProductosService } from './productos.service';

const P1: Producto = { codigo: 'P1', nombre: 'Playera Deportiva', precio: 150, existencia: 20 };

describe('ProductosService', () => {
  let servicio: ProductosService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servicio = TestBed.inject(ProductosService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('listar hace GET /api/v1/productos y devuelve la lista', () => {
    let recibido: Producto[] = [];
    servicio.listar().subscribe((lista) => (recibido = lista));
    const req = http.expectOne('/api/v1/productos');
    expect(req.request.method).toBe('GET');
    req.flush([P1]);
    expect(recibido.length).toBe(1);
    expect(recibido[0].codigo).toBe('P1');
  });

  it('obtener hace GET /api/v1/productos/{codigo}', () => {
    let recibido: Producto | undefined;
    servicio.obtener('P1').subscribe((p) => (recibido = p));
    http.expectOne('/api/v1/productos/P1').flush(P1);
    expect(recibido?.nombre).toBe('Playera Deportiva');
  });

  it('obtener codifica el codigo en la URL', () => {
    servicio.obtener('A/B').subscribe();
    http.expectOne('/api/v1/productos/A%2FB').flush(P1);
  });

  it('un 404 llega como HttpErrorResponse con el formato de error de la API', () => {
    let error: HttpErrorResponse | undefined;
    servicio.obtener('NOPE').subscribe({ error: (e) => (error = e) });
    http
      .expectOne('/api/v1/productos/NOPE')
      .flush({ status: 404, codigo: 'PRODUCTO_NO_ENCONTRADO', mensaje: 'No existe' }, { status: 404, statusText: 'Not Found' });
    expect(error?.status).toBe(404);
    expect(error?.error.codigo).toBe('PRODUCTO_NO_ENCONTRADO');
  });
});
