package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DineroTest {

    @Test
    void sumaDosMontos() {
        Dinero a = Dinero.de(10.50);
        Dinero b = Dinero.de(5.25);
        assertEquals(Dinero.de(15.75), a.mas(b));
    }

    @Test
    void multiplicaPorCantidad() {
        Dinero precio = Dinero.de(20.0);
        assertEquals(Dinero.de(60.0), precio.por(3));
    }

    @Test
    void rechazaMontoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> Dinero.de(-1.0));
    }

    @Test
    void dosMontosIgualesSonIguales() {
        assertEquals(Dinero.de(100.0), Dinero.de(100.0));
    }
        @Test
    void aplicaDescuentoCorrectamente() {
    Dinero monto = Dinero.de(100.00);
    Dinero conDescuento = monto.conDescuento(10);
    assertEquals(Dinero.de(90.00), conDescuento);
}

@Test
    void rechazaPorcentajeDeDescuentoInvalido() {
    Dinero monto = Dinero.de(100.00);
    assertThrows(IllegalArgumentException.class, () -> monto.conDescuento(150));
}

@Test
        void redondeaAlEnteroMasCercano() {
    Dinero monto = Dinero.de(12.50);
    assertEquals(Dinero.de(13.00), monto.redondeadoEntero());
}
}
