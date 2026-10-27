package adapter.out.hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
public class DetalleVentaEntity {

    @EmbeddedId
    private DetalleVentaId id;

    @MapsId("folio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "folio")
    private VentaEntity venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codigo")
    private ProductoEntity producto;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    // precio AL MOMENTO de la venta (no el actual del producto)
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    protected DetalleVentaEntity() {
        // lo exige JPA
    }

    public DetalleVentaEntity(VentaEntity venta, int linea, ProductoEntity producto, int cantidad, BigDecimal precioUnitario) {
        this.id = new DetalleVentaId(venta.getFolio(), linea);
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public DetalleVentaId getId() {
        return id;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
}
