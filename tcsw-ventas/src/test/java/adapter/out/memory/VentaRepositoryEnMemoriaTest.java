package adapter.out.memory;

import domain.Venta;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VentaRepositoryEnMemoriaTest {

    @Test
    void guardaYBuscaVentaCorrectamente() {
        VentaRepositoryEnMemoria repo = new VentaRepositoryEnMemoria();
        Venta venta = new Venta("F1");
        repo.guardar(venta);

        Optional<Venta> encontrada = repo.buscarPorFolio("F1");

        assertTrue(encontrada.isPresent());
        assertEquals("F1", encontrada.get().getFolio());
    }

    @Test
    void buscarPorFolioInexistenteRegresaVacio() {
        VentaRepositoryEnMemoria repo = new VentaRepositoryEnMemoria();

        assertTrue(repo.buscarPorFolio("NO-EXISTE").isEmpty());
    }
}