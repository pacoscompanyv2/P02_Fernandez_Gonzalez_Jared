package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoTest {

    @Test
    void creaProductoValido() {
        Producto p = new Producto("P001", "Teclado", 250.0, 10);
        assertEquals("P001", p.getCodigo());
        assertEquals("Teclado", p.getNombre());
        assertEquals(250.0, p.getPrecio());
        assertEquals(10, p.getExistencia());
    }

    @Test
    void rechazaPrecioNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto("P002", "Mouse", -50.0, 5));
    }

    @Test
    void rechazaExistenciaNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto("P003", "Monitor", 1500.0, -1));
    }

    @Test
    void rechazaNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto("P004", "  ", 100.0, 3));
    }

    @Test
    void descuentaCantidadDisponible() {
        Producto p = new Producto("P005", "Bocina", 400.0, 8);
        p.descontar(3);
        assertEquals(5, p.getExistencia());
    }

    @Test
    void rechazaCantidadCero() {
        Producto p = new Producto("P006", "Cable", 30.0, 4);
        assertThrows(IllegalArgumentException.class, () -> p.descontar(0));
    }

    @Test
    void rechazaDescuentoMayorQueExistencia() {
        Producto p = new Producto("P007", "Audifonos", 600.0, 2);
        assertThrows(IllegalStateException.class, () -> p.descontar(5));
    }

    @Test
    void conservaExistenciaDespuesDeRechazo() {
        Producto p = new Producto("P008", "Webcam", 350.0, 6);
        assertThrows(IllegalStateException.class, () -> p.descontar(10));
        assertEquals(6, p.getExistencia());
    }
}
