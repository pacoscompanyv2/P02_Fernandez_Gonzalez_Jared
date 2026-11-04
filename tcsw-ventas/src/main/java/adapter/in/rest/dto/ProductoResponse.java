package adapter.in.rest.dto;

import domain.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;

// lo que la API muestra de un producto. El dominio NUNCA se serializa directo: el contrato (openapi.yaml) lo controla este DTO.
public record ProductoResponse(String codigo, String nombre, BigDecimal precio, int existencia) {

    public static ProductoResponse from(Producto producto) {
        return new ProductoResponse(
            producto.getCodigo(),
            producto.getNombre(),
            BigDecimal.valueOf(producto.getPrecio()).setScale(2, RoundingMode.HALF_UP),
            producto.getExistencia());
    }
}
