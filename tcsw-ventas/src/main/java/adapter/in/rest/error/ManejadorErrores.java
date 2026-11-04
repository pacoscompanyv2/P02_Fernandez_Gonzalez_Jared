package adapter.in.rest.error;

import adapter.in.rest.dto.ErrorResponse;
import application.exception.ExistenciaInsuficienteException;
import application.exception.ProductoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// UN solo lugar donde cualquier excepcion se vuelve un ErrorResponse con el mismo formato.
// Extiende ResponseEntityExceptionHandler para atrapar tambien los errores del propio Spring MVC (405, 415, ruta inexistente...),
// que por defecto responderian con otro formato (ProblemDetail): handleExceptionInternal los uniforma.
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Object> productoNoEncontrado(ProductoNoEncontradoException ex, WebRequest request) {
        return responder(HttpStatus.NOT_FOUND, CodigoError.PRODUCTO_NO_ENCONTRADO, ex.getMessage(), request, null, null);
    }

    @ExceptionHandler(VentaNoEncontradaException.class)
    public ResponseEntity<Object> ventaNoEncontrada(VentaNoEncontradaException ex, WebRequest request) {
        return responder(HttpStatus.NOT_FOUND, CodigoError.VENTA_NO_ENCONTRADA, ex.getMessage(), request, null, null);
    }

    @ExceptionHandler(ExistenciaInsuficienteException.class)
    public ResponseEntity<Object> existenciaInsuficiente(ExistenciaInsuficienteException ex, WebRequest request) {
        return responder(HttpStatus.CONFLICT, CodigoError.EXISTENCIA_INSUFICIENTE, ex.getMessage(), request, null, null);
    }

    @ExceptionHandler(FolioDuplicadoException.class)
    public ResponseEntity<Object> folioDuplicado(FolioDuplicadoException ex, WebRequest request) {
        return responder(HttpStatus.CONFLICT, CodigoError.FOLIO_DUPLICADO, ex.getMessage(), request, null, null);
    }

    // reglas del dominio/aplicacion que se rompieron por datos del cliente (cantidad <= 0, folio vacio...)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> argumentoInvalido(IllegalArgumentException ex, WebRequest request) {
        return responder(HttpStatus.BAD_REQUEST, CodigoError.SOLICITUD_INVALIDA, ex.getMessage(), request, null, null);
    }

    // el estado del recurso no permite la operacion
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> estadoInvalido(IllegalStateException ex, WebRequest request) {
        return responder(HttpStatus.CONFLICT, CodigoError.CONFLICTO, ex.getMessage(), request, null, null);
    }

    // lo inesperado: se registra completo en el log del servidor, pero al cliente NO se le muestra la causa interna
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> inesperado(Exception ex, WebRequest request) {
        LOG.error("Error inesperado en la API", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, CodigoError.ERROR_INTERNO, "Error interno del servidor", request, null, null);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        List<ErrorResponse.Detalle> detalles = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new ErrorResponse.Detalle(e.getField(), e.getDefaultMessage()))
            .sorted(Comparator.comparing(ErrorResponse.Detalle::campo).thenComparing(ErrorResponse.Detalle::mensaje))
            .collect(Collectors.toList());
        return responder(HttpStatus.BAD_REQUEST, CodigoError.VALIDACION, "La solicitud no es valida", request, detalles, headers);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        return responder(HttpStatus.BAD_REQUEST, CodigoError.JSON_INVALIDO,
            "El cuerpo de la solicitud no es un JSON valido o tiene tipos incorrectos", request, null, headers);
    }

    // 404 de ruta, 405, 406, 415...: errores del framework con el mismo formato que los demas
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        switch (status) {
            case NOT_FOUND:
                return responder(status, CodigoError.RECURSO_NO_ENCONTRADO, "No existe el recurso solicitado", request, null, headers);
            case METHOD_NOT_ALLOWED:
                return responder(status, CodigoError.METODO_NO_PERMITIDO, "Metodo HTTP no permitido para esta ruta", request, null, headers);
            case UNSUPPORTED_MEDIA_TYPE:
                return responder(status, CodigoError.MEDIA_TYPE_NO_SOPORTADO, "Tipo de contenido no soportado: usa application/json", request, null, headers);
            default:
                return responder(status, CodigoError.SOLICITUD_INVALIDA, "Solicitud no valida", request, null, headers);
        }
    }

    private static ResponseEntity<Object> responder(HttpStatus status, CodigoError codigo, String mensaje, WebRequest request,
                                                    List<ErrorResponse.Detalle> detalles, HttpHeaders headers) {
        ErrorResponse cuerpo = new ErrorResponse(Instant.now().toString(), status.value(), status.getReasonPhrase(),
            codigo.name(), mensaje, rutaDe(request), detalles);
        ResponseEntity.BodyBuilder respuesta = ResponseEntity.status(status);
        if (headers != null) {
            respuesta.headers(headers);
        }
        return respuesta.body(cuerpo);
    }

    // getDescription(false) regresa "uri=/api/v1/..."
    private static String rutaDe(WebRequest request) {
        String descripcion = request.getDescription(false);
        return descripcion.startsWith("uri=") ? descripcion.substring(4) : descripcion;
    }
}
