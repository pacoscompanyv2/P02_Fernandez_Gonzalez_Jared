package domain;

public class Producto {

    private final String codigo;
    private final String nombre;
    private final double precio;
    private int existencia;

    public Producto(String codigo, String nombre, double precio, int existencia) {
        // validaciones basicas del constructor
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo no puede estar vacio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (existencia < 0) {
            throw new IllegalArgumentException("La existencia no puede ser negativa");
        }

        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getExistencia() {
        return existencia;
    }

    // baja stock cuando se vende algo
    public void descontar(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (cantidad > existencia) {
            throw new IllegalStateException("Existencia insuficiente");
        }

        existencia = existencia - cantidad;
    }

    // sube stock, por ejemplo cuando llega mercancia nueva
    public void aumentar(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }

        existencia = existencia + cantidad;
    }
}
