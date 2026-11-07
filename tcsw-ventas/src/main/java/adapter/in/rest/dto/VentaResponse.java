package adapter.in.rest.dto;

import domain.Venta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

// lo que la API muestra de una venta. "estado" es CERRADA o ABIERTA; "total" ya incluye el descuento del dominio.
public record VentaResponse(String folio, String estado, List<PartidaResponse> partidas, BigDecimal total) {

    public static VentaResponse from(Venta venta) {
        List<PartidaResponse> partidas = venta.getPartidas().stream()
            .map(PartidaResponse::from)
            .collect(Collectors.toList());
        return new VentaResponse(
            venta.getFolio(),
            venta.isCerrada() ? "CERRADA" : "ABIERTA",
            partidas,
            BigDecimal.valueOf(venta.calcularTotal().valor()).setScale(2, RoundingMode.HALF_UP));
    }
}
