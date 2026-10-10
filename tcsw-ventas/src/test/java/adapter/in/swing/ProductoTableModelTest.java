package adapter.in.swing;

import domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTableModelTest {

    private ProductoTableModel modelo;

    @BeforeEach
    void setUp() {
        modelo = new ProductoTableModel(List.of(
            new Producto("P1", "Playera", 150.0, 20),
            new Producto("P2", "Gorra", 99.0, 30)));
    }

    @Test
    @DisplayName("Exito: El modelo expone filas, columnas y valores de los productos")
    void muestraFilasColumnasYValores() {
        assertEquals(2, modelo.getRowCount());
        assertEquals(4, modelo.getColumnCount());
        assertEquals("Codigo", modelo.getColumnName(0));
        assertEquals("P1", modelo.getValueAt(0, 0));
        assertEquals("Playera", modelo.getValueAt(0, 1));
        assertEquals(Double.valueOf(150.0), modelo.getValueAt(0, 2));
        assertEquals(Integer.valueOf(20), modelo.getValueAt(0, 3));
    }

    @Test
    @DisplayName("Exito: Actualizar reemplaza los datos y avisa a la tabla")
    void actualizarReemplazaDatosYNotifica() {
        int[] avisos = {0};
        modelo.addTableModelListener(e -> avisos[0]++);

        modelo.actualizar(List.of(new Producto("P3", "Calcetas", 40.0, 5)));

        assertEquals(1, modelo.getRowCount());
        assertEquals("P3", modelo.getValueAt(0, 0));
        assertEquals(1, avisos[0]);
    }

    @Test
    @DisplayName("Falla: Una lista nula deja la tabla vacia sin lanzar excepcion")
    void listaNulaDejaTablaVacia() {
        assertEquals(0, new ProductoTableModel(null).getRowCount());
        modelo.actualizar(null);
        assertEquals(0, modelo.getRowCount());
    }

    @Test
    @DisplayName("Falla: Pedir una fila fuera de rango lanza excepcion")
    void filaFueraDeRangoFalla() {
        Exception ex = assertThrows(IndexOutOfBoundsException.class, () -> modelo.getProductoEn(5));
        assertTrue(ex.getMessage().contains("Fila fuera de rango"));
        assertThrows(IndexOutOfBoundsException.class, () -> modelo.getProductoEn(-1));
    }
}
