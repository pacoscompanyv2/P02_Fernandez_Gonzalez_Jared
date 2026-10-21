package adapter.out.jdbc;

import java.sql.SQLException;

// error de persistencia: envuelve la SQLException para que application/domain no conozcan java.sql
public class RepositorioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String sqlState;

    public RepositorioException(String mensaje) {
        super(mensaje);
        this.sqlState = null;
    }

    public RepositorioException(String mensaje, SQLException causa) {
        super(mensaje, causa);
        this.sqlState = causa == null ? null : causa.getSQLState();
    }

    // codigo SQLSTATE de PostgreSQL (por ejemplo 23505 = llave duplicada, 23514 = CHECK violado), o null
    public String getSqlState() {
        return sqlState;
    }
}
