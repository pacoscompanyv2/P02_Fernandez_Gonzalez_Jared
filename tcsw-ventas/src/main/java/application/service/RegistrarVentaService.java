package application.service;

import application.observer.VentaObserver;
import application.port.in.ItemVenta;
import application.port.in.RegistrarVentaUseCase;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.Producto;
import domain.Venta;
import domain.descuento.DescuentoPorMayoreo;
import domain.descuento.DescuentoStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistrarVentaService implements RegistrarVentaUseCase {

    private final ProductoRepository productos;
    private final VentaRepository ventas;
    private final DescuentoStrategy descuento;
    private final List<VentaObserver> observers = new ArrayList<>();

    // por defecto conserva el comportamiento actual: descuento por mayoreo
    public RegistrarVentaService(ProductoRepository productos, VentaRepository ventas) {
        this(productos, ventas, new DescuentoPorMayoreo());
    }

    // la estrategia de descuento se inyecta (ver ADR-001)
    public RegistrarVentaService(ProductoRepository productos, VentaRepository ventas, DescuentoStrategy descuento) {
        if (descuento == null) {
            throw new IllegalArgumentException("La estrategia de descuento no puede ser nula");
        }
        this.productos = productos;
        this.ventas = ventas;
        this.descuento = descuento;
    }

    @Override
    public Venta registrar(String folio, List<ItemVenta> items) {
        // se valida TODO antes de tocar el inventario: si algo falla, no se descuenta nada
        Map<String, Producto> encontrados = validarItems(items);

        Venta venta = new Venta(folio, descuento);
        for (ItemVenta item : items) {
            venta.agregarPartida(encontrados.get(item.getCodigoProducto()), item.getCantidad());
        }
        ventas.guardar(venta);
        for (VentaObserver observer : observers) {
            observer.onVentaRegistrada(venta);
        }
        return venta;
    }

    public void agregarObserver(VentaObserver observer) {
        observers.add(observer);
    }

    private Map<String, Producto> validarItems(List<ItemVenta> items) {
        if (items == null) {
            throw new IllegalArgumentException("La lista de items no puede ser nula");
        }
        Map<String, Producto> encontrados = new LinkedHashMap<>();
        Map<String, Integer> solicitado = new LinkedHashMap<>();

        for (ItemVenta item : items) {
            if (item.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            String codigo = item.getCodigoProducto();
            Producto producto = productos.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + codigo));
            encontrados.put(codigo, producto);
            // si el mismo producto aparece en varias lineas, se suman
            solicitado.merge(codigo, item.getCantidad(), Integer::sum);
        }

        for (Map.Entry<String, Integer> e : solicitado.entrySet()) {
            Producto producto = encontrados.get(e.getKey());
            if (e.getValue() > producto.getExistencia()) {
                throw new IllegalStateException("Existencia insuficiente para " + e.getKey()
                    + ": disponible " + producto.getExistencia() + ", solicitado " + e.getValue());
            }
        }
        return encontrados;
    }
}