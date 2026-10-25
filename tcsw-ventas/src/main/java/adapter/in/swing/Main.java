package adapter.in.swing;

import adapter.out.jdbc.RepositoriosFactory;
import application.facade.VentasFacade;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.Producto;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // raiz de composicion: con variables TCSW_DB_* usa PostgreSQL; sin ellas, memoria (como en P07)
        ProductoRepository productos = RepositoriosFactory.productos();
        VentaRepository ventas = RepositoriosFactory.ventas();
        System.out.println("Almacenamiento: " + (RepositoriosFactory.usaPostgres() ? "PostgreSQL" : "memoria"));

        VentasFacade facade = new VentasFacade(productos, ventas);

        // solo se siembra el catalogo si esta vacio: con PostgreSQL no se deben reiniciar las existencias en cada arranque
        if (productos.listarTodos().isEmpty()) {
            facade.registrarProducto(new Producto("P1", "Playera Deportiva", 150.0, 20));
            facade.registrarProducto(new Producto("P2", "Pantalon Mezclilla", 350.0, 15));
            facade.registrarProducto(new Producto("P3", "Gorra Urbana", 99.0, 30));
        }

        VentasController controller = new VentasController(facade);

        SwingUtilities.invokeLater(() -> new MainFrame(controller).setVisible(true));
    }
}
