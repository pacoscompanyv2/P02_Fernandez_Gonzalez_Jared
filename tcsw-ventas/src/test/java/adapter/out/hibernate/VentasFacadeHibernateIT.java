package adapter.out.hibernate;

import application.facade.VentasFacade;
import application.port.in.ItemVenta;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// el caso de uso completo (facade -> servicio -> adaptadores Hibernate) y la persistencia tras "reiniciar la aplicacion"
class VentasFacadeHibernateIT extends HibernateTestBase {

    private VentasFacade nuevoFacade() {
        return new VentasFacade(new ProductoRepositoryHibernate(sessionFactory), new VentaRepositoryHibernate(sessionFactory));
    }

    @Test
    void registrarVentaPersisteYSobreviveAUnReinicio() throws SQLException {
        VentasFacade facade = nuevoFacade();
        facade.registrarProducto(new Producto("P1", "Playera", 100.0, 10));
        facade.registrarVenta("F-FACADE-1", List.of(new ItemVenta("P1", 3)));
        assertEquals(7, existenciaEnBd("P1"));

        // "cerrar y volver a abrir la aplicacion": SessionFactory y repositorios nuevos
        sessionFactory.close();
        abrirSessionFactory();
        VentasFacade reiniciado = nuevoFacade();

        assertEquals(7, reiniciado.buscarProducto("P1").orElseThrow(AssertionError::new).getExistencia());
        Venta recuperada = reiniciado.getVentaRepository().buscarPorFolio("F-FACADE-1").orElseThrow(AssertionError::new);
        assertEquals(1, recuperada.getPartidas().size());
        assertEquals(3, recuperada.getPartidas().get(0).getCantidad());
    }

    @Test
    void unaVentaRechazadaPorExistenciaNoCambiaNada() throws SQLException {
        VentasFacade facade = nuevoFacade();
        facade.registrarProducto(new Producto("P1", "Playera", 100.0, 2));

        List<ItemVenta> pedido = List.of(new ItemVenta("P1", 5));
        assertThrows(IllegalStateException.class, () -> facade.registrarVenta("F-X", pedido));

        assertEquals(0, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(2, existenciaEnBd("P1"));
        assertTrue(!facade.getVentaRepository().buscarPorFolio("F-X").isPresent());
    }
}
