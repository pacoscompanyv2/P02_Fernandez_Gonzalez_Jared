package adapter.in.swing;

import domain.Producto;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProductoTableModel extends AbstractTableModel {
    private final String[] columnas = {"Codigo", "Nombre", "Precio", "Existencia"};
    private List<Producto> productos;

    public ProductoTableModel(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
    }

    public void actualizar(List<Producto> nuevos) {
        this.productos = nuevos != null ? nuevos : new ArrayList<>();
        fireTableDataChanged();
    }

    public Producto getProductoEn(int fila) {
        if (fila < 0 || fila >= productos.size()) {
            throw new IndexOutOfBoundsException("Fila fuera de rango en la tabla de productos: " + fila);
        }
        return productos.get(fila);
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnas[col];
    }

    @Override
    public Object getValueAt(int fila, int col) {
        Producto p = productos.get(fila);
        switch (col) {
            case 0: return p.getCodigo();
            case 1: return p.getNombre();
            case 2: return p.getPrecio();
            case 3: return p.getExistencia();
            default: return null;
        }
    }
}
