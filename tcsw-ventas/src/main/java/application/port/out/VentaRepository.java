package application.port.out;

import domain.Venta;
import java.util.Optional;

// puerto de salida: lo que el caso de uso necesita para persistir ventas
public interface VentaRepository {
    void guardar(Venta venta);
    Optional<Venta> buscarPorFolio(String folio);
}
