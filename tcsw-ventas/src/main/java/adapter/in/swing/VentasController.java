package adapter.in.swing;

import application.facade.VentasFacade;
import application.port.in.ItemVenta;
import domain.Producto;
import domain.Venta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentasController {
    private final VentasFacade facade;
    private final List<ItemVenta> carrito = new ArrayList<>();

    public VentasController(VentasFacade facade) {
        if (facade == null) {
            throw new IllegalArgumentException("El facade no puede ser nulo");
        }
        this.facade = facade;
    }

    public List<Producto> listarProductos() {
        return facade.listarProductos();
    }

    public void agregarAlCarrito(String codigoProducto, int cantidad) {
        if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo del producto no puede estar vacio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        Optional<Producto> producto = facade.buscarProducto(codigoProducto);
        if (producto.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado: " + codigoProducto);
        }
        // validacion temprana: lo que ya hay en el carrito + lo nuevo no puede pasar de la existencia
        int enCarrito = cantidadEnCarrito(codigoProducto);
        int existencia = producto.get().getExistencia();
        if (enCarrito + cantidad > existencia) {
            throw new IllegalArgumentException("Existencia insuficiente para " + codigoProducto
                + ": disponible " + existencia + ", en carrito " + enCarrito);
        }
        carrito.add(new ItemVenta(codigoProducto, cantidad));
    }

    public void quitarDelCarrito(int indice) {
        if (indice < 0 || indice >= carrito.size()) {
            throw new IndexOutOfBoundsException("Indice de carrito invalido: " + indice);
        }
        carrito.remove(indice);
    }

    public List<ItemVenta> obtenerCarrito() {
        return new ArrayList<>(carrito);
    }

    public Venta registrarVenta(String folio) {
        if (folio == null || folio.trim().isEmpty()) {
            throw new IllegalArgumentException("El folio de la venta es requerido");
        }
        if (carrito.isEmpty()) {
            throw new IllegalStateException("El carrito esta vacio");
        }
        // si el servicio rechaza la venta, el carrito se conserva para que el usuario lo corrija
        Venta venta = facade.registrarVenta(folio, new ArrayList<>(carrito));
        carrito.clear();
        return venta;
    }

    private int cantidadEnCarrito(String codigo) {
        int total = 0;
        for (ItemVenta item : carrito) {
            if (item.getCodigoProducto().equals(codigo)) {
                total += item.getCantidad();
            }
        }
        return total;
    }
}