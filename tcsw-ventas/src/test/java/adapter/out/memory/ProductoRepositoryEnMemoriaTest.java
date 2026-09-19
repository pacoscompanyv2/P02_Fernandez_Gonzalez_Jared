package adapter.out.memory;

import domain.Producto;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoRepositoryEnMemoriaTest {

    @Test
    void guardaYBuscaProductoCorrectamente() {
        ProductoRepositoryEnMemoria repo = new ProductoRepositoryEnMemoria();
        repo.guardar(new Producto("P1", "Playera", 100.0, 10));

        Optional<Producto> encontrado = repo.buscarPorCodigo("P1");

        assertTrue(encontrado.isPresent());
        assertEquals("Playera", encontrado.get().getNombre());
    }

    @Test
    void buscarPorCodigoInexistenteRegresaVacio() {
        ProductoRepositoryEnMemoria repo = new ProductoRepositoryEnMemoria();

        assertTrue(repo.buscarPorCodigo("NO-EXISTE").isEmpty());
    }
}
