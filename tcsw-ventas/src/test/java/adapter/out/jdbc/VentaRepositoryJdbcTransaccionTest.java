package adapter.out.jdbc;

import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// pruebas SIN base de datos del control de la transaccion: con una conexion falsa se comprueba
// QUE se llama (setAutoCommit(false), commit, rollback, close) y en que orden, sin necesidad de PostgreSQL.
class VentaRepositoryJdbcTransaccionTest {

    // registro de lo que el repositorio le pidio a la "conexion"
    private final List<String> llamadas = new ArrayList<>();
    private int filasAfectadasUpdate = 1;      // lo que devuelve el UPDATE de existencias
    private boolean fallarElBatchDeDetalles = false;

    private ConexionJdbc conexionFalsa() {
        return () -> (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(), new Class<?>[] {Connection.class}, manejadorConexion());
    }

    private InvocationHandler manejadorConexion() {
        return (proxy, metodo, args) -> {
            switch (metodo.getName()) {
                case "setAutoCommit":
                    llamadas.add("setAutoCommit(" + args[0] + ")");
                    return null;
                case "commit":
                case "rollback":
                case "close":
                    llamadas.add(metodo.getName());
                    return null;
                case "prepareStatement":
                    String sql = (String) args[0];
                    llamadas.add("prepare:" + sql.split(" ")[0] + " " + sql.split(" ")[1] + " " + sql.split(" ")[2]);
                    return statementFalso(sql);
                default:
                    throw new UnsupportedOperationException(metodo.getName());
            }
        };
    }

    private PreparedStatement statementFalso(String sql) {
        return (PreparedStatement) Proxy.newProxyInstance(
            PreparedStatement.class.getClassLoader(), new Class<?>[] {PreparedStatement.class},
            (proxy, metodo, args) -> {
                switch (metodo.getName()) {
                    case "executeUpdate":
                        return sql.startsWith("UPDATE") ? filasAfectadasUpdate : 1;
                    case "executeBatch":
                        if (fallarElBatchDeDetalles) {
                            throw new SQLException("falla simulada en las partidas", "23503");
                        }
                        return new int[] {1};
                    case "close":
                    case "addBatch":
                    case "setString":
                    case "setInt":
                    case "setBoolean":
                    case "setBigDecimal":
                        return null;
                    default:
                        throw new UnsupportedOperationException(metodo.getName());
                }
            });
    }

    private static Venta ventaConUnProducto() {
        Venta venta = new Venta("F-T1");
        venta.agregarPartida(new Producto("P1", "Playera Deportiva", 150.0, 20), 2);
        return venta;
    }

    @Test
    void siTodoSaleBienHaceCommitYNoRollback() {
        new VentaRepositoryJdbc(conexionFalsa()).guardar(ventaConUnProducto());

        assertEquals("setAutoCommit(false)", llamadas.get(0));
        assertTrue(llamadas.contains("commit"));
        assertFalse(llamadas.contains("rollback"));
        assertEquals("close", llamadas.get(llamadas.size() - 1));
    }

    @Test
    void siFallanLasPartidasHaceRollbackYNuncaCommit() {
        fallarElBatchDeDetalles = true;
        VentaRepositoryJdbc repo = new VentaRepositoryJdbc(conexionFalsa());
        Venta venta = ventaConUnProducto();

        RepositorioException e = assertThrows(RepositorioException.class, () -> repo.guardar(venta));

        assertEquals("23503", e.getSqlState());
        assertTrue(llamadas.contains("rollback"));
        assertFalse(llamadas.contains("commit"));
        assertEquals("close", llamadas.get(llamadas.size() - 1));
    }

    @Test
    void siElProductoNoExisteEnLaBaseHaceRollbackYNoIntentaGuardarPartidas() {
        filasAfectadasUpdate = 0;
        VentaRepositoryJdbc repo = new VentaRepositoryJdbc(conexionFalsa());
        Venta venta = ventaConUnProducto();

        RepositorioException e = assertThrows(RepositorioException.class, () -> repo.guardar(venta));

        assertTrue(e.getMessage().contains("P1"));
        assertTrue(llamadas.contains("rollback"));
        assertFalse(llamadas.contains("commit"));
        assertFalse(llamadas.stream().anyMatch(l -> l.contains("detalle_venta")));
    }

    @Test
    void noAceptaConexionNulaNiVentaNula() {
        assertThrows(IllegalArgumentException.class, () -> new VentaRepositoryJdbc(null));
        VentaRepositoryJdbc repo = new VentaRepositoryJdbc(conexionFalsa());
        assertThrows(IllegalArgumentException.class, () -> repo.guardar(null));
    }

    @Test
    void siLaBaseNoEstaDisponibleLanzaRepositorioException() {
        ConexionJdbc caida = () -> {
            throw new SQLException("servidor no disponible", "08001");
        };
        VentaRepositoryJdbc repo = new VentaRepositoryJdbc(caida);
        Venta venta = ventaConUnProducto();

        RepositorioException guardando = assertThrows(RepositorioException.class, () -> repo.guardar(venta));
        assertThrows(RepositorioException.class, () -> repo.buscarPorFolio("F-T1"));

        assertEquals("08001", guardando.getSqlState());
    }
}
