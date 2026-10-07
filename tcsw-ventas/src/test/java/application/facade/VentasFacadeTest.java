package application.facade;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.port.in.ItemVenta;
import application.service.RegistrarVentaService;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VentasFacadeTest {

    @Test
    void registraVentaCorrectamenteAtravesDelFacade() {
        ProductoRepositoryEnMemoria productos = new ProductoRepositoryEnMemoria();
        VentaRepositoryEnMemoria ventas = new VentaRepositoryEnMemoria();
        RegistrarVentaService service = new RegistrarVentaService(productos, ventas);
        VentasFacade facade = new VentasFacade(productos, ventas, service);

        facade.registrarProducto(new Producto("P1", "Playera", 100.0, 10));

        Venta venta = facade.registrarVenta("F-FACADE-1", List.of(new ItemVenta("P1", 2)));

        assertNotNull(venta);
        assertEquals(1, venta.getPartidas().size());
    }

    @Test
    void rechazaProductoInexistenteAtravesDelFacade() {
        ProductoRepositoryEnMemoria productos = new ProductoRepositoryEnMemoria();
        VentaRepositoryEnMemoria ventas = new VentaRepositoryEnMemoria();
        RegistrarVentaService service = new RegistrarVentaService(productos, ventas);
        VentasFacade facade = new VentasFacade(productos, ventas, service);

        assertThrows(IllegalArgumentException.class,
            () -> facade.registrarVenta("F-FACADE-2", List.of(new ItemVenta("NO-EXISTE", 1))));
    }

    @Test
    void listaYBuscaProductosCorrectamenteAtravesDelFacade() {
        ProductoRepositoryEnMemoria productos = new ProductoRepositoryEnMemoria();
        VentaRepositoryEnMemoria ventas = new VentaRepositoryEnMemoria();
        VentasFacade facade = new VentasFacade(productos, ventas);

        facade.registrarProducto(new Producto("P1", "Playera", 100.0, 10));

        List<Producto> lista = facade.listarProductos();
        assertEquals(1, lista.size());

        Optional<Producto> buscado = facade.buscarProducto("P1");
        assertTrue(buscado.isPresent());
        assertEquals("Playera", buscado.get().getNombre());
    }

    // ---- correcciones: la fachada ahora delega en el caso de uso ----

    @Test
    void notificaObserversAlRegistrarVentaPorElFacade() {
        VentasFacade facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera", 100.0, 10));
        List<String> folios = new ArrayList<>();
        facade.agregarObserver(venta -> folios.add(venta.getFolio()));

        facade.registrarVenta("F-FACADE-3", List.of(new ItemVenta("P1", 1)));

        assertEquals(List.of("F-FACADE-3"), folios);
    }

    @Test
    void noDescuentaExistenciaSiLaVentaSeRechazaPorElFacade() {
        VentasFacade facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        Producto p1 = new Producto("P1", "Playera", 100.0, 10);
        facade.registrarProducto(p1);
        facade.registrarProducto(new Producto("P2", "Gorra", 50.0, 1));

        assertThrows(IllegalStateException.class, () -> facade.registrarVenta("F-FACADE-4",
            List.of(new ItemVenta("P1", 3), new ItemVenta("P2", 2))));

        assertEquals(10, p1.getExistencia());
    }

    @Test
    void rechazaServicioNuloEnElFacade() {
        assertThrows(IllegalArgumentException.class, () -> new VentasFacade(
            new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria(), null));
    }
}