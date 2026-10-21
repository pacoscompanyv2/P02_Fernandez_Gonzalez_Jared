package adapter.out.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

// de donde sacan los repositorios JDBC sus conexiones.
// cada operacion abre la suya y la cierra con try-with-resources (no hay conexiones compartidas).
@FunctionalInterface
public interface ConexionJdbc {
    Connection abrir() throws SQLException;
}
