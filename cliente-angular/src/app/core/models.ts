// Espejo a mano de tcsw-ventas/src/main/resources/static/openapi.yaml (P09). Si el contrato cambia, cambia este archivo.

export interface Producto {
  codigo: string;
  nombre: string;
  precio: number;
  existencia: number;
}

export interface ItemVentaRequest {
  codigoProducto: string;
  cantidad: number;
}

export interface VentaRequest {
  folio: string;
  items: ItemVentaRequest[];
}

export interface PartidaResponse {
  codigoProducto: string;
  nombre: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface VentaResponse {
  folio: string;
  estado: 'CERRADA' | 'ABIERTA';
  partidas: PartidaResponse[];
  total: number;
}

export interface ErrorDetalle {
  campo: string;
  mensaje: string;
}

// formato unico de error de la API (ErrorResponse)
export interface ErrorApi {
  timestamp: string;
  status: number;
  error: string;
  codigo: string;
  mensaje: string;
  ruta: string;
  detalles?: ErrorDetalle[];
}
