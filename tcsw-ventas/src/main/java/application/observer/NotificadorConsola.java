package application.observer;

import domain.Venta;

import java.util.logging.Logger;

public class NotificadorConsola implements VentaObserver {

    private static final Logger LOGGER = Logger.getLogger(NotificadorConsola.class.getName());

    @Override
    public void onVentaRegistrada(Venta venta) {
        LOGGER.info("Venta registrada: " + venta.getFolio());
    }
}

