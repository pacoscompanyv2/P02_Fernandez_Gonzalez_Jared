package application.port.in;

// linea de pedido que entra al caso de uso: que producto y cuantos
public final class ItemVenta {

    private final String codigoProducto;
    private final int cantidad;

    public ItemVenta(String codigoProducto, int cantidad) {
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }
}