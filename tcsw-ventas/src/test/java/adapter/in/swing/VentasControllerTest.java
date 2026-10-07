package adapter.in.swing;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.facade.VentasFacade;
import domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VentasControllerTest {

    private VentasFacade facade;
    private VentasController controller;

    @BeforeEach
    void setUp() {
        facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera", 150.0, 20));
        controller = new VentasController(facade);
    }

    @Test
    @DisplayName("Exito: Listar productos registrados correctamente")
    void listarProductosExito() {
        List<Producto> productos = controller.listarProductos();
        assertEquals(1, productos.size());
        assertEquals("Playera", productos.get(0).getNombre());
    }

    @Test
    @DisplayName("Falla: Inicializar controlador con Facade nulo lanza excepcion")
    void controladorConFacadeNuloFalla() {
        assertThrows(IllegalArgumentException.class, () -> new VentasController(null));
    }

    @Test
    @DisplayName("Exito: Quitar un elemento existente del carrito")
    void quitarDelCarritoExito() {
        controller.agregarAlCarrito("P1", 2);
        assertEquals(1, controller.obtenerCarrito().size());

        controller.quitarDelCarrito(0);
        assertTrue(controller.obtenerCarrito().isEmpty());
    }

    @Test
    @DisplayName("Falla: Quitar elemento con indice fuera de rango lanza excepcion")
    void quitarDelCarritoIndiceInvalidoFalla() {
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> controller.quitarDelCarrito(0));
        assertTrue(exception.getMessage().contains("Indice de carrito invalido"));
    }

    @Test
    @DisplayName("Falla: Agregar mas unidades que la existencia lanza excepcion")
    void agregarMasQueLaExistenciaFalla() {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> controller.agregarAlCarrito("P1", 21));
        assertTrue(ex.getMessage().contains("Existencia insuficiente"));
        assertTrue(controller.obtenerCarrito().isEmpty());
    }

    @Test
    @DisplayName("Falla: Lo acumulado en el carrito tambien cuenta contra la existencia")
    void acumuladoDelCarritoExcedeExistenciaFalla() {
        controller.agregarAlCarrito("P1", 15);

        assertThrows(IllegalArgumentException.class, () -> controller.agregarAlCarrito("P1", 6));
        assertEquals(1, controller.obtenerCarrito().size());
    }

    @Test
    @DisplayName("Falla: Si el registro es rechazado el carrito se conserva")
    void carritoSeConservaSiElRegistroFalla() {
        controller.agregarAlCarrito("P1", 15);
        // la existencia baja por otra via despues de armar el carrito
        facade.buscarProducto("P1").get().descontar(10);

        assertThrows(IllegalStateException.class, () -> controller.registrarVenta("F-CTRL-1"));

        assertEquals(1, controller.obtenerCarrito().size());
        assertEquals(10, facade.buscarProducto("P1").get().getExistencia());
    }
}