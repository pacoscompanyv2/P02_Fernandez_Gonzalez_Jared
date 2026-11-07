package adapter.out.hibernate;

import adapter.in.rest.controller.ProductoController;
import adapter.in.rest.controller.VentaController;
import adapter.in.rest.error.ManejadorErrores;
import application.facade.VentasFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// controladores REST reales -> VentasFacade -> adaptadores HIBERNATE -> PostgreSQL (base *_test).
// Se comprueba lo que de verdad quedo en la base: venta cerrada, existencia descontada, rollback y persistencia tras reiniciar.
class ApiVentasHibernateIT extends HibernateTestBase {

    private static final String PEDIDO_P1_X3 = "{\"folio\":\"IT-API-1\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":3}]}";

    private MockMvc mvc;

    @BeforeEach
    void montarApi() {
        VentasFacade facade = new VentasFacade(new ProductoRepositoryHibernate(sessionFactory), new VentaRepositoryHibernate(sessionFactory));
        mvc = MockMvcBuilders.standaloneSetup(new ProductoController(facade), new VentaController(facade))
            .setControllerAdvice(new ManejadorErrores())
            .build();
    }

    private ResultActions registrar(String json) throws Exception {
        return mvc.perform(post("/api/v1/ventas").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void registrarPorHttpDejaLaVentaCerradaYDescuentaLaExistencia() throws Exception {
        sembrarProducto("P1", "Playera", 100.0, 10);

        registrar(PEDIDO_P1_X3)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.estado").value("CERRADA"))
            .andExpect(jsonPath("$.total").value(300.0));

        assertEquals(1, consultarEntero("SELECT count(*) FROM venta WHERE folio = ?", "IT-API-1"));
        assertEquals("true", consultarTexto("SELECT cerrada::text FROM venta WHERE folio = ?", "IT-API-1"));
        assertEquals(7, existenciaEnBd("P1"));
    }

    @Test
    void laVentaSeConsultaIgualDespuesDeReiniciarLaAplicacion() throws Exception {
        sembrarProducto("P1", "Playera", 100.0, 10);
        registrar(PEDIDO_P1_X3).andExpect(status().isCreated());

        // "reiniciar": SessionFactory, repositorios y controladores nuevos sobre la misma base
        sessionFactory.close();
        abrirSessionFactory();
        montarApi();

        mvc.perform(get("/api/v1/ventas/IT-API-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folio").value("IT-API-1"))
            .andExpect(jsonPath("$.estado").value("CERRADA"))
            .andExpect(jsonPath("$.partidas.length()").value(1))
            .andExpect(jsonPath("$.partidas[0].cantidad").value(3));
    }

    @Test
    void unFolioRepetidoEs409YNoCambiaLaBase() throws Exception {
        sembrarProducto("P1", "Playera", 100.0, 10);
        registrar(PEDIDO_P1_X3).andExpect(status().isCreated());

        registrar(PEDIDO_P1_X3)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("FOLIO_DUPLICADO"));

        assertEquals(1, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(7, existenciaEnBd("P1"));
    }

    @Test
    void sinExistenciaEs409YNoQuedaNadaEnLaBase() throws Exception {
        sembrarProducto("P1", "Playera", 100.0, 2);

        registrar("{\"folio\":\"IT-API-2\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":5}]}")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("EXISTENCIA_INSUFICIENTE"));

        assertEquals(0, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(2, existenciaEnBd("P1"));
    }

    @Test
    void unProductoInexistenteEs404SinTocarLaBase() throws Exception {
        sembrarProducto("P1", "Playera", 100.0, 10);

        registrar("{\"folio\":\"IT-API-3\",\"items\":[{\"codigoProducto\":\"NOPE\",\"cantidad\":1}]}")
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo").value("PRODUCTO_NO_ENCONTRADO"));

        assertEquals(0, consultarEntero("SELECT count(*) FROM venta"));
        assertEquals(10, existenciaEnBd("P1"));
    }

    @Test
    void elCatalogoSembradoSeListaPorHttp() throws Exception {
        sembrarProducto("P1", "Playera", 100.0, 10);
        sembrarProducto("P2", "Pantalon", 350.0, 5);

        mvc.perform(get("/api/v1/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
