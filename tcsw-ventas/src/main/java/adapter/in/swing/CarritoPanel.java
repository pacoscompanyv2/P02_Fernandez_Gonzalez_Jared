package adapter.in.swing;

import javax.swing.*;
import java.awt.*;

public class CarritoPanel extends JPanel {
    private final VentasController controller;
    private final CarritoTableModel modelo;
    private final JTable tabla;
    private final JTextField campoFolio;
    private final JButton botonRegistrar;
    private Runnable alRegistrarVenta;

    public CarritoPanel(VentasController controller) {
        this.controller = controller;
        this.modelo = new CarritoTableModel(controller.obtenerCarrito());
        this.tabla = new JTable(modelo);
        this.tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.campoFolio = new JTextField(10);
        this.botonRegistrar = new JButton("Registrar venta");

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel etiquetaFolio = new JLabel("Folio:");
        etiquetaFolio.setLabelFor(campoFolio);
        panelInferior.add(etiquetaFolio);
        panelInferior.add(campoFolio);

        JButton botonQuitar = new JButton("Quitar seleccion");
        botonQuitar.setMnemonic('Q');
        botonQuitar.addActionListener(e -> quitarSeleccionado());
        panelInferior.add(botonQuitar);

        botonRegistrar.setMnemonic('R');
        botonRegistrar.addActionListener(e -> registrarVenta());
        panelInferior.add(botonRegistrar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    // el MainFrame lo usa para refrescar el catalogo despues de una venta
    public void setAlRegistrarVenta(Runnable alRegistrarVenta) {
        this.alRegistrarVenta = alRegistrarVenta;
    }

    public void actualizar() {
        modelo.actualizar(controller.obtenerCarrito());
    }

    private void quitarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un elemento del carrito para quitarlo.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controller.quitarDelCarrito(fila);
            actualizar();
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarVenta() {
        String folio = campoFolio.getText().trim();
        if (folio.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Captura un folio antes de registrar la venta.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // se deshabilita mientras corre el SwingWorker para evitar doble registro
        botonRegistrar.setEnabled(false);
        new RegistrarVentaWorker(controller, folio, this).execute();
    }

    void onVentaRegistrada() {
        actualizar();
        campoFolio.setText("");
        if (alRegistrarVenta != null) {
            alRegistrarVenta.run();
        }
    }

    void onRegistroTerminado() {
        botonRegistrar.setEnabled(true);
    }
}
