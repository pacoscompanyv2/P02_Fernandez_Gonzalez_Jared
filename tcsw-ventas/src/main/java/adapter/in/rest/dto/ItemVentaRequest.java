package adapter.in.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// una partida del pedido: que producto y cuantos. Los limites reflejan db/schema.sql (codigo VARCHAR(20))
public record ItemVentaRequest(
    @NotBlank(message = "El codigo del producto es obligatorio")
    @Size(max = 20, message = "El codigo del producto no puede pasar de 20 caracteres")
    String codigoProducto,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    @Max(value = 10000, message = "La cantidad no puede pasar de 10000")
    Integer cantidad) {
}
