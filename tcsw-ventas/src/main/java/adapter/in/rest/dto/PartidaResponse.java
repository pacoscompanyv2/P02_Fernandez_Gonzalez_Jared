package adapter.in.rest.dto;

import domain.DetalleVenta;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PartidaResponse(String codigoProducto, String nombre, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {

    public static PartidaResponse from(DetalleVenta partida) {
        return new PartidaResponse(
            partida.getProducto().getCodigo(),
            partida.getProducto().getNombre(),
            partida.getCantidad(),
            BigDecimal.valueOf(partida.getProducto().getPrecio()).setScale(2, RoundingMode.HALF_UP),
            BigDecimal.valueOf(partida.subtotal().valor()).setScale(2, RoundingMode.HALF_UP));
    }
}
