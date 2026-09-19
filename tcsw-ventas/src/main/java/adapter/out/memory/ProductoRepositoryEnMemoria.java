package adapter.out.memory;

import application.port.out.ProductoRepository;
import domain.Producto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductoRepositoryEnMemoria implements ProductoRepository {

    private final Map<String, Producto> datos = new HashMap<>();

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(datos.get(codigo));
    }

    @Override
    public void guardar(Producto producto) {
        datos.put(producto.getCodigo(), producto);
    }
}