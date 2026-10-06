package application.port.out;

import domain.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    void guardar(Producto producto);
    Optional<Producto> buscarPorCodigo(String codigo);
    List<Producto> listarTodos();
}