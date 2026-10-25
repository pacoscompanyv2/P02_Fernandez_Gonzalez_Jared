package adapter.out.jdbc;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;

// punto unico donde se decide el almacenamiento (raiz de composicion):
// si las variables TCSW_DB_* estan definidas -> PostgreSQL; si no -> memoria (la app sigue abriendo sin Docker).
public final class RepositoriosFactory {

    private RepositoriosFactory() {
    }

    public static boolean usaPostgres() {
        return ConfiguracionJdbc.disponible();
    }

    public static ProductoRepository productos() {
        if (usaPostgres()) {
            return new ProductoRepositoryJdbc(ConfiguracionJdbc.desdeEntorno());
        }
        return new ProductoRepositoryEnMemoria();
    }

    public static VentaRepository ventas() {
        if (usaPostgres()) {
            return new VentaRepositoryJdbc(ConfiguracionJdbc.desdeEntorno());
        }
        return new VentaRepositoryEnMemoria();
    }
}
