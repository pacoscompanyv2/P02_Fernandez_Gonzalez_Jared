package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VentaTest {

    @Test
    void agregaPartidaYDescuentaExistencia() {
        Producto p = new Producto("P020", "Teclado", 300.0, 10);
        Venta venta = new Venta("V001");
        venta.agregarPartida(p, 2);
        assertEquals(8, p.getExistencia());
        assertEquals(1, venta.getPartidas().size());
    }

    @Test
    void calculaTotalConVariasPartidas() {
        Producto p1 = new Producto("P021", "Mouse", 100.0, 10);
        Producto p2 = new Producto("P022", "Monitor", 1500.0, 5);
        Venta venta = new Venta("V002");
        venta.agregarPartida(p1, 2);
        venta.agregarPartida(p2, 1);
        assertEquals(Dinero.de(1700.0), venta.calcularTotal());
    }

    @Test
    void rechazaPartidaSinExistenciaSuficiente() {
        Producto p = new Producto("P023", "Audifonos", 500.0, 1);
        Venta venta = new Venta("V003");
        assertThrows(IllegalStateException.class, () -> venta.agregarPartida(p, 5));
    }

    @Test
    void rechazaAgregarPartidaAVentaCerrada() {
        Producto p = new Producto("P024", "Webcam", 400.0, 5);
        Venta venta = new Venta("V004");
        venta.agregarPartida(p, 1);
        venta.cerrar();
        assertThrows(IllegalStateException.class, () -> venta.agregarPartida(p, 1));
    }

    @Test
    void rechazaCerrarVentaSinPartidas() {
        Venta venta = new Venta("V005");
        assertThrows(IllegalStateException.class, venta::cerrar);
    }

    @Test
    void ventaTotalEsCeroSinPartidas() {
        Venta venta = new Venta("V006");
        assertEquals(Dinero.cero(), venta.calcularTotal());
    }

    @Test
    void rechazaFolioVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Venta(" "));
    }

    @Test
    void ventaNoCerradaPorDefecto() {
        Venta venta = new Venta("V007");
        assertTrue(!venta.isCerrada());
    }
}
