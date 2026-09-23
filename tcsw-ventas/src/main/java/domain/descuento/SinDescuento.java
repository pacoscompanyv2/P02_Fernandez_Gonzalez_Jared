package domain.descuento;

import domain.Dinero;

public class SinDescuento implements DescuentoStrategy {
    @Override
    public Dinero aplicar(Dinero subtotal, int cantidadPartidas) {
        return subtotal;
    }
}