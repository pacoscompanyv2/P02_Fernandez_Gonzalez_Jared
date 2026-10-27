package adapter.out.hibernate;

import jakarta.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

// patron "una sesion por operacion": abre sesion, abre transaccion, ejecuta, commit; ante cualquier error hace rollback.
// asi cada metodo de repositorio es atomico (consistencia transaccional) y no queda ninguna sesion abierta.
final class EjecutorTransaccional {

    private EjecutorTransaccional() {
    }

    static <T> T ejecutar(SessionFactory fabrica, String mensajeDeError, Function<Session, T> trabajo) {
        Session sesion = fabrica.openSession();
        Transaction tx = null;
        try {
            tx = sesion.beginTransaction();
            T resultado = trabajo.apply(sesion);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            revertir(tx, e);
            if (e instanceof RepositorioHibernateException) {
                throw e;
            }
            if (e instanceof PersistenceException || e instanceof HibernateException) {
                throw new RepositorioHibernateException(mensajeDeError, e);
            }
            throw e;
        } finally {
            sesion.close();
        }
    }

    // lectura: sesion sin transaccion de escritura
    static <T> T leer(SessionFactory fabrica, String mensajeDeError, Function<Session, T> consulta) {
        try (Session sesion = fabrica.openSession()) {
            return consulta.apply(sesion);
        } catch (RuntimeException e) {
            if (e instanceof PersistenceException || e instanceof HibernateException) {
                throw new RepositorioHibernateException(mensajeDeError, e);
            }
            throw e;
        }
    }

    // si el rollback mismo falla no se pierde el error original: queda como "suppressed"
    private static void revertir(Transaction tx, RuntimeException causa) {
        if (tx == null) {
            return;
        }
        try {
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (RuntimeException ex) {
            causa.addSuppressed(ex);
        }
    }
}
