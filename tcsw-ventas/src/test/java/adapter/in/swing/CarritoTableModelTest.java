package adapter.in.swing;

import application.port.in.ItemVenta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarritoTableModelTest {

    @Test
    @DisplayName("Exito: El modelo expone filas, columnas y valores del carrito")
    void muestraFilasColumnasYValores() {
        CarritoTableModel modelo = new CarritoTableModel(List.of(new ItemVenta("P1", 2), new ItemVenta("P2", 5)));

        assertEquals(2, modelo.getRowCount());
        assertEquals(2, modelo.getColumnCount());
        assertEquals("Producto", modelo.getColumnName(0));
        assertEquals("Cantidad", modelo.getColumnName(1));
        assertEquals("P1", modelo.getValueAt(0, 0));
        assertEquals(Integer.valueOf(5), modelo.getValueAt(1, 1));
    }

    @Test
    @DisplayName("Exito: Actualizar reemplaza los datos y avisa a la tabla")
    void actualizarReemplazaDatosYNotifica() {
        CarritoTableModel modelo = new CarritoTableModel(List.of(new ItemVenta("P1", 2)));
        int[] avisos = {0};
        modelo.addTableModelListener(e -> avisos[0]++);

        modelo.actualizar(List.of());

        assertEquals(0, modelo.getRowCount());
        assertEquals(1, avisos[0]);
    }

    @Test
    @DisplayName("Falla: Una lista nula deja el carrito vacio sin lanzar excepcion")
    void listaNulaDejaCarritoVacio() {
        assertEquals(0, new CarritoTableModel(null).getRowCount());
        CarritoTableModel modelo = new CarritoTableModel(List.of(new ItemVenta("P1", 1)));
        modelo.actualizar(null);
        assertEquals(0, modelo.getRowCount());
    }
}
