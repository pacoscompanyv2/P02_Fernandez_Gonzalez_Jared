package adapter.out.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// datos de conexion tomados del ENTORNO (variables TCSW_DB_*). Nada de credenciales en el codigo ni en el repo.
public final class ConfiguracionJdbc implements ConexionJdbc {

    public static final String VAR_URL = "TCSW_DB_URL";
    public static final String VAR_USUARIO = "TCSW_DB_USER";
    public static final String VAR_CLAVE = "TCSW_DB_PASSWORD";

    private final String url;
    private final String usuario;
    private final String clave;

    private ConfiguracionJdbc(String url, String usuario, String clave) {
        this.url = url;
        this.usuario = usuario;
        this.clave = clave;
    }

    // se separa de desdeEntorno() para poder probarlo con un Map sin tocar el entorno real
    public static ConfiguracionJdbc desde(Map<String, String> entorno) {
        List<String> faltantes = faltantes(entorno);
        if (!faltantes.isEmpty()) {
            // solo se mencionan los NOMBRES de las variables, nunca sus valores
            throw new IllegalStateException("Faltan variables de entorno de base de datos: "
                + String.join(", ", faltantes)
                + ". Copia .env.example a .env y exportalas (ver README-reproduccion.md).");
        }
        return new ConfiguracionJdbc(entorno.get(VAR_URL), entorno.get(VAR_USUARIO), entorno.get(VAR_CLAVE));
    }

    public static ConfiguracionJdbc desdeEntorno() {
        return desde(System.getenv());
    }

    // true si las tres variables estan definidas: asi Main decide entre PostgreSQL y memoria
    public static boolean disponible() {
        return faltantes(System.getenv()).isEmpty();
    }

    // misma configuracion pero apuntando a otra base (las pruebas usan una base aparte, *_test)
    public ConfiguracionJdbc conUrl(String otraUrl) {
        if (otraUrl == null || otraUrl.isBlank()) {
            throw new IllegalArgumentException("La URL no puede estar vacia");
        }
        return new ConfiguracionJdbc(otraUrl, usuario, clave);
    }

    public String getUrl() {
        return url;
    }

    public String getUsuario() {
        return usuario;
    }

    @Override
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    private static List<String> faltantes(Map<String, String> entorno) {
        List<String> faltantes = new ArrayList<>();
        for (String nombre : new String[] {VAR_URL, VAR_USUARIO, VAR_CLAVE}) {
            String valor = entorno.get(nombre);
            if (valor == null || valor.isBlank()) {
                faltantes.add(nombre);
            }
        }
        return faltantes;
    }

    // la clave nunca se imprime
    @Override
    public String toString() {
        return "ConfiguracionJdbc{url=" + url + ", usuario=" + usuario + ", clave=********}";
    }
}
