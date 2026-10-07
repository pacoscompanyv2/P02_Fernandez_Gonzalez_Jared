package application.service;

import application.port.in.ItemVenta;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.Dinero;
import domain.Producto;
import domain.Venta;
import domain.descuento.SinDescuento;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarVentaServiceTest {

    static class ProductoRepositoryFalso implements ProductoRepository {
        private final Map<String, Producto> datos = new HashMap<>();
        void agregar(Producto p) { datos.put(p.getCodigo(), p); }
        public Optional<Producto> buscarPorCodigo(String codigo) { return Optional.ofNullable(datos.get(codigo)); }
        public void guardar(Producto producto) { datos.put(producto.getCodigo(), producto); }
        public List<Producto> listarTodos() { return new ArrayList<>(datos.values()); }
    }

    static class VentaRepositoryFalso implements VentaRepository {
        private final Map<String, Venta> datos = new HashMap<>();
        public void guardar(Venta venta) { datos.put(venta.getFolio(), venta); }
        public Optional<Venta> buscarPorFolio(String folio) { return Optional.ofNullable(datos.get(folio)); }
    }

    @Test
    void registraVentaCorrectamente() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        productos.agregar(new Producto("P1", "Playera", 100.0, 10));
        VentaRepositoryFalso ventas = new VentaRepositoryFalso();
        RegistrarVentaService service = new RegistrarVentaService(productos, ventas);

        Venta venta = service.registrar("F1", List.of(new ItemVenta("P1", 2)));

        assertEquals(1, venta.getPartidas().size());
        assertTrue(ventas.buscarPorFolio("F1").isPresent());
    }

    @Test
    void rechazaProductoInexistente() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        VentaRepositoryFalso ventas = new VentaRepositoryFalso();
        RegistrarVentaService service = new RegistrarVentaService(productos, ventas);

        assertThrows(IllegalArgumentException.class,
            () -> service.registrar("F2", List.of(new ItemVenta("NO-EXISTE", 1))));
    }

    @Test
    void notificaAlObserverCuandoRegistraConExito() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        productos.agregar(new Producto("P1", "Playera", 100.0, 10));
        VentaRepositoryFalso ventas = new VentaRepositoryFalso();
        RegistrarVentaService service = new RegistrarVentaService(productos, ventas);

        List<String> notificados = new ArrayList<>();
        service.agregarObserver(venta -> notificados.add(venta.getFolio()));

        service.registrar("F-OBS-1", List.of(new ItemVenta("P1", 1)));

        assertEquals(1, notificados.size());
        assertEquals("F-OBS-1", notificados.get(0));
    }

    @Test
    void noNotificaAlObserverSiElRegistroFalla() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        VentaRepositoryFalso ventas = new VentaRepositoryFalso();
        RegistrarVentaService service = new RegistrarVentaService(productos, ventas);

        List<String> notificados = new ArrayList<>();
        service.agregarObserver(venta -> notificados.add(venta.getFolio()));

        assertThrows(IllegalArgumentException.class,
            () -> service.registrar("F-OBS-2", List.of(new ItemVenta("NO-EXISTE", 1))));

        assertEquals(0, notificados.size());
    }

    // ---- correcciones: inventario atomico y estrategia inyectable ----

    @Test
    void noDescuentaInventarioSiUnaLineaNoTieneExistencia() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        Producto p1 = new Producto("P1", "Playera", 100.0, 10);
        productos.agregar(p1);
        productos.agregar(new Producto("P2", "Gorra", 50.0, 1));
        VentaRepositoryFalso ventas = new VentaRepositoryFalso();
        RegistrarVentaService service = new RegistrarVentaService(productos, ventas);

        assertThrows(IllegalStateException.class, () -> service.registrar("F-STOCK-1",
            List.of(new ItemVenta("P1", 2), new ItemVenta("P2", 5))));

        assertEquals(10, p1.getExistencia());
        assertFalse(ventas.buscarPorFolio("F-STOCK-1").isPresent());
    }

    @Test
    void sumaLasLineasRepetidasDelMismoProductoAntesDeValidarExistencia() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        Producto p1 = new Producto("P1", "Playera", 100.0, 5);
        productos.agregar(p1);
        RegistrarVentaService service = new RegistrarVentaService(productos, new VentaRepositoryFalso());

        assertThrows(IllegalStateException.class, () -> service.registrar("F-STOCK-2",
            List.of(new ItemVenta("P1", 3), new ItemVenta("P1", 3))));

        assertEquals(5, p1.getExistencia());
    }

    @Test
    void rechazaCantidadNoPositivaSinTocarInventario() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        Producto p1 = new Producto("P1", "Playera", 100.0, 5);
        productos.agregar(p1);
        RegistrarVentaService service = new RegistrarVentaService(productos, new VentaRepositoryFalso());

        assertThrows(IllegalArgumentException.class,
            () -> service.registrar("F-STOCK-3", List.of(new ItemVenta("P1", 0))));

        assertEquals(5, p1.getExistencia());
    }

    @Test
    void usaLaEstrategiaDeDescuentoInyectada() {
        List<ItemVenta> tresLineas = List.of(
            new ItemVenta("P1", 1), new ItemVenta("P2", 1), new ItemVenta("P3", 1));

        ProductoRepositoryFalso productosA = productosConTresArticulos();
        Venta conMayoreo = new RegistrarVentaService(productosA, new VentaRepositoryFalso())
            .registrar("F-DESC-1", tresLineas);

        ProductoRepositoryFalso productosB = productosConTresArticulos();
        Venta sinDescuento = new RegistrarVentaService(productosB, new VentaRepositoryFalso(), new SinDescuento())
            .registrar("F-DESC-2", tresLineas);

        assertEquals(Dinero.de(285.0), conMayoreo.calcularTotal());
        assertEquals(Dinero.de(300.0), sinDescuento.calcularTotal());
    }

    @Test
    void rechazaEstrategiaDeDescuentoNula() {
        assertThrows(IllegalArgumentException.class,
            () -> new RegistrarVentaService(new ProductoRepositoryFalso(), new VentaRepositoryFalso(), null));
    }

    private ProductoRepositoryFalso productosConTresArticulos() {
        ProductoRepositoryFalso productos = new ProductoRepositoryFalso();
        productos.agregar(new Producto("P1", "Playera", 100.0, 10));
        productos.agregar(new Producto("P2", "Pantalon", 100.0, 10));
        productos.agregar(new Producto("P3", "Gorra", 100.0, 10));
        return productos;
    }
}