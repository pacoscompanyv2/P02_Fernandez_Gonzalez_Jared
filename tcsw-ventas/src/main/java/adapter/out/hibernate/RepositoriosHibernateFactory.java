package adapter.out.hibernate;

import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import org.hibernate.SessionFactory;

import java.util.Map;

// raiz de composicion del adaptador Hibernate. Se elige con la variable TCSW_PERSISTENCIA=hibernate
// (si no esta, Main sigue usando JDBC o memoria como en P08). La SessionFactory es cara de crear: se crea UNA vez.
public final class RepositoriosHibernateFactory {

    public static final String VAR_PERSISTENCIA = "TCSW_PERSISTENCIA";

    private static SessionFactory fabrica;

    private RepositoriosHibernateFactory() {
    }

    public static boolean seleccionado(Map<String, String> entorno) {
        String valor = entorno.get(VAR_PERSISTENCIA);
        return valor != null && valor.trim().equalsIgnoreCase("hibernate");
    }

    public static boolean seleccionado() {
        return seleccionado(System.getenv());
    }

    public static ProductoRepository productos() {
        return new ProductoRepositoryHibernate(sessionFactory());
    }

    public static VentaRepository ventas() {
        return new VentaRepositoryHibernate(sessionFactory());
    }

    // package-private: ni Main ni nada fuera del adaptador debe ver tipos de Hibernate
    static synchronized SessionFactory sessionFactory() {
        if (fabrica == null) {
            fabrica = SesionesHibernate.desdeEntorno();
        }
        return fabrica;
    }
}
