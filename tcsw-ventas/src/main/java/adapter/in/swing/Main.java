package adapter.in.swing;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.facade.VentasFacade;
import domain.Producto;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        VentasFacade facade = new VentasFacade(
            new ProductoRepositoryEnMemoria(), new VentaRepositoryEnMemoria());
        facade.registrarProducto(new Producto("P1", "Playera Deportiva", 150.0, 20));
        facade.registrarProducto(new Producto("P2", "Pantalon Mezclilla", 350.0, 15));
        facade.registrarProducto(new Producto("P3", "Gorra Urbana", 99.0, 30));

        VentasController controller = new VentasController(facade);

        SwingUtilities.invokeLater(() -> new MainFrame(controller).setVisible(true));
    }
}
