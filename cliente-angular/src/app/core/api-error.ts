import { HttpErrorResponse } from '@angular/common/http';
import { ErrorApi, ErrorDetalle } from './models';

// lo que las pantallas muestran de un error: siempre la misma forma, venga de la API o de la red
export interface ErrorVisible {
  status: number;
  codigo: string;
  mensaje: string;
  detalles: ErrorDetalle[];
}

const SIN_CONEXION = 'No se pudo conectar con la API. Revisa que este en marcha en el puerto 8080.';

export function describirError(err: unknown): ErrorVisible {
  if (err instanceof HttpErrorResponse) {
    // 0 = sin red; 502/503/504 = el proxy de Angular no alcanzo la API
    if (err.status === 0 || err.status === 502 || err.status === 503 || err.status === 504) {
      return { status: err.status, codigo: 'SIN_CONEXION', mensaje: SIN_CONEXION, detalles: [] };
    }
    const cuerpo = err.error as Partial<ErrorApi> | null;
    if (cuerpo && typeof cuerpo === 'object' && typeof cuerpo.mensaje === 'string') {
      return {
        status: err.status,
        codigo: typeof cuerpo.codigo === 'string' ? cuerpo.codigo : 'ERROR',
        mensaje: cuerpo.mensaje,
        detalles: Array.isArray(cuerpo.detalles) ? cuerpo.detalles : [],
      };
    }
    return { status: err.status, codigo: 'ERROR_HTTP', mensaje: `La API respondio ${err.status}.`, detalles: [] };
  }
  return { status: -1, codigo: 'ERROR_DESCONOCIDO', mensaje: 'Ocurrio un error inesperado.', detalles: [] };
}
