package domain.descuento;

import domain.Dinero;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DescuentoStrategyTest {

    @Test
    void mayoreoAplicaDescuentoConTresOMasPartidas() {
        DescuentoStrategy estrategia = new DescuentoPorMayoreo();
        Dinero resultado = estrategia.aplicar(Dinero.de(100.0), 3);
        assertEquals(Dinero.de(95.0), resultado);
    }

    @Test
    void mayoreoNoAplicaDescuentoConMenosDeTresPartidas() {
        DescuentoStrategy estrategia = new DescuentoPorMayoreo();
        Dinero resultado = estrategia.aplicar(Dinero.de(100.0), 2);
        assertEquals(Dinero.de(100.0), resultado);
    }

    @Test
    void sinDescuentoNuncaAplicaDescuento() {
        DescuentoStrategy estrategia = new SinDescuento();
        Dinero resultado = estrategia.aplicar(Dinero.de(100.0), 10);
        assertEquals(Dinero.de(100.0), resultado);
    }

    @Test
    void factoryRegresaLaEstrategiaCorrecta() {
        assertEquals(DescuentoPorMayoreo.class, DescuentoStrategyFactory.crear("MAYOREO").getClass());
        assertEquals(SinDescuento.class, DescuentoStrategyFactory.crear("NINGUNO").getClass());
    }

    @Test
    void factoryRechazaTipoDesconocido() {
        assertThrows(IllegalArgumentException.class, () -> DescuentoStrategyFactory.crear("NO-EXISTE"));
    }

    @Test
    void ventaConSinDescuentoNoAplicaDescuentoAunConVariasPartidas() {
        Producto producto = new Producto("P1", "Playera", 50.0, 100);

        Venta ventaConDescuento = new Venta("F-STRAT-1");
        ventaConDescuento.agregarPartida(producto, 1);
        ventaConDescuento.agregarPartida(producto, 1);
        ventaConDescuento.agregarPartida(producto, 1);

        Venta ventaSinDescuento = new Venta("F-STRAT-2", new SinDescuento());
        ventaSinDescuento.agregarPartida(producto, 1);
        ventaSinDescuento.agregarPartida(producto, 1);
        ventaSinDescuento.agregarPartida(producto, 1);

        assertEquals(Dinero.de(143.0), ventaConDescuento.calcularTotal());
        assertEquals(Dinero.de(150.0), ventaSinDescuento.calcularTotal());
    }
}