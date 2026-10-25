package adapter.out.jdbc;

import application.facade.VentasFacade;
import application.port.in.ItemVenta;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// recorrido COMPLETO por el nucleo hexagonal con PostgreSQL: fachada -> caso de uso -> puertos -> adaptadores JDBC.
// demuestra que los puertos se conservaron: el nucleo no cambio, solo cambio el adaptador.
class VentasFacadeJdbcIT extends PostgresTestBase {

    private VentasFacade facade;

    @BeforeEach
    void armarFachada() {
        facade = new VentasFacade(new ProductoRepositoryJdbc(conexion), new VentaRepositoryJdbc(conexion));
        facade.registrarProducto(new Producto("P1", "Playera Deportiva", 150.0, 20));
        facade.registrarProducto(new Producto("P2", "Pantalon Mezclilla", 350.0, 15));
    }

    @Test
    void registrarUnaVentaPorLaFachadaDescuentaLaExistenciaEnPostgres() throws SQLException {
        Venta venta = facade.registrarVenta("F-100", List.of(new ItemVenta("P1", 3)));

        assertEquals("F-100", venta.getFolio());
        assertEquals(17, existenciaEnBd("P1"));
        assertTrue(facade.getVentaRepository().buscarPorFolio("F-100").isPresent());
        assertEquals(17, facade.buscarProducto("P1").orElseThrow(AssertionError::new).getExistencia());
    }

    @Test
    void loPersistidoSobreviveAUnaFachadaNueva() {
        facade.registrarVenta("F-101", List.of(new ItemVenta("P2", 5)));

        // simula cerrar y volver a abrir la aplicacion
        VentasFacade nueva = new VentasFacade(new ProductoRepositoryJdbc(conexion), new VentaRepositoryJdbc(conexion));

        assertEquals(2, nueva.listarProductos().size());
        assertEquals(10, nueva.buscarProducto("P2").orElseThrow(AssertionError::new).getExistencia());
        assertTrue(nueva.getVentaRepository().buscarPorFolio("F-101").isPresent());
    }

    @Test
    void existenciaInsuficienteSeRechazaYLaBaseNoCambia() throws SQLException {
        List<ItemVenta> items = Collections.singletonList(new ItemVenta("P1", 99));

        assertThrows(IllegalStateException.class, () -> facade.registrarVenta("F-102", items));

        assertEquals(20, existenciaEnBd("P1"));
        assertEquals(0, consultarEntero("SELECT count(*) FROM venta"));
    }

    @Test
    void productoInexistenteSeRechazaYLaBaseNoCambia() throws SQLException {
        List<ItemVenta> items = Collections.singletonList(new ItemVenta("ZZ", 1));

        assertThrows(IllegalArgumentException.class, () -> facade.registrarVenta("F-103", items));

        assertEquals(0, consultarEntero("SELECT count(*) FROM venta"));
        assertFalse(facade.getVentaRepository().buscarPorFolio("F-103").isPresent());
    }
}
