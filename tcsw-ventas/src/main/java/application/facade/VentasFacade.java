package application.facade;

import application.port.in.ItemVenta;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import application.service.RegistrarVentaService;
import domain.Producto;
import domain.Venta;

import java.util.List;

public class VentasFacade {

    private final ProductoRepository productos;
    private final RegistrarVentaService service;

    public VentasFacade(ProductoRepository productos, VentaRepository ventas) {
        this.productos = productos;
        this.service = new RegistrarVentaService(productos, ventas);
    }

    public void registrarProducto(Producto producto) {
        productos.guardar(producto);
    }

    public Venta registrarVenta(String folio, List<ItemVenta> items) {
        return service.registrar(folio, items);
    }
}