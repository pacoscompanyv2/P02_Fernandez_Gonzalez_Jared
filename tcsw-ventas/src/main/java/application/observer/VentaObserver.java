package application.observer;

import domain.Venta;

public interface VentaObserver {
    void onVentaRegistrada(Venta venta);
}