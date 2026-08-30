package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DetalleVentaTest {

    @Test
    void calculaSubtotal() {
        Producto p = new Producto("P010", "Cable HDMI", 150.0, 20);
        DetalleVenta detalle = new DetalleVenta(p, 3);
        assertEquals(Dinero.de(450.0), detalle.subtotal());
    }

    @Test
    void rechazaProductoNulo() {
        assertThrows(IllegalArgumentException.class, () -> new DetalleVenta(null, 1));
    }

    @Test
    void rechazaCantidadCeroOMenor() {
        Producto p = new Producto("P011", "Mousepad", 80.0, 10);
        assertThrows(IllegalArgumentException.class, () -> new DetalleVenta(p, 0));
    }
}
