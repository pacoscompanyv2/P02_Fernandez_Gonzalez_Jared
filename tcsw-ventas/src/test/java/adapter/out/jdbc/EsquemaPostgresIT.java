package adapter.out.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// comprueba contra PostgreSQL REAL que el esquema de db/schema.sql quedo aplicado
class EsquemaPostgresIT extends PostgresTestBase {

    @Test
    void elServidorEsPostgreSql16() throws SQLException {
        String version = consultarTexto("SELECT version()");

        assertTrue(version.startsWith("PostgreSQL 16"), "version obtenida: " + version);
    }

    @Test
    void existenLasTresTablasDelEsquema() throws SQLException {
        int tablas = consultarEntero(
            "SELECT count(*) FROM information_schema.tables "
            + "WHERE table_schema = 'public' AND table_name IN ('producto', 'venta', 'detalle_venta')");

        assertEquals(3, tablas);
    }

    @Test
    void laBaseRechazaUnProductoConExistenciaNegativa() {
        SQLException e = org.junit.jupiter.api.Assertions.assertThrows(SQLException.class,
            () -> ejecutar("INSERT INTO producto (codigo, nombre, precio, existencia) VALUES ('N1', 'Negativo', 10, -1)"));

        // 23514 = check_violation
        assertEquals("23514", e.getSQLState());
    }
}
