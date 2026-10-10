package adapter.in.swing;

import application.port.in.ItemVenta;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CarritoTableModel extends AbstractTableModel {
    private final String[] columnas = {"Producto", "Cantidad"};
    private List<ItemVenta> items;

    public CarritoTableModel(List<ItemVenta> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public void actualizar(List<ItemVenta> nuevos) {
        this.items = nuevos != null ? nuevos : new ArrayList<>();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return items.size();
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
        ItemVenta item = items.get(fila);
        switch (col) {
            case 0: return item.getCodigoProducto();
            case 1: return item.getCantidad();
            default: return null;
        }
    }
}
