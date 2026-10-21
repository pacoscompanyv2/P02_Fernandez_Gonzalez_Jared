package adapter.out.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// base de las pruebas de INTEGRACION (clases *IT): se ejecutan con  mvn verify -Pintegracion
// y necesitan PostgreSQL arriba (docker compose up -d --wait) y las variables de .env exportadas.
// trabajan SOLO contra la base *_test: el TRUNCATE nunca toca los datos de la aplicacion.
@Tag("integracion")
abstract class PostgresTestBase {

    protected ConfiguracionJdbc conexion;

    @BeforeEach
    void prepararBaseDePruebas() throws SQLException {
        ConfiguracionJdbc base = ConfiguracionJdbc.desdeEntorno();
        String urlPruebas = System.getenv("TCSW_TEST_DB_URL");
        if (urlPruebas == null || urlPruebas.isBlank()) {
            throw new IllegalStateException("Falta TCSW_TEST_DB_URL. Exporta las variables de .env (set -a; . ./.env; set +a).");
        }
        if (!urlPruebas.endsWith("_test")) {
            // protege la base real: jamas se trunca una base que no termine en _test
            throw new IllegalStateException("TCSW_TEST_DB_URL debe apuntar a una base que termine en _test");
        }
        conexion = base.conUrl(urlPruebas);
        ejecutar("TRUNCATE TABLE detalle_venta, venta, producto");
    }

    // INSERT/UPDATE/DDL con parametros
    protected void ejecutar(String sql, Object... parametros) throws SQLException {
        try (Connection c = conexion.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, parametros);
            ps.executeUpdate();
        }
    }

    // consulta que regresa un solo numero (conteos, existencias)
    protected int consultarEntero(String sql, Object... parametros) throws SQLException {
        try (Connection c = conexion.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // consulta que regresa un solo texto
    protected String consultarTexto(String sql, Object... parametros) throws SQLException {
        try (Connection c = conexion.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getString(1);
            }
        }
    }

    // siembra un producto directo con SQL (no depende del adaptador que se esta probando)
    protected void sembrarProducto(String codigo, String nombre, double precio, int existencia) throws SQLException {
        ejecutar("INSERT INTO producto (codigo, nombre, precio, existencia) VALUES (?, ?, ?, ?)",
            codigo, nombre, java.math.BigDecimal.valueOf(precio), existencia);
    }

    protected int existenciaEnBd(String codigo) throws SQLException {
        return consultarEntero("SELECT existencia FROM producto WHERE codigo = ?", codigo);
    }

    private static void asignar(PreparedStatement ps, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
    }
}
