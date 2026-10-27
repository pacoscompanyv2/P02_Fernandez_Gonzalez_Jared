package adapter.out.hibernate;

import org.hibernate.JDBCException;

// error de persistencia del adaptador Hibernate. Envuelve las excepciones de Hibernate/JPA para que
// nadie fuera del adaptador tenga que conocerlas. getSqlState() devuelve el SQLSTATE de PostgreSQL si lo hay
// (23505 = llave duplicada, 22001 = texto muy largo, 23514 = CHECK) o null.
public class RepositorioHibernateException extends RuntimeException {

    private final String sqlState;

    public RepositorioHibernateException(String mensaje) {
        super(mensaje);
        this.sqlState = null;
    }

    public RepositorioHibernateException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.sqlState = buscarSqlState(causa);
    }

    public String getSqlState() {
        return sqlState;
    }

    // Hibernate envuelve la causa real en varias capas: se recorre la cadena hasta encontrar un JDBCException
    private static String buscarSqlState(Throwable causa) {
        Throwable actual = causa;
        int profundidad = 0;
        while (actual != null && profundidad < 10) {
            if (actual instanceof JDBCException) {
                return ((JDBCException) actual).getSQLState();
            }
            actual = actual.getCause();
            profundidad++;
        }
        return null;
    }
}
