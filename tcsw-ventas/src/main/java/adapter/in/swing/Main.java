package adapter.in.swing;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.facade.VentasFacade;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.Producto;

public class Main {
    public static void main(String[] args) {
        ProductoRepository productosRepo = new ProductoRepositoryEnMemoria();
        VentaRepository ventasRepo = new VentaRepositoryEnMemoria();

        VentasFacade facade = new VentasFacade(productosRepo, ventasRepo);
        facade.registrarProducto(new Producto("P1", "Playera Deportiva", 150.0, 20));
        facade.registrarProducto(new Producto("P2", "Pantalon Mezclilla", 350.0, 15));
        facade.registrarProducto(new Producto("P3", "Gorra Urbana", 99.0, 30));

        VentasController controller = new VentasController(facade);
        
        System.out.println("Cliente Swing inicializado con " + controller.listarProductos().size() + " productos.");
    }
}