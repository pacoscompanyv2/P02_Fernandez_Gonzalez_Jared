package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// agrupa las partidas de una venta y controla su ciclo de vida (abierta/cerrada)
public final class Venta {

    private final String folio;
    private final List<DetalleVenta> partidas;
    private boolean cerrada;

    public Venta(String folio) {
        if (folio == null || folio.isBlank()) {
            throw new IllegalArgumentException("El folio no puede estar vacio");
        }

        this.folio = folio;
        this.partidas = new ArrayList<>();
        this.cerrada = false;
    }

    public String getFolio() {
        return folio;
    }

    public List<DetalleVenta> getPartidas() {
        // se regresa una copia de solo lectura para que no la modifiquen desde afuera
        return Collections.unmodifiableList(partidas);
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public void agregarPartida(Producto producto, int cantidad) {
        if (cerrada) {
            throw new IllegalStateException("No se pueden agregar partidas a una venta cerrada");
        }

        // primero se descuenta el stock, si falla aqui ya no se agrega la partida
        producto.descontar(cantidad);
        partidas.add(new DetalleVenta(producto, cantidad));
    }

    public Dinero calcularTotal() {
        Dinero total = Dinero.cero();
        for (DetalleVenta partida : partidas) {
            total = total.mas(partida.subtotal());
        }
        return total;
    }

    public void cerrar() {
        if (partidas.isEmpty()) {
            throw new IllegalStateException("No se puede cerrar una venta sin partidas");
        }

        cerrada = true;
    }
}
