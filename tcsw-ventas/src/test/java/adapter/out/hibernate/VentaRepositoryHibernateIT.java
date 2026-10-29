package adapter.out.hibernate;

import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// integracion de VentaRepositoryHibernate contra PostgreSQL REAL (base *_test). mvn verify -Pintegracion
// mismos casos que VentaRepositoryJdbcIT: los dos adaptadores cumplen el mismo contrato.
class VentaRepositoryHibernateIT extends HibernateTestBase {

    private ProductoRepositoryHibernate productos;
    private VentaRepositoryHibernate ventas;

    @BeforeEach
    void sembrarCatalogo() throws SQLException {
        productos = new ProductoRepositoryHibernate(sessionFactory);
        ventas = new VentaRepositoryHibernate(sessionFactory);
        sembrarProducto("P1", "Playera Deportiva", 150.0, 20);
        sembrarProducto("P2", "Pantalon Mezclilla", 350.0, 15);
        sembrarProducto("P3", "Gorra Urbana", 99.0, 30);
    }

    private Producto cargar(String codigo) {
        return productos.buscarPorCodigo(codigo).orElseThrow(AssertionError::new);
    }

    @Test
    void guardarPersisteVentaPartidasYDescuentaExistencias() throws SQLException {
        Venta venta = new Venta("F-001");
        venta.agregarPartida(cargar("P1"), 2);
        venta.agregarPartida(cargar("P2"), 1);

        ventas.guardar(venta);

        assertEquals(1, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(2, consultarEntero("SELECT count(*) FROM detalle_venta"));
        assertEquals(18, existenciaEnBd("P1"));
        assertEquals(14, existenciaEnBd("P2"));
        assertEquals(venta.calcularTotal().valor(),
            Double.parseDouble(consultarTexto("SELECT total FROM venta WHERE folio = 'F-001'")), 0.001);
    }

    @Test
    void elTotalGuardadoIncluyeElDescuentoPorMayoreo() throws SQLException {
        Venta venta = new Venta("F-002");
        venta.agregarPartida(cargar("P1"), 1);
        venta.agregarPartida(cargar("P2"), 1);
        venta.agregarPartida(cargar("P3"), 1);   // 3 partidas -> 5% de descuento

        ventas.guardar(venta);

        double guardado = Double.parseDouble(consultarTexto("SELECT total FROM venta WHERE folio = 'F-002'"));
        assertEquals(venta.calcularTotal().valor(), guardado, 0.001);
        assertTrue(guardado < 150.0 + 350.0 + 99.0);
    }

    @Test
    void buscarPorFolioReconstruyeLaVentaSinTocarLasExistencias() throws SQLException {
        Venta venta = new Venta("F-001");
        venta.agregarPartida(cargar("P1"), 2);
        venta.agregarPartida(cargar("P2"), 1);
        ventas.guardar(venta);

        Optional<Venta> recuperada = ventas.buscarPorFolio("F-001");

        assertTrue(recuperada.isPresent());
        assertEquals("F-001", recuperada.get().getFolio());
        assertEquals(2, recuperada.get().getPartidas().size());
        assertEquals("P1", recuperada.get().getPartidas().get(0).getProducto().getCodigo());
        assertEquals(2, recuperada.get().getPartidas().get(0).getCantidad());
        assertEquals(venta.calcularTotal(), recuperada.get().calcularTotal());
        assertFalse(recuperada.get().isCerrada());
        // reconstruir NO debe descontar de nuevo
        assertEquals(18, existenciaEnBd("P1"));
        assertEquals(14, existenciaEnBd("P2"));
    }

    @Test
    void unaVentaCerradaSeRecuperaCerrada() throws SQLException {
        Venta venta = new Venta("F-003");
        venta.agregarPartida(cargar("P3"), 4);
        venta.cerrar();
        ventas.guardar(venta);

        assertTrue(ventas.buscarPorFolio("F-003").orElseThrow(AssertionError::new).isCerrada());
        assertEquals("true", consultarTexto("SELECT cerrada::text FROM venta WHERE folio = 'F-003'"));
    }

    @Test
    void elMismoProductoEnDosPartidasDescuentaUnaVezCadaCantidad() throws SQLException {
        Producto p1 = cargar("P1");
        Venta venta = new Venta("F-004");
        venta.agregarPartida(p1, 2);
        venta.agregarPartida(p1, 3);

        ventas.guardar(venta);

        assertEquals(15, existenciaEnBd("P1"));
        assertEquals(2, consultarEntero("SELECT count(*) FROM detalle_venta WHERE folio = 'F-004'"));
    }

    @Test
    void buscarUnFolioQueNoExisteRegresaVacio() {
        assertFalse(ventas.buscarPorFolio("NO-EXISTE").isPresent());
    }

    @Test
    void unFolioDuplicadoSeRechazaYNoDescuentaDosVeces() throws SQLException {
        Venta primera = new Venta("F-001");
        primera.agregarPartida(cargar("P1"), 2);
        ventas.guardar(primera);

        Venta repetida = new Venta("F-001");
        repetida.agregarPartida(cargar("P1"), 3);

        RepositorioHibernateException e = assertThrows(RepositorioHibernateException.class, () -> ventas.guardar(repetida));

        assertEquals("23505", e.getSqlState());      // unique_violation
        assertEquals(1, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(1, consultarEntero("SELECT count(*) FROM detalle_venta"));
        assertEquals(18, existenciaEnBd("P1"));       // el descuento de la venta repetida se revirtio
    }

    @Test
    void siUnProductoNoExisteSeRevierteTodaLaTransaccion() throws SQLException {
        Venta venta = new Venta("F-005");
        venta.agregarPartida(cargar("P1"), 4);                                      // este si existe
        venta.agregarPartida(new Producto("X9", "Fantasma", 10.0, 5), 1);           // este no esta en la base

        RepositorioHibernateException e = assertThrows(RepositorioHibernateException.class, () -> ventas.guardar(venta));

        assertTrue(e.getMessage().contains("X9"));
        assertEquals(0, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(0, consultarEntero("SELECT count(*) FROM detalle_venta"));
        assertEquals(20, existenciaEnBd("P1"));
    }

    @Test
    void guardarUnaVentaNulaSeRechaza() {
        assertThrows(IllegalArgumentException.class, () -> ventas.guardar(null));
    }
}
