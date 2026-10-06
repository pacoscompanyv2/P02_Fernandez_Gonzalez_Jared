package adapter.out.memory;

import application.port.out.ProductoRepository;
import domain.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductoRepositoryEnMemoria implements ProductoRepository {
    private final Map<String, Producto> datos = new HashMap<>();

    @Override
    public void guardar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        datos.put(producto.getCodigo(), producto);
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(datos.get(codigo));
    }

    @Override
    public List<Producto> listarTodos() {
        return new ArrayList<>(datos.values());
    }
}