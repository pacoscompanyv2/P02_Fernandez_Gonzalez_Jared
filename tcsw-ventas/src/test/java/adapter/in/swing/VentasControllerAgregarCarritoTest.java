package adapter.in.swing;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.facade.VentasFacade;
import domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VentasControllerAgregarCarritoTest {

    private VentasFacade facade;
    private VentasController controller;

    @BeforeEach
    void setUp() {
        facade = new VentasFacade(new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera", 150.0, 20));
        controller = new VentasController(facade);
    }

    @Test
    @DisplayName("Exito: Agregar producto valido al carrito")
    void agregaProductoValidoAlCarrito() {
        controller.agregarAlCarrito("P1", 2);
        assertEquals(1, controller.obtenerCarrito().size());
        assertEquals("P1", controller.obtenerCarrito().get(0).getCodigoProducto());
        assertEquals(2, controller.obtenerCarrito().get(0).getCantidad());
    }

    @Test
    @DisplayName("Falla: Rechazar cantidad cero o negativa")
    void rechazaCantidadCeroONegativa() {
        Exception exCero = assertThrows(IllegalArgumentException.class,
            () -> controller.agregarAlCarrito("P1", 0));
        assertEquals("La cantidad debe ser mayor a cero", exCero.getMessage());

        Exception exNegativa = assertThrows(IllegalArgumentException.class,
            () -> controller.agregarAlCarrito("P1", -5));
        assertEquals("La cantidad debe ser mayor a cero", exNegativa.getMessage());
    }

    @Test
    @DisplayName("Falla: Rechazar producto inexistente")
    void rechazaProductoInexistente() {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> controller.agregarAlCarrito("NO-EXISTE", 1));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    @DisplayName("Falla: Rechazar codigo de producto nulo o vacio")
    void agregarCodigoNuloOVacioFalla() {
        assertThrows(IllegalArgumentException.class, () -> controller.agregarAlCarrito(null, 1));
        assertThrows(IllegalArgumentException.class, () -> controller.agregarAlCarrito("   ", 1));
    }

    @Test
    @DisplayName("Exito: Agregar el mismo producto dos veces suma lineas dentro de la existencia")
    void agregarMismoProductoDosVecesDentroDeExistencia() {
        controller.agregarAlCarrito("P1", 5);
        controller.agregarAlCarrito("P1", 5);
        assertEquals(2, controller.obtenerCarrito().size());
    }
}
