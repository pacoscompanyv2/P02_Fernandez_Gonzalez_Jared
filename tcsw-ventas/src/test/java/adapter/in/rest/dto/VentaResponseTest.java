package adapter.in.rest.dto;

import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// el mapeo dominio -> DTO con objetos reales (sin Mockito): estado, partidas, subtotales y descuento por mayoreo
class VentaResponseTest {

    @Test
    void unaVentaCerradaSeMapeaConSusPartidasYTotal() {
        Venta venta = new Venta("F-1");
        venta.agregarPartida(new Producto("P1", "Playera Deportiva", 150.0, 20), 2);
        venta.cerrar();

        VentaResponse r = VentaResponse.from(venta);

        assertEquals("F-1", r.folio());
        assertEquals("CERRADA", r.estado());
        assertEquals(1, r.partidas().size());
        PartidaResponse p = r.partidas().get(0);
        assertEquals("P1", p.codigoProducto());
        assertEquals("Playera Deportiva", p.nombre());
        assertEquals(2, p.cantidad());
        assertEquals("150.00", p.precioUnitario().toPlainString());
        assertEquals("300.00", p.subtotal().toPlainString());
        assertEquals("300.00", r.total().toPlainString());
    }

    @Test
    void conTresPartidasSeAplicaElDescuentoPorMayoreoDelDominio() {
        Venta venta = new Venta("F-2");
        venta.agregarPartida(new Producto("P1", "Uno", 100.0, 10), 1);
        venta.agregarPartida(new Producto("P2", "Dos", 200.0, 10), 1);
        venta.agregarPartida(new Producto("P3", "Tres", 50.0, 10), 1);
        venta.cerrar();

        // 350.00 con 5% = 332.50 y el dominio redondea al entero: 333.00
        assertEquals("333.00", VentaResponse.from(venta).total().toPlainString());
    }

    @Test
    void unaVentaSinCerrarSeMarcaComoAbierta() {
        Venta venta = new Venta("F-3");
        venta.agregarPartida(new Producto("P1", "Uno", 10.0, 5), 1);

        assertEquals("ABIERTA", VentaResponse.from(venta).estado());
    }
}
