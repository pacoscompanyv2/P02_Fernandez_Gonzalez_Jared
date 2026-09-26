package application.observer;

import domain.Venta;

public class NotificadorConsola implements VentaObserver {
    @Override
    public void onVentaRegistrada(Venta venta) {
        System.out.println("Venta registrada: " + venta.getFolio());
    }
}