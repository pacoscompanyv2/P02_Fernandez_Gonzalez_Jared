package application.exception;

// no alcanza el inventario para el pedido. Hereda de IllegalStateException (como antes) para no romper
// a quien ya la atrapaba; la API REST la distingue para responder 409. El texto del mensaje no cambia.
public class ExistenciaInsuficienteException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    private final String codigo;
    private final int disponible;
    private final int solicitado;

    public ExistenciaInsuficienteException(String codigo, int disponible, int solicitado) {
        super("Existencia insuficiente para " + codigo + ": disponible " + disponible + ", solicitado " + solicitado);
        this.codigo = codigo;
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getDisponible() {
        return disponible;
    }

    public int getSolicitado() {
        return solicitado;
    }
}
