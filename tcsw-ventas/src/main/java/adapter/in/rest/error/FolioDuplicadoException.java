package adapter.in.rest.error;

// POST /api/v1/ventas con un folio que ya existe -> 409 FOLIO_DUPLICADO
public class FolioDuplicadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FolioDuplicadoException(String folio) {
        super("Ya existe una venta con el folio " + folio);
    }
}
