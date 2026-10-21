package adapter.out.jdbc;

import domain.Producto;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// pruebas SIN base de datos: validaciones del adaptador y traduccion de errores SQL.
// la conexion falsa siempre falla, como si PostgreSQL estuviera apagado.
class ProductoRepositoryJdbcValidacionTest {

    private static final ConexionJdbc CONEXION_CAIDA = () -> {
        throw new SQLException("servidor no disponible", "08001");
    };

    @Test
    void noAceptaConexionNula() {
        assertThrows(IllegalArgumentException.class, () -> new ProductoRepositoryJdbc(null));
    }

    @Test
    void noGuardaUnProductoNulo() {
        ProductoRepositoryJdbc repo = new ProductoRepositoryJdbc(CONEXION_CAIDA);

        assertThrows(IllegalArgumentException.class, () -> repo.guardar(null));
    }

    @Test
    void siLaBaseFallaAlGuardarLanzaRepositorioExceptionConSqlState() {
        ProductoRepositoryJdbc repo = new ProductoRepositoryJdbc(CONEXION_CAIDA);

        RepositorioException e = assertThrows(RepositorioException.class,
            () -> repo.guardar(new Producto("P1", "Playera", 150.0, 20)));

        assertEquals("08001", e.getSqlState());
        assertTrue(e.getMessage().contains("P1"));
    }

    @Test
    void siLaBaseFallaAlBuscarOListarLanzaRepositorioException() {
        ProductoRepositoryJdbc repo = new ProductoRepositoryJdbc(CONEXION_CAIDA);

        assertThrows(RepositorioException.class, () -> repo.buscarPorCodigo("P1"));
        assertThrows(RepositorioException.class, repo::listarTodos);
    }
}
