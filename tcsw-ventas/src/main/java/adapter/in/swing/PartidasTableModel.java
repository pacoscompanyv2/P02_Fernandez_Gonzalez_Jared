package adapter.in.swing;

import domain.DetalleVenta;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

// modelo de tabla (la "M" visible del MVC) para las partidas de una venta consultada
public class PartidasTableModel extends AbstractTableModel {
    private final String[] columnas = {"Codigo", "Producto", "Cantidad", "Precio unit.", "Subtotal"};
    private transient List<DetalleVenta> partidas;

    public PartidasTableModel() {
        this.partidas = new ArrayList<>();
    }

    public void actualizar(List<DetalleVenta> nuevas) {
        this.partidas = nuevas != null ? new ArrayList<>(nuevas) : new ArrayList<>();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return partidas.size();
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
        DetalleVenta d = partidas.get(fila);
        switch (col) {
            case 0: return d.getProducto().getCodigo();
            case 1: return d.getProducto().getNombre();
            case 2: return d.getCantidad();
            case 3: return d.getProducto().getPrecio();
            case 4: return d.subtotal().valor();
            default: return null;
        }
    }
}
