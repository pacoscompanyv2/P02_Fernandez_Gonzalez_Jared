package adapter.in.swing;

import domain.DetalleVenta;
import domain.Producto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PartidasTableModelTest {

    @Test
    void muestraCodigoNombreCantidadPrecioYSubtotal() {
        PartidasTableModel modelo = new PartidasTableModel();
        modelo.actualizar(List.of(new DetalleVenta(new Producto("P1", "Playera", 150.0, 20), 2)));

        assertEquals(1, modelo.getRowCount());
        assertEquals(5, modelo.getColumnCount());
        assertEquals("P1", modelo.getValueAt(0, 0));
        assertEquals("Playera", modelo.getValueAt(0, 1));
        assertEquals(2, modelo.getValueAt(0, 2));
        assertEquals(150.0, (double) modelo.getValueAt(0, 3), 0.001);
        assertEquals(300.0, (double) modelo.getValueAt(0, 4), 0.001);
    }

    @Test
    void actualizarConNuloDejaLaTablaVaciaYAvisa() {
        PartidasTableModel modelo = new PartidasTableModel();
        int[] avisos = {0};
        modelo.addTableModelListener(e -> avisos[0]++);

        modelo.actualizar(null);

        assertEquals(0, modelo.getRowCount());
        assertEquals(1, avisos[0]);
    }
}
