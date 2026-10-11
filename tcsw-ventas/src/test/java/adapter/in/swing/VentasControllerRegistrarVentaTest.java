package adapter.in.swing;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.facade.VentasFacade;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VentasControllerRegistrarVentaTest {

    private VentasFacade facade;
    private VentasController controller;

    @BeforeEach
    void setUp() {
        facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera", 150.0, 20));
        controller = new VentasController(facade);
    }

    @Test
    @DisplayName("Exito: Registrar venta con carrito valido")
    void registraVentaConCarritoValido() {
        controller.agregarAlCarrito("P1", 2);
        Venta venta = controller.registrarVenta("F-SWING-1");

        assertNotNull(venta);
        assertEquals("F-SWING-1", venta.getFolio());
        assertTrue(controller.obtenerCarrito().isEmpty());
    }

    @Test
    @DisplayName("Exito: Registrar la venta descuenta la existencia del catalogo")
    void registrarVentaDescuentaExistencia() {
        controller.agregarAlCarrito("P1", 2);
        controller.registrarVenta("F-SWING-3");

        assertEquals(18, controller.listarProductos().get(0).getExistencia());
    }

    @Test
    @DisplayName("Falla: Rechazar registro de venta con carrito vacio")
    void rechazaRegistrarVentaConCarritoVacio() {
        Exception ex = assertThrows(IllegalStateException.class,
            () -> controller.registrarVenta("F-SWING-2"));
        assertEquals("El carrito esta vacio", ex.getMessage());
    }

    @Test
    @DisplayName("Falla: Registrar venta con folio nulo o vacio")
    void registrarVentaFolioInvalidoFalla() {
        controller.agregarAlCarrito("P1", 1);
        assertThrows(IllegalArgumentException.class, () -> controller.registrarVenta(null));
        assertThrows(IllegalArgumentException.class, () -> controller.registrarVenta("   "));
        assertEquals(1, controller.obtenerCarrito().size());
    }
}
