package application.port.in;

import domain.Venta;
import java.util.List;

// puerto de entrada: asi le pide el mundo exterior que registre una venta
public interface RegistrarVentaUseCase {
    Venta registrar(String folio, List<ItemVenta> items);
}