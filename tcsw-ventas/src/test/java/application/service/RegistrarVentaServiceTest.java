package application.service;

import application.port.in.ItemVenta;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarVentaServiceTest {

    static class ProductoRepositoryFalso implements ProductoRepository {
        private final Map<String, Producto> datos = new HashMap<>();
        void agregar(Producto p) { datos.put(p.getCodigo(), p); }
        public Optional<Producto> buscarPorCodigo(String codigo) { return Optional.ofNullable(datos.get(codigo)); }
        public void guardar(Producto producto) { datos.put(producto.getCodigo(), producto); }
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

}