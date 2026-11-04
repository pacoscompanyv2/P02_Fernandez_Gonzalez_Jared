package adapter.in.rest;

import adapter.in.rest.dto.ErrorResponse;
import adapter.in.rest.dto.ProductoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// arranca la API de verdad (Tomcat en puerto libre) con almacenamiento en MEMORIA: nunca toca PostgreSQL
// aunque tengas las variables TCSW_* exportadas. Comprueba el cableado completo: Spring, controlador, facade, errores.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "tcsw.almacenamiento=memoria")
class ApiArranqueTest {

    @LocalServerPort
    private int puerto;

    @Autowired
    private TestRestTemplate http;

    private String url(String ruta) {
        return "http://127.0.0.1:" + puerto + ruta;
    }

    @Test
    void elCatalogoInicialSeListaPorHttp() {
        ResponseEntity<ProductoResponse[]> respuesta = http.getForEntity(url("/api/v1/productos"), ProductoResponse[].class);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(3, respuesta.getBody().length);
        assertEquals("P1", respuesta.getBody()[0].codigo());
    }

    @Test
    void unProductoInexistenteDevuelveElFormatoDeErrorPorHttp() {
        ResponseEntity<ErrorResponse> respuesta = http.getForEntity(url("/api/v1/productos/NOPE"), ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals("PRODUCTO_NO_ENCONTRADO", respuesta.getBody().codigo());
        assertEquals("/api/v1/productos/NOPE", respuesta.getBody().ruta());
    }

    @Test
    void unaRutaInexistenteTambienUsaElFormatoDeError() {
        ResponseEntity<ErrorResponse> respuesta = http.getForEntity(url("/api/v1/no-existe"), ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals("RECURSO_NO_ENCONTRADO", respuesta.getBody().codigo());
    }
}
