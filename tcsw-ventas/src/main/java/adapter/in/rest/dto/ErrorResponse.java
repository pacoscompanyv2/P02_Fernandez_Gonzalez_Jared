package adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

// forma UNICA de todo error de la API (400, 404, 405, 409, 415, 500...). Ver openapi.yaml -> components/schemas/Error.
//   codigo   : texto estable para programas (VALIDACION, PRODUCTO_NO_ENCONTRADO...)
//   mensaje  : texto para personas
//   detalles : solo en errores de validacion, uno por campo
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String timestamp, int status, String error, String codigo, String mensaje,
                            String ruta, List<Detalle> detalles) {

    public record Detalle(String campo, String mensaje) {
    }
}
