package adapter.in.rest.controller;

import adapter.in.rest.error.ManejadorErrores;
import application.exception.ExistenciaInsuficienteException;
import application.exception.ProductoNoEncontradoException;
import application.facade.VentasFacade;
import application.port.in.ItemVenta;
import domain.Producto;
import domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// MockMvc "standalone": sin servidor ni PostgreSQL; el facade es un doble de Mockito y el manejador de errores es el real
class VentaControllerTest {

    private static final String RUTA = "/api/v1/ventas";

    private VentasFacade facade;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        facade = mock(VentasFacade.class);
        mvc = MockMvcBuilders.standaloneSetup(new VentaController(facade))
            .setControllerAdvice(new ManejadorErrores())
            .build();
    }

    private static Venta ventaCerrada(String folio, int cantidad) {
        Venta venta = new Venta(folio);
        venta.agregarPartida(new Producto("P1", "Playera Deportiva", 150.0, 20), cantidad);
        venta.cerrar();
        return venta;
    }

    private org.springframework.test.web.servlet.ResultActions enviar(String json) throws Exception {
        return mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void registrarUnaVentaValidaRegresa201ConLocationYElCuerpo() throws Exception {
        when(facade.registrarVenta(eq("F-1"), anyList())).thenReturn(ventaCerrada("F-1", 2));

        enviar("{\"folio\":\"F-1\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":2}]}")
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", endsWith("/api/v1/ventas/F-1")))
            .andExpect(jsonPath("$.folio").value("F-1"))
            .andExpect(jsonPath("$.estado").value("CERRADA"))
            .andExpect(jsonPath("$.partidas[0].codigoProducto").value("P1"))
            .andExpect(jsonPath("$.partidas[0].cantidad").value(2))
            .andExpect(jsonPath("$.total").value(300.0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void elControladorPasaAlFacadeElFolioYLasPartidasLimpios() throws Exception {
        when(facade.registrarVenta(anyString(), anyList())).thenReturn(ventaCerrada("F-2", 1));

        enviar("{\"folio\":\"  F-2  \",\"items\":[{\"codigoProducto\":\" P1 \",\"cantidad\":1}]}")
            .andExpect(status().isCreated());

        ArgumentCaptor<List<ItemVenta>> items = ArgumentCaptor.forClass(List.class);
        verify(facade).registrarVenta(eq("F-2"), items.capture());
        assertEquals(1, items.getValue().size());
        assertEquals("P1", items.getValue().get(0).getCodigoProducto());
        assertEquals(1, items.getValue().get(0).getCantidad());
    }

    @Test
    void unFolioVacioEs400ConDetalleDelCampo() throws Exception {
        enviar("{\"folio\":\" \",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":1}]}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("VALIDACION"))
            .andExpect(jsonPath("$.detalles[0].campo").value("folio"))
            .andExpect(jsonPath("$.detalles[0].mensaje").value("El folio es obligatorio"));
        verify(facade, never()).registrarVenta(any(), any());
    }

    @Test
    void unFolioDeMasDe30CaracteresEs400() throws Exception {
        String largo = "F".repeat(31);
        enviar("{\"folio\":\"" + largo + "\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":1}]}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detalles[0].campo").value("folio"))
            .andExpect(jsonPath("$.detalles[0].mensaje").value("El folio no puede pasar de 30 caracteres"));
    }

    @Test
    void sinPartidasEs400() throws Exception {
        enviar("{\"folio\":\"F-3\",\"items\":[]}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detalles[0].campo").value("items"))
            .andExpect(jsonPath("$.detalles[0].mensaje").value("La venta necesita al menos una partida"));
    }

    @Test
    void sinElCampoItemsEs400() throws Exception {
        enviar("{\"folio\":\"F-3\"}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detalles[0].campo").value("items"));
    }

    @Test
    void unaCantidadCeroEs400ConLaRutaDeLaPartida() throws Exception {
        enviar("{\"folio\":\"F-4\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":0}]}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detalles[0].campo").value("items[0].cantidad"))
            .andExpect(jsonPath("$.detalles[0].mensaje").value("La cantidad debe ser mayor a cero"));
    }

    @Test
    void sinCantidadEs400() throws Exception {
        enviar("{\"folio\":\"F-4\",\"items\":[{\"codigoProducto\":\"P1\"}]}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detalles[0].campo").value("items[0].cantidad"))
            .andExpect(jsonPath("$.detalles[0].mensaje").value("La cantidad es obligatoria"));
    }

    @Test
    void unCodigoDeProductoVacioEs400() throws Exception {
        enviar("{\"folio\":\"F-4\",\"items\":[{\"codigoProducto\":\"\",\"cantidad\":1}]}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detalles[0].campo").value("items[0].codigoProducto"));
    }

    @Test
    void unJsonRotoEs400() throws Exception {
        enviar("{\"folio\": ")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("JSON_INVALIDO"));
    }

    @Test
    void unaCantidadQueNoEsNumeroEs400() throws Exception {
        enviar("{\"folio\":\"F-5\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":\"muchas\"}]}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("JSON_INVALIDO"));
    }

    @Test
    void sinContentTypeJsonEs415() throws Exception {
        mvc.perform(post(RUTA).contentType(MediaType.TEXT_PLAIN).content("folio=F-6"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.codigo").value("MEDIA_TYPE_NO_SOPORTADO"));
    }

    @Test
    void unProductoInexistenteEs404() throws Exception {
        when(facade.registrarVenta(anyString(), anyList())).thenThrow(new ProductoNoEncontradoException("NOPE"));

        enviar("{\"folio\":\"F-7\",\"items\":[{\"codigoProducto\":\"NOPE\",\"cantidad\":1}]}")
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo").value("PRODUCTO_NO_ENCONTRADO"))
            .andExpect(jsonPath("$.mensaje").value("Producto no encontrado: NOPE"));
    }

    @Test
    void sinExistenciaSuficienteEs409() throws Exception {
        when(facade.registrarVenta(anyString(), anyList())).thenThrow(new ExistenciaInsuficienteException("P1", 2, 50));

        enviar("{\"folio\":\"F-8\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":50}]}")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("EXISTENCIA_INSUFICIENTE"))
            .andExpect(jsonPath("$.mensaje").value("Existencia insuficiente para P1: disponible 2, solicitado 50"));
    }

    @Test
    void unFolioRepetidoEs409YNoLlegaAlCasoDeUso() throws Exception {
        when(facade.buscarVenta("F-9")).thenReturn(Optional.of(ventaCerrada("F-9", 1)));

        enviar("{\"folio\":\"F-9\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":1}]}")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("FOLIO_DUPLICADO"));
        verify(facade, never()).registrarVenta(any(), any());
    }

    @Test
    void consultarUnaVentaExistenteRegresa200() throws Exception {
        when(facade.buscarVenta("F-10")).thenReturn(Optional.of(ventaCerrada("F-10", 3)));

        mvc.perform(get(RUTA + "/F-10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.folio").value("F-10"))
            .andExpect(jsonPath("$.estado").value("CERRADA"))
            .andExpect(jsonPath("$.partidas.length()").value(1))
            .andExpect(jsonPath("$.total").value(450.0));
    }

    @Test
    void consultarUnaVentaInexistenteRegresa404() throws Exception {
        mvc.perform(get(RUTA + "/NO-EXISTE"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo").value("VENTA_NO_ENCONTRADA"))
            .andExpect(jsonPath("$.ruta").value("/api/v1/ventas/NO-EXISTE"));
    }
}
