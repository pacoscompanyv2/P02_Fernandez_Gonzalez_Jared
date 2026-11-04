package application.service;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.exception.ExistenciaInsuficienteException;
import application.exception.ProductoNoEncontradoException;
import application.port.in.ItemVenta;
import domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// P09: los errores del caso de uso ahora son tipos propios (la API REST los traduce a 404 y 409) pero siguen siendo
// IllegalArgumentException / IllegalStateException con el mismo texto de siempre
class RegistrarVentaExcepcionesTest {

    private RegistrarVentaService servicio;

    @BeforeEach
    void preparar() {
        ProductoRepositoryEnMemoria productos = new ProductoRepositoryEnMemoria();
        productos.guardar(new Producto("P1", "Playera", 100.0, 3));
        servicio = new RegistrarVentaService(productos, new VentaRepositoryEnMemoria());
    }

    @Test
    void productoInexistenteLanzaProductoNoEncontradoConElTextoDeSiempre() {
        List<ItemVenta> pedido = List.of(new ItemVenta("NOPE", 1));
        ProductoNoEncontradoException e = assertThrows(ProductoNoEncontradoException.class,
            () -> servicio.registrar("F-1", pedido));
        assertEquals("NOPE", e.getCodigo());
        assertEquals("Producto no encontrado: NOPE", e.getMessage());
        // compatibilidad: sigue siendo una IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar("F-1", pedido));
    }

    @Test
    void existenciaInsuficienteLanzaSuTipoConLosNumeros() {
        List<ItemVenta> pedido = List.of(new ItemVenta("P1", 2), new ItemVenta("P1", 2));
        ExistenciaInsuficienteException e = assertThrows(ExistenciaInsuficienteException.class,
            () -> servicio.registrar("F-2", pedido));
        assertEquals("P1", e.getCodigo());
        assertEquals(3, e.getDisponible());
        assertEquals(4, e.getSolicitado());
        assertEquals("Existencia insuficiente para P1: disponible 3, solicitado 4", e.getMessage());
        // compatibilidad: sigue siendo una IllegalStateException
        assertThrows(IllegalStateException.class, () -> servicio.registrar("F-2", pedido));
    }
}
