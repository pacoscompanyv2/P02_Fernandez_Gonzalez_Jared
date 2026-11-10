import { TestBed } from '@angular/core/testing';
import { CarritoService } from './carrito.service';
import { Producto } from './models';

const P1: Producto = { codigo: 'P1', nombre: 'Playera Deportiva', precio: 150, existencia: 20 };
const P2: Producto = { codigo: 'P2', nombre: 'Pantalon Mezclilla', precio: 350, existencia: 15 };

describe('CarritoService (base)', () => {
  let carrito: CarritoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    carrito = TestBed.inject(CarritoService);
  });

  it('empieza vacio', () => {
    expect(carrito.items().length).toBe(0);
    expect(carrito.cantidadTotal()).toBe(0);
  });

  it('agregar el mismo producto suma la cantidad en una sola partida', () => {
    carrito.agregar(P1);
    carrito.agregar(P1, 2);
    expect(carrito.items().length).toBe(1);
    expect(carrito.items()[0].cantidad).toBe(3);
    expect(carrito.cantidadTotal()).toBe(3);
  });

  it('productos distintos son partidas distintas', () => {
    carrito.agregar(P1);
    carrito.agregar(P2);
    expect(carrito.items().length).toBe(2);
  });

  it('rechaza cantidades que no son enteros positivos', () => {
    expect(carrito.agregar(P1, 0)).toBe(false);
    expect(carrito.agregar(P1, -1)).toBe(false);
    expect(carrito.agregar(P1, 1.5)).toBe(false);
    expect(carrito.items().length).toBe(0);
  });
});
