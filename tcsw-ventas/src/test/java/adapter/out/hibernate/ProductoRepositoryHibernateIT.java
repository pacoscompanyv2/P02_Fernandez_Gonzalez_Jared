package adapter.out.hibernate;

import domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// integracion de ProductoRepositoryHibernate contra PostgreSQL REAL (base *_test).  mvn verify -Pintegracion
// mismos casos que ProductoRepositoryJdbcIT: ambos adaptadores cumplen el mismo contrato del puerto.
class ProductoRepositoryHibernateIT extends HibernateTestBase {

    private ProductoRepositoryHibernate productos;

    @BeforeEach
    void crearRepositorio() {
        productos = new ProductoRepositoryHibernate(sessionFactory);
    }

    @Test
    void guardarYRecuperarUnProducto() throws SQLException {
        productos.guardar(new Producto("P1", "Playera Deportiva", 150.0, 20));

        Optional<Producto> leido = productos.buscarPorCodigo("P1");

        assertTrue(leido.isPresent());
        assertEquals("Playera Deportiva", leido.get().getNombre());
        assertEquals(150.0, leido.get().getPrecio(), 0.001);
        assertEquals(20, leido.get().getExistencia());
        assertEquals(20, existenciaEnBd("P1"));
    }

    @Test
    void guardarDosVecesElMismoCodigoActualizaSinDuplicar() throws SQLException {
        productos.guardar(new Producto("P1", "Playera", 100.0, 10));
        productos.guardar(new Producto("P1", "Playera Nueva", 120.0, 7));

        assertEquals(1, consultarEntero("SELECT count(*) FROM producto"));
        Producto leido = productos.buscarPorCodigo("P1").orElseThrow(AssertionError::new);
        assertEquals("Playera Nueva", leido.getNombre());
        assertEquals(7, leido.getExistencia());
    }

    @Test
    void listarTodosRegresaOrdenadoPorCodigo() {
        productos.guardar(new Producto("P3", "Gorra", 99.0, 30));
        productos.guardar(new Producto("P1", "Playera", 150.0, 20));
        productos.guardar(new Producto("P2", "Pantalon", 350.0, 15));

        List<Producto> lista = productos.listarTodos();

        assertEquals(3, lista.size());
        assertEquals("P1", lista.get(0).getCodigo());
        assertEquals("P3", lista.get(2).getCodigo());
    }

    @Test
    void losDatosSobrevivenAUnaSessionFactoryNueva() {
        productos.guardar(new Producto("P1", "Playera", 150.0, 20));
        sessionFactory.close();

        // equivale a cerrar y volver a abrir la aplicacion
        abrirSessionFactory();
        ProductoRepositoryHibernate otroRepositorio = new ProductoRepositoryHibernate(sessionFactory);

        assertTrue(otroRepositorio.buscarPorCodigo("P1").isPresent());
    }

    @Test
    void buscarUnCodigoInexistenteRegresaVacio() {
        assertFalse(productos.buscarPorCodigo("NO-EXISTE").isPresent());
    }

    @Test
    void guardarNuloSeRechaza() {
        assertThrows(IllegalArgumentException.class, () -> productos.guardar(null));
    }

    @Test
    void unCodigoMasLargoQueLaColumnaSeRechazaConSqlState22001() throws SQLException {
        Producto largo = new Producto("CODIGO-MUY-LARGO-123456789", "Largo", 10.0, 1);

        RepositorioHibernateException e = assertThrows(RepositorioHibernateException.class, () -> productos.guardar(largo));

        assertEquals("22001", e.getSqlState());
        assertEquals(0, consultarEntero("SELECT count(*) FROM producto"));
    }

    @Test
    void unNombreConSqlInyectadoSeGuardaComoTextoLiteral() throws SQLException {
        String malicioso = "x'); DROP TABLE producto;--";
        productos.guardar(new Producto("P9", malicioso, 10.0, 1));

        assertEquals(malicioso, productos.buscarPorCodigo("P9").orElseThrow(AssertionError::new).getNombre());
        assertEquals(1, consultarEntero("SELECT count(*) FROM producto"));
    }

    @Test
    void unCodigoDeBusquedaMaliciosoNoRegresaFilas() {
        productos.guardar(new Producto("P1", "Playera", 150.0, 20));

        assertFalse(productos.buscarPorCodigo("' OR '1'='1").isPresent());
    }
}
