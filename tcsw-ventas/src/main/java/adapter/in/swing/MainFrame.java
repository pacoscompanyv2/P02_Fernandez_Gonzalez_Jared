package adapter.in.swing;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(VentasController controller) {
        super("tcsw-ventas - Cliente Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);

        CarritoPanel carritoPanel = new CarritoPanel(controller);
        CatalogoPanel catalogoPanel = new CatalogoPanel(controller, carritoPanel::actualizar);
        // despues de una venta el catalogo vuelve a leer las existencias
        carritoPanel.setAlRegistrarVenta(catalogoPanel::actualizar);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, catalogoPanel, carritoPanel);
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);
    }
}
