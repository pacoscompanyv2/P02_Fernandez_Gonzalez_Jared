package adapter.in.rest.controller;

import adapter.in.rest.error.ManejadorErrores;
import application.facade.VentasFacade;
import domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// controlador + ManejadorErrores con MockMvc "standalone": sin servidor ni PostgreSQL, el facade es un doble de Mockito
class ProductoControllerTest {

    private VentasFacade facade;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        facade = mock(VentasFacade.class);
        mvc = MockMvcBuilders.standaloneSetup(new ProductoController(facade))
            .setControllerAdvice(new ManejadorErrores())
            .build();
    }

    @Test
    void listarRegresaTodosLosProductos() throws Exception {
        when(facade.listarProductos()).thenReturn(List.of(
            new Producto("P1", "Playera Deportiva", 150.0, 20),
            new Producto("P2", "Pantalon Mezclilla", 350.0, 15)));

        mvc.perform(get("/api/v1/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].codigo").value("P1"))
            .andExpect(jsonPath("$[0].nombre").value("Playera Deportiva"))
            .andExpect(jsonPath("$[0].precio").value(150.0))
            .andExpect(jsonPath("$[0].existencia").value(20))
            .andExpect(jsonPath("$[1].codigo").value("P2"));
    }

    @Test
    void obtenerRegresaElProductoPedido() throws Exception {
        when(facade.buscarProducto("P1")).thenReturn(Optional.of(new Producto("P1", "Playera Deportiva", 150.0, 20)));

        mvc.perform(get("/api/v1/productos/P1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codigo").value("P1"))
            .andExpect(jsonPath("$.existencia").value(20));
    }

    @Test
    void obtenerUnCodigoInexistenteRegresa404ConElFormatoDeError() throws Exception {
        when(facade.buscarProducto("NOPE")).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/productos/NOPE"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.codigo").value("PRODUCTO_NO_ENCONTRADO"))
            .andExpect(jsonPath("$.mensaje").value("Producto no encontrado: NOPE"))
            .andExpect(jsonPath("$.ruta").value("/api/v1/productos/NOPE"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void unMetodoNoPermitidoRegresa405ConElMismoFormato() throws Exception {
        mvc.perform(post("/api/v1/productos/P1"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(header().exists("Allow"))
            .andExpect(jsonPath("$.codigo").value("METODO_NO_PERMITIDO"))
            .andExpect(jsonPath("$.status").value(405));
    }
}
