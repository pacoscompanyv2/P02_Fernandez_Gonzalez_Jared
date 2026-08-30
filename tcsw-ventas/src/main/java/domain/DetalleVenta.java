package domain;

// representa una linea dentro de una venta: un producto y cuantos se llevaron
public final class DetalleVenta {

    private final Producto producto;
    private final int cantidad;

    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }

        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    // precio del producto multiplicado por la cantidad de esta linea
    public Dinero subtotal() {
        Dinero precioUnitario = Dinero.de(producto.getPrecio());
        return precioUnitario.por(cantidad);
    }
}
