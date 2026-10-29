package adapter.out.hibernate;

import adapter.out.jdbc.ProductoRepositoryJdbc;
import adapter.out.jdbc.VentaRepositoryJdbc;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// consistencia transaccional (ADR-008): dos ventas arman su carrito con la MISMA lectura vieja (P1 = 10) y piden 6 cada una.
// solo hay stock para UNA. JDBC (P08) no lo detecta; Hibernate con bloqueo + descuento relativo, si.
class ConcurrenciaVentasIT extends HibernateTestBase {

    @BeforeEach
    void sembrar() throws SQLException {
        sembrarProducto("P1", "Playera Deportiva", 150.0, 10);
    }

    // ANTES (deuda conocida D2): el adaptador JDBC escribe existencia = 4 dos veces y vende 12 de 10.
    // si algun dia se corrige JDBC, esta prueba debe cambiarse a assertThrows (y ADR-008 actualizarse).
    @Test
    void jdbcConLecturaVieja_vendeMasDeLoQueHay_deudaConocida() throws SQLException {
        ProductoRepositoryJdbc productos = new ProductoRepositoryJdbc(conexion);
        VentaRepositoryJdbc ventas = new VentaRepositoryJdbc(conexion);
        Venta a = new Venta("A");
        a.agregarPartida(productos.buscarPorCodigo("P1").orElseThrow(AssertionError::new), 6);
        Venta b = new Venta("B");
        b.agregarPartida(productos.buscarPorCodigo("P1").orElseThrow(AssertionError::new), 6);

        ventas.guardar(a);
        ventas.guardar(b);

        assertEquals(2, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(4, existenciaEnBd("P1"));     // deberia ser -2: se "perdio" una actualizacion
    }

    // DESPUES: Hibernate bloquea la fila, lee la existencia ACTUAL y rechaza la segunda venta.
    @Test
    void hibernateConLecturaVieja_rechazaLaSegundaVenta() throws SQLException {
        ProductoRepositoryHibernate productos = new ProductoRepositoryHibernate(sessionFactory);
        VentaRepositoryHibernate ventas = new VentaRepositoryHibernate(sessionFactory);
        Venta a = new Venta("A");
        a.agregarPartida(productos.buscarPorCodigo("P1").orElseThrow(AssertionError::new), 6);
        Venta b = new Venta("B");
        b.agregarPartida(productos.buscarPorCodigo("P1").orElseThrow(AssertionError::new), 6);

        ventas.guardar(a);
        RepositorioHibernateException e = assertThrows(RepositorioHibernateException.class, () -> ventas.guardar(b));

        assertEquals(true, e.getMessage().contains("Existencia insuficiente"));
        assertEquals(1, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(4, existenciaEnBd("P1"));
    }

    // dos hilos de verdad al mismo tiempo: el bloqueo hace que uno espere al otro; gana uno y pierde uno
    @Test
    void hibernateConDosHilosSimultaneos_soloUnaVentaPasa() throws Exception {
        ProductoRepositoryHibernate productos = new ProductoRepositoryHibernate(sessionFactory);
        VentaRepositoryHibernate ventas = new VentaRepositoryHibernate(sessionFactory);
        Venta a = new Venta("HILO-A");
        a.agregarPartida(productos.buscarPorCodigo("P1").orElseThrow(AssertionError::new), 6);
        Venta b = new Venta("HILO-B");
        b.agregarPartida(productos.buscarPorCodigo("P1").orElseThrow(AssertionError::new), 6);

        CountDownLatch salida = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<Boolean>> resultados = new ArrayList<>();
            for (Venta venta : new Venta[] {a, b}) {
                Callable<Boolean> tarea = () -> {
                    salida.await();
                    try {
                        ventas.guardar(venta);
                        return true;
                    } catch (RepositorioHibernateException e) {
                        return false;
                    }
                };
                resultados.add(pool.submit(tarea));
            }
            salida.countDown();
            int exitos = 0;
            for (Future<Boolean> r : resultados) {
                if (r.get(30, TimeUnit.SECONDS)) {
                    exitos++;
                }
            }
            assertEquals(1, exitos);
        } finally {
            pool.shutdownNow();
        }
        assertEquals(1, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(4, existenciaEnBd("P1"));
    }
}
