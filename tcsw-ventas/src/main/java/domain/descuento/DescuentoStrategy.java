package domain.descuento;

import domain.Dinero;

public interface DescuentoStrategy {
    Dinero aplicar(Dinero subtotal, int cantidadPartidas);
}   