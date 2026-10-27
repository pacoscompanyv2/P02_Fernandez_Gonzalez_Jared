package application.facade;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.port.in.ItemVenta;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VentasFacadeBuscarVentaTest {

    private VentasFacade facade;

    @BeforeEach
    void preparar() {
        facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera", 100.0, 10));
    }

    @Test
    void encuentraUnaVentaYaRegistrada() {
        facade.registrarVenta("F-1", List.of(new ItemVenta("P1", 2)));

        Optional<Venta> encontrada = facade.buscarVenta("F-1");

        assertTrue(encontrada.isPresent());
        assertEquals("F-1", encontrada.get().getFolio());
        assertEquals(1, encontrada.get().getPartidas().size());
    }

    @Test
    void unFolioInexistenteRegresaVacio() {
        assertFalse(facade.buscarVenta("NO-EXISTE").isPresent());
    }

    @Test
    void consultarNoCambiaLasExistencias() {
        facade.registrarVenta("F-2", List.of(new ItemVenta("P1", 3)));

        facade.buscarVenta("F-2");
        facade.buscarVenta("F-2");

        assertEquals(7, facade.buscarProducto("P1").orElseThrow(AssertionError::new).getExistencia());
    }
}
