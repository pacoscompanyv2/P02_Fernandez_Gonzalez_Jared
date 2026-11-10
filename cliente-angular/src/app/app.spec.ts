import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { CarritoService } from './core/carrito.service';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [App], providers: [provideRouter([])] }).compileComponents();
  });

  it('muestra el menu con cuatro enlaces y el carrito vacio', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelectorAll('nav a').length).toBe(4);
    expect(el.textContent).toContain('Carrito (0)');
  });

  it('el menu refleja cuantas unidades hay en el carrito', async () => {
    TestBed.inject(CarritoService).agregar({ codigo: 'P1', nombre: 'Playera', precio: 150, existencia: 20 }, 3);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Carrito (3)');
  });
});
