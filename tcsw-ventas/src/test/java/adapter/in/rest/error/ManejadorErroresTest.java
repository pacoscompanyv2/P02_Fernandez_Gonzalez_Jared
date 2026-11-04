package adapter.in.rest.error;

import application.exception.ExistenciaInsuficienteException;
import application.exception.ProductoNoEncontradoException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// una prueba por tipo de error: TODOS deben salir con el mismo formato (status, error, codigo, mensaje, ruta, timestamp)
class ManejadorErroresTest {

    @RestController
    public static class Falla {
        @GetMapping("/falla/producto")
        public String producto() {
            throw new ProductoNoEncontradoException("X9");
        }

        @GetMapping("/falla/venta")
        public String venta() {
            throw new VentaNoEncontradaException("F-9");
        }

        @GetMapping("/falla/existencia")
        public String existencia() {
            throw new ExistenciaInsuficienteException("P1", 2, 5);
        }

        @GetMapping("/falla/folio")
        public String folio() {
            throw new FolioDuplicadoException("F-1");
        }

        @GetMapping("/falla/argumento")
        public String argumento() {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        @GetMapping("/falla/estado")
        public String estado() {
            throw new IllegalStateException("La venta ya esta cerrada");
        }

        @GetMapping("/falla/inesperado")
        public String inesperado() {
            throw new RuntimeException("detalle interno: password=secreto en tabla venta");
        }

        @PostMapping(path = "/falla/validar", consumes = MediaType.APPLICATION_JSON_VALUE)
        public String validar(@Valid @RequestBody Dto dto) {
            return "ok";
        }
    }

    public record Dto(@NotBlank(message = "El nombre es obligatorio") String nombre,
                      @Min(value = 1, message = "n debe ser mayor a cero") int n) {
    }

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new Falla()).setControllerAdvice(new ManejadorErrores()).build();
    }

    @Test
    void productoNoEncontradoEs404() throws Exception {
        mvc.perform(get("/falla/producto"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.codigo").value("PRODUCTO_NO_ENCONTRADO"))
            .andExpect(jsonPath("$.ruta").value("/falla/producto"));
    }

    @Test
    void ventaNoEncontradaEs404() throws Exception {
        mvc.perform(get("/falla/venta"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo").value("VENTA_NO_ENCONTRADA"))
            .andExpect(jsonPath("$.mensaje").value("Venta no encontrada: F-9"));
    }

    @Test
    void existenciaInsuficienteEs409() throws Exception {
        mvc.perform(get("/falla/existencia"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("EXISTENCIA_INSUFICIENTE"))
            .andExpect(jsonPath("$.mensaje").value("Existencia insuficiente para P1: disponible 2, solicitado 5"));
    }

    @Test
    void folioDuplicadoEs409() throws Exception {
        mvc.perform(get("/falla/folio"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("FOLIO_DUPLICADO"));
    }

    @Test
    void argumentoInvalidoEs400() throws Exception {
        mvc.perform(get("/falla/argumento"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
            .andExpect(jsonPath("$.mensaje").value("La cantidad debe ser mayor a cero"));
    }

    @Test
    void estadoInvalidoEs409() throws Exception {
        mvc.perform(get("/falla/estado"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.codigo").value("CONFLICTO"));
    }

    @Test
    void unErrorInesperadoEs500YNoFiltraLaCausaInterna() throws Exception {
        MvcResult resultado = mvc.perform(get("/falla/inesperado"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
            .andExpect(jsonPath("$.mensaje").value("Error interno del servidor"))
            .andReturn();
        String cuerpo = resultado.getResponse().getContentAsString();
        assertFalse(cuerpo.contains("secreto"), "el cuerpo no debe revelar la causa interna");
        assertFalse(cuerpo.contains("password"), "el cuerpo no debe revelar la causa interna");
    }

    @Test
    void laValidacionListaCadaCampoInvalidoOrdenado() throws Exception {
        mvc.perform(post("/falla/validar").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\" \",\"n\":0}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("VALIDACION"))
            .andExpect(jsonPath("$.detalles.length()").value(2))
            .andExpect(jsonPath("$.detalles[0].campo").value("n"))
            .andExpect(jsonPath("$.detalles[0].mensaje").value("n debe ser mayor a cero"))
            .andExpect(jsonPath("$.detalles[1].campo").value("nombre"))
            .andExpect(jsonPath("$.detalles[1].mensaje").value("El nombre es obligatorio"));
    }

    @Test
    void unJsonRotoEs400() throws Exception {
        mvc.perform(post("/falla/validar").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\": "))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("JSON_INVALIDO"));
    }

    @Test
    void unTipoDeDatoIncorrectoEs400() throws Exception {
        mvc.perform(post("/falla/validar").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"a\",\"n\":\"muchos\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("JSON_INVALIDO"));
    }

    @Test
    void unTipoDeContenidoNoSoportadoEs415() throws Exception {
        mvc.perform(post("/falla/validar").contentType(MediaType.TEXT_PLAIN).content("hola"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.codigo").value("MEDIA_TYPE_NO_SOPORTADO"));
    }
}
