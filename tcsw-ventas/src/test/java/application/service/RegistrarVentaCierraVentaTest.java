package application.service;

import adapter.out.memory.ProductoRepositoryEnMemoria;
import adapter.out.memory.VentaRepositoryEnMemoria;
import application.port.in.ItemVenta;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// C02: una venta registrada debe quedar CERRADA (en P08 quedaba cerrada=false en la base)
class RegistrarVentaCierraVentaTest {

    private ProductoRepositoryEnMemoria productos;
    private VentaRepositoryEnMemoria ventas;
    private RegistrarVentaService servicio;

    @BeforeEach
    void preparar() {
        productos = new ProductoRepositoryEnMemoria();
        ventas = new VentaRepositoryEnMemoria();
        productos.guardar(new Producto("P1", "Playera", 100.0, 10));
        servicio = new RegistrarVentaService(productos, ventas);
    }

    @Test
    void laVentaRegistradaQuedaCerrada() {
        Venta venta = servicio.registrar("F-1", List.of(new ItemVenta("P1", 2)));

        assertTrue(venta.isCerrada());
        assertTrue(ventas.buscarPorFolio("F-1").orElseThrow(AssertionError::new).isCerrada());
    }

    @Test
    void unaVentaSinPartidasSeRechazaYNoSeGuarda() {
        assertThrows(IllegalStateException.class, () -> servicio.registrar("F-VACIA", List.of()));

        assertTrue(ventas.buscarPorFolio("F-VACIA").isEmpty());
    }
}
