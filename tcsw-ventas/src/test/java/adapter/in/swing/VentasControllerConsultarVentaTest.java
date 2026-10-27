package adapter.in.swing;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.facade.VentasFacade;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VentasControllerConsultarVentaTest {

    private VentasController controller;

    @BeforeEach
    void preparar() {
        VentasFacade facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera", 150.0, 20));
        controller = new VentasController(facade);
        controller.agregarAlCarrito("P1", 2);
        controller.registrarVenta("F-100");
    }

    @Test
    @DisplayName("Exito: consulta una venta registrada, cerrada y con su partida")
    void consultaUnaVentaRegistrada() {
        Venta venta = controller.consultarVenta("F-100");

        assertEquals("F-100", venta.getFolio());
        assertTrue(venta.isCerrada());
        assertEquals(1, venta.getPartidas().size());
    }

    @Test
    @DisplayName("Exito: el folio se limpia de espacios")
    void limpiaEspacios() {
        assertEquals("F-100", controller.consultarVenta("  F-100  ").getFolio());
    }

    @Test
    @DisplayName("Falla: folio vacio o nulo se rechaza")
    void folioVacioSeRechaza() {
        assertThrows(IllegalArgumentException.class, () -> controller.consultarVenta("   "));
        assertThrows(IllegalArgumentException.class, () -> controller.consultarVenta(null));
    }

    @Test
    @DisplayName("Falla: folio inexistente da un mensaje con el folio")
    void folioInexistenteSeRechaza() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> controller.consultarVenta("F-NO"));
        assertTrue(e.getMessage().contains("F-NO"));
    }
}
