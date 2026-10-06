package application.facade;

import application.observer.VentaObserver;
import application.port.in.ItemVenta;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import application.service.RegistrarVentaService;
import domain.Producto;
import domain.Venta;

import java.util.List;
import java.util.Optional;

public class VentasFacade {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final RegistrarVentaService registrarVentaService;

    // arma el caso de uso por defecto (descuento por mayoreo)
    public VentasFacade(ProductoRepository productoRepository, VentaRepository ventaRepository) {
        this(productoRepository, ventaRepository, new RegistrarVentaService(productoRepository, ventaRepository));
    }

    public VentasFacade(ProductoRepository productoRepository, VentaRepository ventaRepository, RegistrarVentaService registrarVentaService) {
        if (registrarVentaService == null) {
            throw new IllegalArgumentException("El servicio de registro de ventas no puede ser nulo");
        }
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.registrarVentaService = registrarVentaService;
    }

    public void registrarProducto(Producto producto) {
        productoRepository.guardar(producto);
    }

    public List<Producto> listarProductos() {
        return productoRepository.listarTodos();
    }

    public Optional<Producto> buscarProducto(String codigo) {
        return productoRepository.buscarPorCodigo(codigo);
    }

    // la venta pasa por el caso de uso: valida existencias, aplica descuento y notifica observers
    public Venta registrarVenta(String folio, List<ItemVenta> items) {
        return registrarVentaService.registrar(folio, items);
    }

    public void agregarObserver(VentaObserver observer) {
        registrarVentaService.agregarObserver(observer);
    }

    public ProductoRepository getProductoRepository() {
        return productoRepository;
    }

    public VentaRepository getVentaRepository() {
        return ventaRepository;
    }
}