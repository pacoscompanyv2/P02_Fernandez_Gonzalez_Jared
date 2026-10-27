package adapter.out.hibernate;

import adapter.out.jdbc.ConfiguracionJdbc;
import adapter.out.jdbc.PostgresTestBase;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

// base de las pruebas de integracion del adaptador Hibernate. Reusa PostgresTestBase (TRUNCATE solo en la base *_test,
// siembra por SQL directo) y le agrega una SessionFactory apuntando a esa misma base de pruebas.
abstract class HibernateTestBase extends PostgresTestBase {

    protected SessionFactory sessionFactory;

    @BeforeEach
    void abrirSessionFactory() {
        // el @BeforeEach de la superclase ya corrio: 'conexion' apunta a la base *_test
        sessionFactory = SesionesHibernate.crear(conexion.getUrl(),
            System.getenv(ConfiguracionJdbc.VAR_USUARIO), System.getenv(ConfiguracionJdbc.VAR_CLAVE));
    }

    @AfterEach
    void cerrarSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
