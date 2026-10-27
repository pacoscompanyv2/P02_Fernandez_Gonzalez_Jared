package adapter.out.hibernate;

import domain.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

// entidad de PERSISTENCIA (no es el dominio): el dominio no sabe nada de JPA. Se traduce con aDominio()/desde().
@Entity
@Table(name = "producto")
public class ProductoEntity {

    @Id
    @Column(name = "codigo", length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "existencia", nullable = false)
    private int existencia;

    protected ProductoEntity() {
        // lo exige JPA
    }

    public ProductoEntity(String codigo, String nombre, BigDecimal precio, int existencia) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }

    public static ProductoEntity desde(Producto producto) {
        return new ProductoEntity(producto.getCodigo(), producto.getNombre(), aPrecio(producto.getPrecio()), producto.getExistencia());
    }

    public static BigDecimal aPrecio(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public Producto aDominio() {
        return new Producto(codigo, nombre, precio.doubleValue(), existencia);
    }

    public void copiarDe(Producto producto) {
        this.nombre = producto.getNombre();
        this.precio = aPrecio(producto.getPrecio());
        this.existencia = producto.getExistencia();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }
}
