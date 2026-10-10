package adapter.in.swing;

import javax.swing.*;
import java.awt.*;

public class CatalogoPanel extends JPanel {
    private final VentasController controller;
    private final ProductoTableModel modelo;
    private final JTable tabla;
    private final JSpinner spinnerCantidad;
    private final Runnable onProductoAgregado;

    public CatalogoPanel(VentasController controller, Runnable onProductoAgregado) {
        this.controller = controller;
        this.onProductoAgregado = onProductoAgregado;
        this.modelo = new ProductoTableModel(controller.listarProductos());
        this.tabla = new JTable(modelo);
        this.tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Catalogo de Productos"));
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel etiquetaCantidad = new JLabel("Cantidad:");
        etiquetaCantidad.setLabelFor(spinnerCantidad);
        panelInferior.add(etiquetaCantidad);
        panelInferior.add(spinnerCantidad);

        JButton botonAgregar = new JButton("Agregar al carrito");
        botonAgregar.setMnemonic('A');
        botonAgregar.addActionListener(e -> agregarSeleccionado());
        panelInferior.add(botonAgregar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    // vuelve a leer el catalogo (por ejemplo, para ver la existencia despues de una venta)
    public void actualizar() {
        modelo.actualizar(controller.listarProductos());
    }

    private void agregarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un producto de la lista.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = modelo.getProductoEn(fila).getCodigo();
        int cantidad = (int) spinnerCantidad.getValue();

        try {
            controller.agregarAlCarrito(codigo, cantidad);
            if (onProductoAgregado != null) {
                onProductoAgregado.run();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "No se pudo agregar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
