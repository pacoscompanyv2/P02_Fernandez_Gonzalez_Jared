package adapter.out.hibernate;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// unitarias (sin base de datos): la seleccion por variable y el mensaje cuando falta configuracion
class RepositoriosHibernateFactoryTest {

    @Test
    void seleccionaHibernateSoloSiLaVariableLoPide() {
        assertTrue(RepositoriosHibernateFactory.seleccionado(Map.of("TCSW_PERSISTENCIA", "hibernate")));
        assertTrue(RepositoriosHibernateFactory.seleccionado(Map.of("TCSW_PERSISTENCIA", " Hibernate ")));
        assertFalse(RepositoriosHibernateFactory.seleccionado(Map.of("TCSW_PERSISTENCIA", "jdbc")));
        assertFalse(RepositoriosHibernateFactory.seleccionado(Map.of()));
    }

    @Test
    void sinVariablesDeBaseDeDatosFallaConMensajeClaroYSinValores() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> SesionesHibernate.desde(Map.of()));
        assertTrue(e.getMessage().contains("TCSW_DB_URL"));
        assertTrue(e.getMessage().contains("TCSW_DB_PASSWORD"));
    }

    @Test
    void laClaveNuncaApareceEnElMensajeDeError() {
        Map<String, String> parcial = Map.of("TCSW_DB_PASSWORD", "secreto-que-no-debe-salir");
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> SesionesHibernate.desde(parcial));
        assertFalse(e.getMessage().contains("secreto-que-no-debe-salir"));
    }
}
