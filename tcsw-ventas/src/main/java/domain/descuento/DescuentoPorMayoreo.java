package domain.descuento;

import domain.Dinero;

public class DescuentoPorMayoreo implements DescuentoStrategy {
    @Override
    public Dinero aplicar(Dinero subtotal, int cantidadPartidas) {
        if (cantidadPartidas >= 3) {
            return subtotal.conDescuento(5);
        }
        return subtotal;
    }
}