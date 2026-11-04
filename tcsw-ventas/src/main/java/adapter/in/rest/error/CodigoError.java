package adapter.in.rest.error;

// catalogo cerrado de codigos de error de la API: el cliente decide por "codigo", no por el texto del mensaje.
// Debe coincidir con el enum del esquema Error en openapi.yaml (lo verifica ContratoApiTest).
public enum CodigoError {
    VALIDACION,
    JSON_INVALIDO,
    SOLICITUD_INVALIDA,
    PRODUCTO_NO_ENCONTRADO,
    VENTA_NO_ENCONTRADA,
    EXISTENCIA_INSUFICIENTE,
    FOLIO_DUPLICADO,
    CONFLICTO,
    RECURSO_NO_ENCONTRADO,
    METODO_NO_PERMITIDO,
    MEDIA_TYPE_NO_SOPORTADO,
    ERROR_INTERNO
}
