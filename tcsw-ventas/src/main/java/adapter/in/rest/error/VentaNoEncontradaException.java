package adapter.in.rest.error;

// GET /api/v1/ventas/{folio} con un folio que no existe -> 404 VENTA_NO_ENCONTRADA
public class VentaNoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VentaNoEncontradaException(String folio) {
        super("Venta no encontrada: " + folio);
    }
}
