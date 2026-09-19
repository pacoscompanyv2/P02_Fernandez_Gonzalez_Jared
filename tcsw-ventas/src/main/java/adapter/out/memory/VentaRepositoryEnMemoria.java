package adapter.out.memory;

import application.port.out.VentaRepository;
import domain.Venta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VentaRepositoryEnMemoria implements VentaRepository {

    private final Map<String, Venta> datos = new HashMap<>();

    @Override
    public void guardar(Venta venta) {
        datos.put(venta.getFolio(), venta);
    }

    @Override
    public Optional<Venta> buscarPorFolio(String folio) {
        return Optional.ofNullable(datos.get(folio));
    }
}