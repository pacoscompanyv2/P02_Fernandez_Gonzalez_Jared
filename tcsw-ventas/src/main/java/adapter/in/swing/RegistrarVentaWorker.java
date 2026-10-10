package adapter.in.swing;

import domain.Venta;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class RegistrarVentaWorker extends SwingWorker<Venta, Void> {
    private final VentasController controller;
    private final String folio;
    private final CarritoPanel panel;

    public RegistrarVentaWorker(VentasController controller, String folio, CarritoPanel panel) {
        this.controller = controller;
        this.folio = folio;
        this.panel = panel;
    }

    // corre fuera del EDT: el registro no congela la ventana
    @Override
    protected Venta doInBackground() {
        return controller.registrarVenta(folio);
    }

    // corre de nuevo en el EDT: aqui si se puede tocar la interfaz
    @Override
    protected void done() {
        try {
            Venta venta = get();
            JOptionPane.showMessageDialog(panel,
                "Venta registrada con exito.\nFolio: " + venta.getFolio()
                    + "\nTotal: $" + String.format("%.2f", venta.calcularTotal().valor()),
                "Venta Registrada", JOptionPane.INFORMATION_MESSAGE);
            panel.onVentaRegistrada();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(panel, "Proceso interrumpido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ExecutionException ex) {
            // el error del dominio se muestra tal cual; el carrito se conserva para corregirlo
            Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
            String mensaje = causa.getMessage() != null ? causa.getMessage() : causa.getClass().getSimpleName();
            JOptionPane.showMessageDialog(panel,
                mensaje,
                "Error al registrar la venta", JOptionPane.ERROR_MESSAGE);
        } finally {
            panel.onRegistroTerminado();
        }
    }
}
