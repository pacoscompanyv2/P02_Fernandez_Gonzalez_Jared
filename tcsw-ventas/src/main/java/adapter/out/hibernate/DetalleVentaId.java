package adapter.out.hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// llave compuesta (folio, linea) de detalle_venta
@Embeddable
public class DetalleVentaId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "folio", length = 30)
    private String folio;

    @Column(name = "linea")
    private int linea;

    protected DetalleVentaId() {
        // lo exige JPA
    }

    public DetalleVentaId(String folio, int linea) {
        this.folio = folio;
        this.linea = linea;
    }

    public String getFolio() {
        return folio;
    }

    public int getLinea() {
        return linea;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof DetalleVentaId)) {
            return false;
        }
        DetalleVentaId that = (DetalleVentaId) otro;
        return linea == that.linea && Objects.equals(folio, that.folio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folio, linea);
    }
}
