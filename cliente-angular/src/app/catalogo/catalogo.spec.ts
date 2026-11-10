import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CarritoService } from '../core/carrito.service';
import { Producto } from '../core/models';
import { Catalogo } from './catalogo';

const PRODUCTOS: Producto[] = [
  { codigo: 'P1', nombre: 'Playera Deportiva', precio: 150, existencia: 20 },
  { codigo: 'P2', nombre: 'Pantalon Mezclilla', precio: 350, existencia: 15 },
  { codigo: 'P3', nombre: 'Gorra Urbana', precio: 99, existencia: 0 },
];

describe('Catalogo', () => {
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Catalogo],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  async function abrir() {
    const fixture = TestBed.createComponent(Catalogo);
    fixture.detectChanges();
    http.expectOne('/api/v1/productos').flush(PRODUCTOS);
    await fixture.whenStable();
    fixture.detectChanges();
    return fixture;
  }

  it('muestra los productos que devuelve la API', async () => {
    const fixture = await abrir();
    const filas = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(filas.length).toBe(3);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Playera Deportiva');
  });

  it('filtra por texto', async () => {
    const fixture = await abrir();
    const el = fixture.nativeElement as HTMLElement;
    const entrada = el.querySelector('input.buscar') as HTMLInputElement;
    entrada.value = 'gorra';
    entrada.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    fixture.detectChanges();
    expect(el.querySelectorAll('tbody tr').length).toBe(1);
    expect(el.textContent).toContain('Gorra Urbana');
  });

  it('un producto sin existencia no se puede agregar', async () => {
    const fixture = await abrir();
    const botones = (fixture.nativeElement as HTMLElement).querySelectorAll('button.agregar');
    expect((botones[2] as HTMLButtonElement).disabled).toBe(true);
    expect((botones[0] as HTMLButtonElement).disabled).toBe(false);
  });

  it('agregar manda el producto al carrito', async () => {
    const fixture = await abrir();
    const boton = (fixture.nativeElement as HTMLElement).querySelector('button.agregar') as HTMLButtonElement;
    boton.click();
    await fixture.whenStable();
    expect(TestBed.inject(CarritoService).cantidadTotal()).toBe(1);
  });

  it('si la API responde error muestra el mensaje y permite reintentar', async () => {
    const fixture = TestBed.createComponent(Catalogo);
    fixture.detectChanges();
    http
      .expectOne('/api/v1/productos')
      .flush({ status: 500, codigo: 'ERROR_INTERNO', mensaje: 'Error interno del servidor' }, { status: 500, statusText: 'Server Error' });
    await fixture.whenStable();
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.error')?.textContent).toContain('ERROR_INTERNO');
    (el.querySelector('button.reintentar') as HTMLButtonElement).click();
    http.expectOne('/api/v1/productos').flush(PRODUCTOS);
    await fixture.whenStable();
    fixture.detectChanges();
    expect(el.querySelector('.error')).toBeNull();
    expect(el.querySelectorAll('tbody tr').length).toBe(3);
  });
});
