package application.service;

import application.port.in.ItemVenta;
import application.port.in.RegistrarVentaUseCase;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.Producto;
import domain.Venta;

import java.util.List;

public class RegistrarVentaService implements RegistrarVentaUseCase {

    private final ProductoRepository productos;
    private final VentaRepository ventas;

    public RegistrarVentaService(ProductoRepository productos, VentaRepository ventas) {
        this.productos = productos;
        this.ventas = ventas;
    }

    @Override
    public Venta registrar(String folio, List<ItemVenta> items) {
        Venta venta = new Venta(folio);
        for (ItemVenta item : items) {
            Producto producto = productos.buscarPorCodigo(item.getCodigoProducto())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Producto no encontrado: " + item.getCodigoProducto()));
            venta.agregarPartida(producto, item.getCantidad());
        }
        ventas.guardar(venta);
        return venta;
    }
}