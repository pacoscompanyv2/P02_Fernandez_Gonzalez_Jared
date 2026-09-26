package domain.descuento;

public class DescuentoStrategyFactory {

    public static DescuentoStrategy crear(String tipo) {
        switch (tipo) {
            case "MAYOREO":
                return new DescuentoPorMayoreo();
            case "NINGUNO":
                return new SinDescuento();
            default:
                throw new IllegalArgumentException("Tipo de descuento desconocido: " + tipo);
        }
    }
}