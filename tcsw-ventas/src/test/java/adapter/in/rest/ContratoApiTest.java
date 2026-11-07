package adapter.in.rest;

import adapter.in.rest.controller.ProductoController;
import adapter.in.rest.controller.VentaController;
import adapter.in.rest.dto.ErrorResponse;
import adapter.in.rest.dto.ItemVentaRequest;
import adapter.in.rest.dto.PartidaResponse;
import adapter.in.rest.dto.ProductoResponse;
import adapter.in.rest.dto.VentaRequest;
import adapter.in.rest.dto.VentaResponse;
import adapter.in.rest.error.CodigoError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// el contrato (openapi.yaml) y el codigo NO pueden separarse: esta prueba compara el archivo contra los controladores y
// los DTO reales. Si alguien agrega una ruta, un campo o un codigo de error sin documentarlo, truena.
class ContratoApiTest {

    private static Map<String, Object> contrato;

    @BeforeAll
    static void cargarContrato() throws IOException {
        try (InputStream in = ContratoApiTest.class.getResourceAsStream("/static/openapi.yaml")) {
            assertNotNull(in, "No se encontro src/main/resources/static/openapi.yaml");
            contrato = new Yaml().load(in);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> mapa(Object valor) {
        return (Map<String, Object>) valor;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> lista(Object valor) {
        return (List<Object>) valor;
    }

    private static Map<String, Object> esquema(String nombre) {
        return mapa(mapa(mapa(contrato.get("components")).get("schemas")).get(nombre));
    }

    private static Set<String> componentes(Class<?> registro) {
        return Arrays.stream(registro.getRecordComponents()).map(RecordComponent::getName).collect(Collectors.toCollection(TreeSet::new));
    }

    private static Set<String> operacionesDocumentadas() {
        Set<String> operaciones = new TreeSet<>();
        for (Map.Entry<String, Object> ruta : mapa(contrato.get("paths")).entrySet()) {
            for (String metodo : mapa(ruta.getValue()).keySet()) {
                operaciones.add(metodo.toUpperCase(Locale.ROOT) + " " + ruta.getKey());
            }
        }
        return operaciones;
    }

    // lee los @RequestMapping/@GetMapping/@PostMapping reales de los controladores
    private static Set<String> operacionesImplementadas() {
        Set<String> operaciones = new TreeSet<>();
        for (Class<?> controlador : List.of(ProductoController.class, VentaController.class)) {
            RequestMapping base = AnnotatedElementUtils.findMergedAnnotation(controlador, RequestMapping.class);
            assertNotNull(base, controlador.getSimpleName() + " debe tener @RequestMapping");
            for (Method metodo : controlador.getDeclaredMethods()) {
                RequestMapping mapeo = AnnotatedElementUtils.findMergedAnnotation(metodo, RequestMapping.class);
                if (mapeo == null) {
                    continue;
                }
                String subruta = mapeo.path().length == 0 ? "" : mapeo.path()[0];
                for (RequestMethod http : mapeo.method()) {
                    operaciones.add(http.name() + " " + base.path()[0] + subruta);
                }
            }
        }
        return operaciones;
    }

    @Test
    void lasRutasYMetodosDelContratoSonExactamenteLosDeLosControladores() {
        assertEquals(operacionesImplementadas(), operacionesDocumentadas());
    }

    @Test
    void cadaOperacionDocumentaLasRespuestasQueRealmenteProduce() {
        Map<String, List<String>> esperadas = Map.of(
            "GET /api/v1/productos", List.of("200"),
            "GET /api/v1/productos/{codigo}", List.of("200", "404"),
            "POST /api/v1/ventas", List.of("201", "400", "404", "409", "415"),
            "GET /api/v1/ventas/{folio}", List.of("200", "404"));
        assertEquals(esperadas.keySet(), operacionesDocumentadas(), "la tabla de respuestas esperadas debe cubrir todas las operaciones");
        for (Map.Entry<String, List<String>> e : esperadas.entrySet()) {
            String[] partes = e.getKey().split(" ", 2);
            Map<String, Object> operacion = mapa(mapa(mapa(contrato.get("paths")).get(partes[1])).get(partes[0].toLowerCase(Locale.ROOT)));
            Set<String> documentadas = mapa(operacion.get("responses")).keySet().stream().map(String::valueOf).collect(Collectors.toSet());
            assertTrue(documentadas.containsAll(e.getValue()), e.getKey() + " debe documentar " + e.getValue() + " y documenta " + documentadas);
        }
    }

    @Test
    void elEsquemaErrorTieneLosCamposDeErrorResponse() {
        Map<String, Object> error = esquema("Error");
        assertEquals(componentes(ErrorResponse.class), mapa(error.get("properties")).keySet().stream().collect(Collectors.toCollection(TreeSet::new)));
        // detalles es el unico campo opcional
        Set<String> requeridos = lista(error.get("required")).stream().map(String::valueOf).collect(Collectors.toCollection(TreeSet::new));
        Set<String> esperados = componentes(ErrorResponse.class);
        esperados.remove("detalles");
        assertEquals(esperados, requeridos);
    }

    @Test
    void losCodigosDeErrorDelContratoSonLosDelEnumCodigoError() {
        Set<String> enContrato = lista(mapa(mapa(esquema("Error").get("properties")).get("codigo")).get("enum")).stream()
            .map(String::valueOf).collect(Collectors.toCollection(TreeSet::new));
        Set<String> enCodigo = Arrays.stream(CodigoError.values()).map(Enum::name).collect(Collectors.toCollection(TreeSet::new));
        assertEquals(enCodigo, enContrato);
    }

    @Test
    void losEsquemasDeDatosTienenLosCamposDeLosDto() {
        Map<String, Class<?>> pares = Map.of(
            "Producto", ProductoResponse.class,
            "Partida", PartidaResponse.class,
            "Venta", VentaResponse.class,
            "VentaRequest", VentaRequest.class,
            "ItemVentaRequest", ItemVentaRequest.class,
            "ErrorDetalle", ErrorResponse.Detalle.class);
        for (Map.Entry<String, Class<?>> par : pares.entrySet()) {
            Set<String> enContrato = mapa(esquema(par.getKey()).get("properties")).keySet().stream().collect(Collectors.toCollection(TreeSet::new));
            assertEquals(componentes(par.getValue()), enContrato, "esquema " + par.getKey() + " vs " + par.getValue().getSimpleName());
        }
    }
}
