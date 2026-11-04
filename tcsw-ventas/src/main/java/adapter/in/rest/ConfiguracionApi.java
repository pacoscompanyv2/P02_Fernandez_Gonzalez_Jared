package adapter.in.rest;

import adapter.out.hibernate.RepositoriosHibernateFactory;
import adapter.out.jdbc.RepositoriosFactory;
import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.facade.VentasFacade;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// raiz de composicion de la API (igual criterio que Main de Swing):
//   tcsw.almacenamiento=memoria        -> memoria (lo usan las pruebas; nunca toca PostgreSQL)
//   auto (por defecto)                 -> TCSW_PERSISTENCIA=hibernate -> Hibernate; si hay TCSW_DB_* -> JDBC; si no, memoria
@Configuration
public class ConfiguracionApi {

    private static final Logger LOG = LoggerFactory.getLogger(ConfiguracionApi.class);

    @Bean
    public VentasFacade ventasFacade(@Value("${tcsw.almacenamiento:auto}") String modo) {
        ProductoRepository productos;
        VentaRepository ventas;
        String almacenamiento;
        if ("memoria".equalsIgnoreCase(modo)) {
            productos = new ProductoRepositoryEnMemoria();
            ventas = new VentaRepositoryEnMemoria();
            almacenamiento = "memoria";
        } else if (RepositoriosHibernateFactory.seleccionado()) {
            productos = RepositoriosHibernateFactory.productos();
            ventas = RepositoriosHibernateFactory.ventas();
            almacenamiento = "PostgreSQL (Hibernate)";
        } else {
            productos = RepositoriosFactory.productos();
            ventas = RepositoriosFactory.ventas();
            almacenamiento = RepositoriosFactory.usaPostgres() ? "PostgreSQL (JDBC)" : "memoria";
        }
        LOG.info("Almacenamiento: {}", almacenamiento);
        VentasFacade facade = new VentasFacade(productos, ventas);
        // solo se siembra el catalogo si esta vacio (con PostgreSQL no se reinician las existencias en cada arranque)
        if (productos.listarTodos().isEmpty()) {
            facade.registrarProducto(new Producto("P1", "Playera Deportiva", 150.0, 20));
            facade.registrarProducto(new Producto("P2", "Pantalon Mezclilla", 350.0, 15));
            facade.registrarProducto(new Producto("P3", "Gorra Urbana", 99.0, 30));
        }
        return facade;
    }
}
