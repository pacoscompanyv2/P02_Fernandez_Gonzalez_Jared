package adapter.in.swing;

import domain.Venta;

import javax.swing.*;
import java.awt.*;

// vista de consulta (C02): captura un folio y muestra estado, partidas y total de una venta YA guardada.
// no conoce el repositorio ni la base: solo habla con el controlador.
public class ConsultaVentaPanel extends JPanel {
    private final transient VentasController controller;
    private final PartidasTableModel modelo = new PartidasTableModel();
    private final JTextField campoFolio = new JTextField(12);
    private final JLabel etiquetaEstado = new JLabel(" ");
    private final JLabel etiquetaTotal = new JLabel(" ");

    public ConsultaVentaPanel(VentasController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Consultar venta"));

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel etiquetaFolio = new JLabel("Folio:");
        etiquetaFolio.setLabelFor(campoFolio);
        superior.add(etiquetaFolio);
        superior.add(campoFolio);
        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.setMnemonic('B');
        botonBuscar.addActionListener(e -> buscar());
        campoFolio.addActionListener(e -> buscar());
        superior.add(botonBuscar);
        add(superior, BorderLayout.NORTH);

        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        inferior.add(etiquetaEstado);
        inferior.add(etiquetaTotal);
        add(inferior, BorderLayout.SOUTH);
    }

    private void buscar() {
        try {
            Venta venta = controller.consultarVenta(campoFolio.getText());
            modelo.actualizar(venta.getPartidas());
            etiquetaEstado.setText("Estado: " + (venta.isCerrada() ? "CERRADA" : "ABIERTA"));
            etiquetaTotal.setText("Total: $" + String.format("%.2f", venta.calcularTotal().valor()));
        } catch (IllegalArgumentException ex) {
            modelo.actualizar(null);
            etiquetaEstado.setText(" ");
            etiquetaTotal.setText(" ");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}
