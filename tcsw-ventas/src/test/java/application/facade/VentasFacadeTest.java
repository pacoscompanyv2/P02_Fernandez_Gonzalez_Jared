package application.facade;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.port.in.ItemVenta;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VentasFacadeTest {

    @Test
    void registraVentaCorrectamenteAtravesDelFacade() {
        VentasFacade facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera", 100.0, 10));

        Venta venta = facade.registrarVenta("F-FACADE-1", List.of(new ItemVenta("P1", 2)));

        assertEquals(1, venta.getPartidas().size());
    }

    @Test
    void rechazaProductoInexistenteAtravesDelFacade() {
        VentasFacade facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());

        assertThrows(IllegalArgumentException.class,
            () -> facade.registrarVenta("F-FACADE-2", List.of(new ItemVenta("NO-EXISTE", 1))));
    }
}