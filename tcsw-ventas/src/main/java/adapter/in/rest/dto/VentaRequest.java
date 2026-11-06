package adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// cuerpo de POST /api/v1/ventas. La validacion de FORMA vive aqui (Bean Validation); las reglas de NEGOCIO
// (existe el producto, alcanza la existencia) las decide el caso de uso.
public record VentaRequest(
    @NotBlank(message = "El folio es obligatorio")
    @Size(max = 30, message = "El folio no puede pasar de 30 caracteres")
    String folio,

    @NotEmpty(message = "La venta necesita al menos una partida")
    @Size(max = 100, message = "Una venta no puede tener mas de 100 partidas")
    List<@Valid @NotNull(message = "La partida no puede ser nula") ItemVentaRequest> items) {
}
