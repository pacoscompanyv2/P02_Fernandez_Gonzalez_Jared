package adapter.in.rest;

import adapter.in.rest.dto.ErrorResponse;
import adapter.in.rest.dto.ItemVentaRequest;
import adapter.in.rest.dto.ProductoResponse;
import adapter.in.rest.dto.VentaRequest;
import adapter.in.rest.dto.VentaResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// el recorrido completo por HTTP REAL (Tomcat en un puerto libre) con almacenamiento en MEMORIA: nunca toca PostgreSQL.
// Es la version automatica de scripts/verificar-p09.sh (fase de memoria).
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "tcsw.almacenamiento=memoria")
class ApiRecorridoTest {

    @LocalServerPort
    private int puerto;

    @Autowired
    private TestRestTemplate http;

    private String url(String ruta) {
        return "http://127.0.0.1:" + puerto + ruta;
    }

    private int existencia(String codigo) {
        ResponseEntity<ProductoResponse> r = http.getForEntity(url("/api/v1/productos/" + codigo), ProductoResponse.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        return r.getBody().existencia();
    }

    private static VentaRequest venta(String folio, String codigo, int cantidad) {
        return new VentaRequest(folio, List.of(new ItemVentaRequest(codigo, cantidad)));
    }

    @Test
    void registrarConsultarYVerElInventarioPorHttp() {
        int antes = existencia("P3");

        ResponseEntity<VentaResponse> creada = http.postForEntity(url("/api/v1/ventas"), venta("REC-1", "P3", 2), VentaResponse.class);
        assertEquals(HttpStatus.CREATED, creada.getStatusCode());
        assertNotNull(creada.getHeaders().getLocation());
        assertTrue(creada.getHeaders().getLocation().toString().endsWith("/api/v1/ventas/REC-1"));
        assertEquals("CERRADA", creada.getBody().estado());
        assertEquals("198.00", creada.getBody().total().toPlainString());

        ResponseEntity<VentaResponse> consultada = http.getForEntity(creada.getHeaders().getLocation(), VentaResponse.class);
        assertEquals(HttpStatus.OK, consultada.getStatusCode());
        assertEquals("REC-1", consultada.getBody().folio());
        assertEquals(1, consultada.getBody().partidas().size());
        assertEquals(antes - 2, existencia("P3"));
    }

    @Test
    void elMismoFolioDosVecesEs409() {
        assertEquals(HttpStatus.CREATED, http.postForEntity(url("/api/v1/ventas"), venta("REC-2", "P3", 1), VentaResponse.class).getStatusCode());

        ResponseEntity<ErrorResponse> repetida = http.postForEntity(url("/api/v1/ventas"), venta("REC-2", "P3", 1), ErrorResponse.class);
        assertEquals(HttpStatus.CONFLICT, repetida.getStatusCode());
        assertEquals("FOLIO_DUPLICADO", repetida.getBody().codigo());
    }

    @Test
    void unaVentaInvalidaEs400ConDetallesPorCampo() {
        ResponseEntity<ErrorResponse> r = http.postForEntity(url("/api/v1/ventas"), new VentaRequest("", List.of()), ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("VALIDACION", r.getBody().codigo());
        assertFalse(r.getBody().detalles().isEmpty());
    }

    @Test
    void unJsonRotoEs400() {
        HttpHeaders cabeceras = new HttpHeaders();
        cabeceras.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ErrorResponse> r = http.postForEntity(url("/api/v1/ventas"), new HttpEntity<>("{\"folio\": ", cabeceras), ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("JSON_INVALIDO", r.getBody().codigo());
    }

    @Test
    void sinExistenciaEs409YElInventarioNoCambia() {
        int antes = existencia("P2");

        ResponseEntity<ErrorResponse> r = http.postForEntity(url("/api/v1/ventas"), venta("REC-3", "P2", antes + 1), ErrorResponse.class);

        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertEquals("EXISTENCIA_INSUFICIENTE", r.getBody().codigo());
        assertEquals(antes, existencia("P2"));
        assertEquals(HttpStatus.NOT_FOUND, http.getForEntity(url("/api/v1/ventas/REC-3"), ErrorResponse.class).getStatusCode());
    }

    @Test
    void unProductoInexistenteEs404() {
        ResponseEntity<ErrorResponse> r = http.postForEntity(url("/api/v1/ventas"), venta("REC-4", "NOPE", 1), ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("PRODUCTO_NO_ENCONTRADO", r.getBody().codigo());
    }

    @Test
    void borrarUnaVentaNoEstaPermitido() {
        ResponseEntity<ErrorResponse> r = http.exchange(url("/api/v1/ventas/REC-1"), HttpMethod.DELETE, null, ErrorResponse.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, r.getStatusCode());
        assertEquals("METODO_NO_PERMITIDO", r.getBody().codigo());
    }

    @Test
    void elContratoSeSirveEnOpenapiYaml() {
        ResponseEntity<String> r = http.getForEntity(url("/openapi.yaml"), String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertTrue(r.getBody().contains("openapi: 3.0.3"));
        assertTrue(r.getBody().contains("/api/v1/ventas"));
    }
}
