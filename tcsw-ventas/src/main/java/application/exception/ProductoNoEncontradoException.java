package application.exception;

// el producto pedido no existe en el catalogo. Hereda de IllegalArgumentException para no romper
// a quien ya atrapaba esa excepcion (Swing, pruebas de P07/P08/C02); la API REST la distingue para responder 404.
public class ProductoNoEncontradoException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final String codigo;

    public ProductoNoEncontradoException(String codigo) {
        super("Producto no encontrado: " + codigo);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
