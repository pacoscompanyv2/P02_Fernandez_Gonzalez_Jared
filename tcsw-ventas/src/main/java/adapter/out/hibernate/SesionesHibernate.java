package adapter.out.hibernate;

import adapter.out.jdbc.ConfiguracionJdbc;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// fabrica de SessionFactory. La configuracion sale del ENTORNO (mismas variables TCSW_DB_* que el adaptador JDBC):
// nada de credenciales en codigo ni en hibernate.cfg.xml.
// hbm2ddl=validate: Hibernate NO crea ni modifica tablas; solo comprueba que db/schema.sql coincide con las entidades.
public final class SesionesHibernate {

    private SesionesHibernate() {
    }

    public static SessionFactory desdeEntorno() {
        return desde(System.getenv());
    }

    public static SessionFactory desde(Map<String, String> entorno) {
        List<String> faltantes = new ArrayList<>();
        for (String variable : new String[] {ConfiguracionJdbc.VAR_URL, ConfiguracionJdbc.VAR_USUARIO, ConfiguracionJdbc.VAR_CLAVE}) {
            String valor = entorno.get(variable);
            if (valor == null || valor.isBlank()) {
                faltantes.add(variable);
            }
        }
        if (!faltantes.isEmpty()) {
            // solo NOMBRES de variables, nunca valores
            throw new IllegalStateException("Faltan variables de entorno de base de datos: " + String.join(", ", faltantes)
                + ". Copia .env.example a .env y exportalas (ver README-reproduccion.md).");
        }
        return crear(entorno.get(ConfiguracionJdbc.VAR_URL), entorno.get(ConfiguracionJdbc.VAR_USUARIO),
            entorno.get(ConfiguracionJdbc.VAR_CLAVE));
    }

    public static SessionFactory crear(String url, String usuario, String clave) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La URL no puede estar vacia");
        }
        Map<String, Object> ajustes = new HashMap<>();
        ajustes.put("jakarta.persistence.jdbc.url", url);
        ajustes.put("jakarta.persistence.jdbc.user", usuario);
        ajustes.put("jakarta.persistence.jdbc.password", clave);
        ajustes.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        ajustes.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        ajustes.put("hibernate.hbm2ddl.auto", "validate");
        ajustes.put("hibernate.show_sql", "false");

        StandardServiceRegistry registro = new StandardServiceRegistryBuilder().applySettings(ajustes).build();
        try {
            return new MetadataSources(registro)
                .addAnnotatedClass(ProductoEntity.class)
                .addAnnotatedClass(VentaEntity.class)
                .addAnnotatedClass(DetalleVentaEntity.class)
                .buildMetadata()
                .buildSessionFactory();
        } catch (RuntimeException e) {
            StandardServiceRegistryBuilder.destroy(registro);
            throw new RepositorioHibernateException("No se pudo iniciar Hibernate (revisa la conexion y que db/schema.sql este aplicado)", e);
        }
    }
}
