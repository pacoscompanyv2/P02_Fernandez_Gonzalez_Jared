import { HttpErrorResponse } from '@angular/common/http';
import { describirError } from './api-error';

function http(status: number, error: unknown): HttpErrorResponse {
  return new HttpErrorResponse({ status, error });
}

describe('describirError', () => {
  it('usa el mensaje y el codigo del formato de error de la API', () => {
    const e = describirError(http(404, { status: 404, codigo: 'PRODUCTO_NO_ENCONTRADO', mensaje: 'No existe el producto NOPE' }));
    expect(e.status).toBe(404);
    expect(e.codigo).toBe('PRODUCTO_NO_ENCONTRADO');
    expect(e.mensaje).toBe('No existe el producto NOPE');
    expect(e.detalles.length).toBe(0);
  });

  it('conserva los detalles por campo de un 400 de validacion', () => {
    const e = describirError(
      http(400, { status: 400, codigo: 'VALIDACION', mensaje: 'La solicitud no es valida', detalles: [{ campo: 'folio', mensaje: 'El folio es obligatorio' }] }),
    );
    expect(e.codigo).toBe('VALIDACION');
    expect(e.detalles.length).toBe(1);
    expect(e.detalles[0].campo).toBe('folio');
  });

  it('un 409 por existencia conserva su codigo', () => {
    const e = describirError(http(409, { status: 409, codigo: 'EXISTENCIA_INSUFICIENTE', mensaje: 'Existencia insuficiente para P1' }));
    expect(e.codigo).toBe('EXISTENCIA_INSUFICIENTE');
  });

  it('sin conexion (status 0) da un mensaje claro', () => {
    const e = describirError(http(0, null));
    expect(e.codigo).toBe('SIN_CONEXION');
    expect(e.mensaje).toContain('No se pudo conectar');
  });

  it('un 504 del proxy cuando la API esta apagada tambien es sin conexion', () => {
    expect(describirError(http(504, 'Gateway Timeout')).codigo).toBe('SIN_CONEXION');
  });

  it('una respuesta que no tiene el formato de la API no rompe', () => {
    const e = describirError(http(500, '<html>boom</html>'));
    expect(e.codigo).toBe('ERROR_HTTP');
    expect(e.mensaje).toContain('500');
  });

  it('un error que no es HTTP se describe como desconocido', () => {
    expect(describirError(new Error('x')).codigo).toBe('ERROR_DESCONOCIDO');
  });
});
