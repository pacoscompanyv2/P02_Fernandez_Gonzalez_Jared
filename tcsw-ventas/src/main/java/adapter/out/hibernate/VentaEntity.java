package adapter.out.hibernate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// la columna creada_en NO se mapea a proposito: PostgreSQL la llena con DEFAULT now()
@Entity
@Table(name = "venta")
public class VentaEntity {

    @Id
    @Column(name = "folio", length = 30)
    private String folio;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "cerrada", nullable = false)
    private boolean cerrada;

    // las partidas viven y mueren con la venta (cascade): persistir la venta persiste sus detalles
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id.linea ASC")
    private List<DetalleVentaEntity> detalles = new ArrayList<>();

    protected VentaEntity() {
        // lo exige JPA
    }

    public VentaEntity(String folio, BigDecimal total, boolean cerrada) {
        this.folio = folio;
        this.total = total;
        this.cerrada = cerrada;
    }

    public void agregarDetalle(DetalleVentaEntity detalle) {
        detalles.add(detalle);
    }

    public String getFolio() {
        return folio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public List<DetalleVentaEntity> getDetalles() {
        return detalles;
    }
}
