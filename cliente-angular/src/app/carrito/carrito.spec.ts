import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { CarritoService } from '../core/carrito.service';
import { Producto } from '../core/models';
import { Carrito } from './carrito';

const P1: Producto = { codigo: 'P1', nombre: 'Playera Deportiva', precio: 150, existencia: 3 };
const P2: Producto = { codigo: 'P2', nombre: 'Pantalon Mezclilla', precio: 350, existencia: 15 };

describe('Carrito (pantalla)', () => {
  let servicio: CarritoService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [Carrito], providers: [provideRouter([])] }).compileComponents();
    servicio = TestBed.inject(CarritoService);
  });

  async function abrir() {
    const fixture = TestBed.createComponent(Carrito);
    fixture.detectChanges();
    await fixture.whenStable();
    return fixture;
  }

  async function refrescar(fixture: Awaited<ReturnType<typeof abrir>>) {
    await fixture.whenStable();
    fixture.detectChanges();
  }

  it('con el carrito vacio muestra el mensaje y no la tabla', async () => {
    const el = (await abrir()).nativeElement as HTMLElement;
    expect(el.querySelector('.vacio')).not.toBeNull();
    expect(el.querySelector('table')).toBeNull();
  });

  it('muestra una fila por partida y el total estimado', async () => {
    servicio.agregar(P1, 2);
    servicio.agregar(P2, 1);
    const el = (await abrir()).nativeElement as HTMLElement;
    expect(el.querySelectorAll('tbody tr').length).toBe(2);
    expect(el.querySelector('.total')?.textContent).toContain('650');
  });

  it('el boton + sube la cantidad hasta la existencia y despues avisa', async () => {
    servicio.agregar(P1, 2);
    const fixture = await abrir();
    const el = fixture.nativeElement as HTMLElement;
    (el.querySelector('button.mas') as HTMLButtonElement).click();
    await refrescar(fixture);
    expect(servicio.items()[0].cantidad).toBe(3);
    (el.querySelector('button.mas') as HTMLButtonElement).click();
    await refrescar(fixture);
    expect(servicio.items()[0].cantidad).toBe(3);
    expect(el.querySelector('.aviso')?.textContent).toContain('No hay mas existencia');
  });

  it('el boton - no baja de 1', async () => {
    servicio.agregar(P2, 1);
    const fixture = await abrir();
    const el = fixture.nativeElement as HTMLElement;
    (el.querySelector('button.menos') as HTMLButtonElement).click();
    await refrescar(fixture);
    expect(servicio.items()[0].cantidad).toBe(1);
    expect(el.querySelector('.aviso')?.textContent).toContain('minima');
  });

  it('Quitar saca la partida y Vaciar deja el carrito vacio', async () => {
    servicio.agregar(P1, 1);
    servicio.agregar(P2, 1);
    const fixture = await abrir();
    const el = fixture.nativeElement as HTMLElement;
    (el.querySelector('button.quitar') as HTMLButtonElement).click();
    await refrescar(fixture);
    expect(el.querySelectorAll('tbody tr').length).toBe(1);
    (el.querySelector('button.vaciar') as HTMLButtonElement).click();
    await refrescar(fixture);
    expect(el.querySelector('.vacio')).not.toBeNull();
  });
});
