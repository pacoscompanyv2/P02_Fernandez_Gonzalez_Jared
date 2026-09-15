package application.port.out;

import domain.Producto;
import java.util.Optional;

// puerto de salida: lo que el caso de uso necesita para consultar productos
public interface ProductoRepository {
    Optional<Producto> buscarPorCodigo(String codigo);
    void guardar(Producto producto);
}