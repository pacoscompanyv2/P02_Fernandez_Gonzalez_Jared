package adapter.out.jdbc;

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

// integracion de ProductoRepositoryJdbc contra PostgreSQL REAL (base *_test).
// se corre con:  mvn verify -Pintegracion
class ProductoRepositoryJdbcIT extends PostgresTestBase {

    private ProductoRepositoryJdbc repo;

    @BeforeEach
    void crearRepositorio() {
        // se ejecuta despues del @BeforeEach de la clase base, que ya dejo las tablas vacias
        repo = new ProductoRepositoryJdbc(conexion);
    }

    // ---------- casos positivos ----------

    @Test
    void guardaYRecuperaUnProducto() {
        repo.guardar(new Producto("P1", "Playera Deportiva", 150.0, 20));

        Optional<Producto> recuperado = repo.buscarPorCodigo("P1");

        assertTrue(recuperado.isPresent());
        assertEquals("Playera Deportiva", recuperado.get().getNombre());
        assertEquals(150.0, recuperado.get().getPrecio(), 0.001);
        assertEquals(20, recuperado.get().getExistencia());
    }

    @Test
    void guardarDosVecesElMismoCodigoActualizaSinDuplicar() throws SQLException {
        repo.guardar(new Producto("P1", "Playera Deportiva", 150.0, 20));
        repo.guardar(new Producto("P1", "Playera Deportiva v2", 160.5, 18));

        Producto recuperado = repo.buscarPorCodigo("P1").orElseThrow(AssertionError::new);

        assertEquals("Playera Deportiva v2", recuperado.getNombre());
        assertEquals(160.5, recuperado.getPrecio(), 0.001);
        assertEquals(18, recuperado.getExistencia());
        assertEquals(1, consultarEntero("SELECT count(*) FROM producto"));
    }

    @Test
    void listarTodosRegresaLosProductosOrdenadosPorCodigo() {
        repo.guardar(new Producto("P3", "Gorra Urbana", 99.0, 30));
        repo.guardar(new Producto("P1", "Playera Deportiva", 150.0, 20));
        repo.guardar(new Producto("P2", "Pantalon Mezclilla", 350.0, 15));

        List<Producto> todos = repo.listarTodos();

        assertEquals(3, todos.size());
        assertEquals("P1", todos.get(0).getCodigo());
        assertEquals("P2", todos.get(1).getCodigo());
        assertEquals("P3", todos.get(2).getCodigo());
    }

    @Test
    void listarTodosEnBaseVaciaRegresaListaVacia() {
        assertTrue(repo.listarTodos().isEmpty());
    }

    @Test
    void losDatosSobrevivenAUnRepositorioNuevo() {
        repo.guardar(new Producto("P2", "Pantalon Mezclilla", 350.0, 15));

        // otra instancia = como si la aplicacion se hubiera cerrado y vuelto a abrir
        ProductoRepositoryJdbc otraInstancia = new ProductoRepositoryJdbc(conexion);

        assertEquals(15, otraInstancia.buscarPorCodigo("P2").orElseThrow(AssertionError::new).getExistencia());
    }

    // ---------- casos negativos / limite ----------

    @Test
    void buscarUnCodigoQueNoExisteRegresaVacio() {
        assertFalse(repo.buscarPorCodigo("NO-EXISTE").isPresent());
    }

    @Test
    void guardarUnProductoNuloSeRechazaAntesDeTocarLaBase() {
        assertThrows(IllegalArgumentException.class, () -> repo.guardar(null));
    }

    @Test
    void laBaseRechazaUnCodigoMasLargoQueLaColumna() {
        // el dominio acepta codigos largos, la columna es VARCHAR(20): el error llega como RepositorioException
        Producto codigoLargo = new Producto("CODIGO-DEMASIADO-LARGO-123", "Largo", 10.0, 1);

        RepositorioException e = assertThrows(RepositorioException.class, () -> repo.guardar(codigoLargo));

        // 22001 = string_data_right_truncation
        assertEquals("22001", e.getSqlState());
    }

    @Test
    void laBaseRechazaExistenciaNegativaAunSiSeSaltanElDominio() {
        // inserta directo con SQL (sin pasar por Producto) para probar que la integridad vive tambien en la base
        SQLException e = assertThrows(SQLException.class,
            () -> ejecutar("INSERT INTO producto (codigo, nombre, precio, existencia) VALUES (?, ?, ?, ?)",
                "N1", "Negativo", java.math.BigDecimal.TEN, -5));

        assertEquals("23514", e.getSQLState());
    }

    // ---------- PreparedStatement: los datos nunca se interpretan como SQL ----------

    @Test
    void unNombreConSqlInyectadoSeGuardaComoTextoLiteral() throws SQLException {
        String malicioso = "x'); DROP TABLE producto;--";

        repo.guardar(new Producto("I1", malicioso, 1.0, 1));

        // la tabla sigue existiendo y el texto quedo guardado tal cual
        assertEquals(malicioso, consultarTexto("SELECT nombre FROM producto WHERE codigo = 'I1'"));
        assertEquals(1, consultarEntero("SELECT count(*) FROM producto"));
    }

    @Test
    void unCodigoDeBusquedaMalicioso1OR1NoRegresaFilas() {
        repo.guardar(new Producto("P1", "Playera Deportiva", 150.0, 20));

        assertFalse(repo.buscarPorCodigo("' OR '1'='1").isPresent());
    }
}
