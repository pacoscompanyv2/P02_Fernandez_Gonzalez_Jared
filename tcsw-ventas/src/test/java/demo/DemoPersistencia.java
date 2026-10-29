package demo;

import adapter.out.hibernate.RepositoriosHibernateFactory;
import adapter.out.jdbc.RepositoriosFactory;
import application.facade.VentasFacade;
import application.port.in.ItemVenta;
import application.port.out.ProductoRepository;
import application.port.out.VentaRepository;
import domain.DetalleVenta;
import domain.Producto;
import domain.Venta;

import java.util.List;
import java.util.Optional;

// demostracion SIN ventana para la evidencia "antes/despues": se ejecuta en DOS corridas de JVM distintas.
//   sembrar   -> registra una venta de 2 unidades de P1 y muestra existencias ANTES y DESPUES
//   consultar -> en otro proceso (tras reiniciar PostgreSQL) lee existencias y la venta guardada
// el adaptador sale de TCSW_PERSISTENCIA (hibernate) o, si no esta, JDBC. No imprime URL, usuario ni clave.
public final class DemoPersistencia {

    private static final String FOLIO = "DEMO-C02-1";

    private DemoPersistencia() {
    }

    public static void main(String[] args) {
        String modo = args.length > 0 ? args[0] : "";
        boolean hibernate = RepositoriosHibernateFactory.seleccionado();
        ProductoRepository productos = hibernate ? RepositoriosHibernateFactory.productos() : RepositoriosFactory.productos();
        VentaRepository ventas = hibernate ? RepositoriosHibernateFactory.ventas() : RepositoriosFactory.ventas();
        System.out.println("Adaptador: " + (hibernate ? "Hibernate" : "JDBC") + "   Modo: " + modo);
        VentasFacade facade = new VentasFacade(productos, ventas);

        int codigo = 0;
        if ("sembrar".equals(modo)) {
            codigo = sembrar(facade);
        } else if ("consultar".equals(modo)) {
            codigo = consultar(facade);
        } else {
            System.out.println("Uso: DemoPersistencia sembrar|consultar");
            codigo = 2;
        }
        System.exit(codigo);
    }

    private static int sembrar(VentasFacade facade) {
        if (!facade.buscarProducto("P1").isPresent()) {
            facade.registrarProducto(new Producto("P1", "Playera Deportiva", 150.0, 20));
        }
        System.out.println("ANTES   : P1 existencia=" + existencia(facade));
        facade.registrarVenta(FOLIO, List.of(new ItemVenta("P1", 2)));
        System.out.println("DESPUES : P1 existencia=" + existencia(facade) + "  (venta " + FOLIO + " registrada)");
        return 0;
    }

    private static int consultar(VentasFacade facade) {
        System.out.println("TRAS REINICIAR: P1 existencia=" + existencia(facade));
        Optional<Venta> venta = facade.getVentaRepository().buscarPorFolio(FOLIO);
        if (!venta.isPresent()) {
            System.out.println("La venta " + FOLIO + " NO se encontro");
            return 1;
        }
        Venta v = venta.get();
        System.out.println("Venta " + v.getFolio() + " encontrada: cerrada=" + v.isCerrada()
            + " total=" + v.calcularTotal().valor() + " partidas=" + v.getPartidas().size());
        for (DetalleVenta d : v.getPartidas()) {
            System.out.println("  - " + d.getProducto().getCodigo() + " x" + d.getCantidad());
        }
        return 0;
    }

    private static int existencia(VentasFacade facade) {
        return facade.buscarProducto("P1").map(Producto::getExistencia).orElse(-1);
    }
}
