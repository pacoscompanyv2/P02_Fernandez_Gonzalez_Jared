import { TestBed } from '@angular/core/testing';
import { CarritoService, MAX_CANTIDAD } from './carrito.service';
import { Producto } from './models';

const P1: Producto = { codigo: 'P1', nombre: 'Playera Deportiva', precio: 150, existencia: 20 };
const P2: Producto = { codigo: 'P2', nombre: 'Pantalon Mezclilla', precio: 350, existencia: 15 };
const P3: Producto = { codigo: 'P3', nombre: 'Gorra Urbana', precio: 99.99, existencia: 0 };

describe('CarritoService', () => {
  let carrito: CarritoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    carrito = TestBed.inject(CarritoService);
  });

  it('empieza vacio', () => {
    expect(carrito.vacio()).toBe(true);
    expect(carrito.cantidadTotal()).toBe(0);
    expect(carrito.total()).toBe(0);
  });

  it('agregar el mismo producto suma en una sola partida', () => {
    carrito.agregar(P1);
    carrito.agregar(P1, 2);
    expect(carrito.items().length).toBe(1);
    expect(carrito.items()[0].cantidad).toBe(3);
  });

  it('no deja agregar mas de la existencia', () => {
    expect(carrito.agregar(P1, 20)).toBe(true);
    expect(carrito.agregar(P1, 1)).toBe(false);
    expect(carrito.items()[0].cantidad).toBe(20);
  });

  it('un producto sin existencia no se agrega', () => {
    expect(carrito.agregar(P3)).toBe(false);
    expect(carrito.vacio()).toBe(true);
  });

  it('rechaza cantidades que no son enteros positivos', () => {
    expect(carrito.agregar(P1, 0)).toBe(false);
    expect(carrito.agregar(P1, -2)).toBe(false);
    expect(carrito.agregar(P1, 1.5)).toBe(false);
    expect(carrito.vacio()).toBe(true);
  });

  it('el total estimado suma precio por cantidad', () => {
    carrito.agregar(P1, 2);
    carrito.agregar(P2, 1);
    expect(carrito.total()).toBe(650);
    expect(carrito.cantidadTotal()).toBe(3);
  });

  it('el total no arrastra error de decimales', () => {
    carrito.agregar({ ...P3, existencia: 10 }, 3);
    expect(carrito.total()).toBe(299.97);
  });

  it('cambiarCantidad fija la cantidad y respeta la existencia', () => {
    carrito.agregar(P2, 1);
    expect(carrito.cambiarCantidad('P2', 5)).toBe(true);
    expect(carrito.items()[0].cantidad).toBe(5);
    expect(carrito.cambiarCantidad('P2', 16)).toBe(false);
    expect(carrito.cambiarCantidad('P2', 0)).toBe(false);
    expect(carrito.cambiarCantidad('NOPE', 1)).toBe(false);
    expect(carrito.items()[0].cantidad).toBe(5);
  });

  it('quitar saca la partida y vaciar deja todo en cero', () => {
    carrito.agregar(P1);
    carrito.agregar(P2);
    carrito.quitar('P1');
    expect(carrito.items().length).toBe(1);
    expect(carrito.items()[0].producto.codigo).toBe('P2');
    carrito.vaciar();
    expect(carrito.vacio()).toBe(true);
  });

  it('aRequest arma el cuerpo que espera la API', () => {
    carrito.agregar(P1, 2);
    carrito.agregar(P2, 1);
    expect(carrito.aRequest('  F-9  ')).toEqual({
      folio: 'F-9',
      items: [
        { codigoProducto: 'P1', cantidad: 2 },
        { codigoProducto: 'P2', cantidad: 1 },
      ],
    });
  });

  it('el limite de la API (10000) tambien se respeta', () => {
    const mucho: Producto = { codigo: 'M', nombre: 'Mucho', precio: 1, existencia: MAX_CANTIDAD + 5 };
    expect(carrito.agregar(mucho, MAX_CANTIDAD)).toBe(true);
    expect(carrito.agregar(mucho, 1)).toBe(false);
  });
});
