import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { VentaResponse } from '../core/models';
import { Consulta } from './consulta';

const VENTA: VentaResponse = {
  folio: 'F-1',
  estado: 'CERRADA',
  partidas: [{ codigoProducto: 'P1', nombre: 'Playera Deportiva', cantidad: 2, precioUnitario: 150, subtotal: 300 }],
  total: 300,
};

describe('Consulta', () => {
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Consulta],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  async function abrir(folioInicial?: string) {
    const fixture = TestBed.createComponent(Consulta);
    if (folioInicial) {
      fixture.componentRef.setInput('folio', folioInicial);
    }
    fixture.detectChanges();
    await fixture.whenStable();
    return fixture;
  }

  function buscar(fixture: Awaited<ReturnType<typeof abrir>>, folio: string) {
    const el = fixture.nativeElement as HTMLElement;
    const entrada = el.querySelector('input.folio-entrada') as HTMLInputElement;
    entrada.value = folio;
    entrada.dispatchEvent(new Event('input'));
    (el.querySelector('button.buscar') as HTMLButtonElement).click();
  }

  async function refrescar(fixture: Awaited<ReturnType<typeof abrir>>) {
    await fixture.whenStable();
    fixture.detectChanges();
  }

  it('busca la venta por folio y muestra su estado y partidas', async () => {
    const fixture = await abrir();
    buscar(fixture, 'F-1');
    const req = http.expectOne('/api/v1/ventas/F-1');
    expect(req.request.method).toBe('GET');
    req.flush(VENTA);
    await refrescar(fixture);
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.estado')?.textContent).toBe('CERRADA');
    expect(el.querySelectorAll('tbody tr').length).toBe(1);
  });

  it('un folio que no existe (404) muestra VENTA_NO_ENCONTRADA', async () => {
    const fixture = await abrir();
    buscar(fixture, 'NOPE');
    http
      .expectOne('/api/v1/ventas/NOPE')
      .flush({ status: 404, codigo: 'VENTA_NO_ENCONTRADA', mensaje: 'No existe la venta NOPE' }, { status: 404, statusText: 'Not Found' });
    await refrescar(fixture);
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.error')?.textContent).toContain('VENTA_NO_ENCONTRADA');
    expect(el.querySelector('.resultado')).toBeNull();
  });

  it('un folio vacio no llama a la API', async () => {
    const fixture = await abrir();
    buscar(fixture, '   ');
    await refrescar(fixture);
    http.expectNone((req) => req.url.startsWith('/api/v1/ventas'));
    expect((fixture.nativeElement as HTMLElement).querySelector('.error')?.textContent).toContain('Escribe el folio');
  });

  it('si llega un folio por la URL lo consulta al abrir', async () => {
    const fixture = await abrir('F-1');
    http.expectOne('/api/v1/ventas/F-1').flush(VENTA);
    await refrescar(fixture);
    expect((fixture.nativeElement as HTMLElement).querySelector('.folio')?.textContent).toBe('F-1');
  });
});
