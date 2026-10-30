package adapter.out.hibernate;

import application.port.out.VentaRepository;
import domain.DetalleVenta;
import domain.Producto;
import domain.Venta;
import jakarta.persistence.LockModeType;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

// segundo adaptador del puerto VentaRepository. guardar() es UNA transaccion con bloqueo pesimista (ver ADR-008).
public class VentaRepositoryHibernate implements VentaRepository {

    private static final String HQL_BUSCAR =
        "select v from VentaEntity v "
        + "left join fetch v.detalles d "
        + "left join fetch d.producto "
        + "where v.folio = :folio";

    private final SessionFactory fabrica;

    public VentaRepositoryHibernate(SessionFactory fabrica) {
        if (fabrica == null) {
            throw new IllegalArgumentException("La SessionFactory no puede ser nula");
        }
        this.fabrica = fabrica;
    }

    @Override
    public void guardar(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }
        EjecutorTransaccional.ejecutar(fabrica,
            "No se pudo guardar la venta " + venta.getFolio() + " (transaccion revertida)", sesion -> {

            // 1) total pedido por producto; TreeMap = orden por codigo, siempre el mismo -> sin interbloqueos entre ventas
            Map<String, Integer> pedido = new TreeMap<>();
            for (DetalleVenta partida : venta.getPartidas()) {
                pedido.merge(partida.getProducto().getCodigo(), partida.getCantidad(), Integer::sum);
            }

            // 2) se BLOQUEA cada producto (SELECT ... FOR UPDATE), se lee su existencia ACTUAL y se descuenta de forma relativa
            Map<String, ProductoEntity> bloqueados = new HashMap<>();
            for (Map.Entry<String, Integer> e : pedido.entrySet()) {
                ProductoEntity producto = sesion.find(ProductoEntity.class, e.getKey(), LockModeType.PESSIMISTIC_WRITE);
                if (producto == null) {
                    throw new RepositorioHibernateException("El producto " + e.getKey() + " no existe en la base");
                }
                if (producto.getExistencia() < e.getValue()) {
                    throw new RepositorioHibernateException("Existencia insuficiente para " + e.getKey()
                        + ": disponible " + producto.getExistencia() + ", solicitado " + e.getValue());
                }
                producto.setExistencia(producto.getExistencia() - e.getValue());
                bloqueados.put(e.getKey(), producto);
            }

            // 3) venta + partidas (cascade); el precio unitario es el del momento de la venta
            VentaEntity entidad = new VentaEntity(venta.getFolio(),
                ProductoEntity.aPrecio(venta.calcularTotal().valor()), venta.isCerrada());
            int linea = 1;
            for (DetalleVenta partida : venta.getPartidas()) {
                Producto p = partida.getProducto();
                entidad.agregarDetalle(new DetalleVentaEntity(entidad, linea++, bloqueados.get(p.getCodigo()),
                    partida.getCantidad(), ProductoEntity.aPrecio(p.getPrecio())));
            }
            sesion.persist(entidad);
            return null;   // el commit (y con el, los INSERT/UPDATE) ocurre en EjecutorTransaccional
        });
    }

    @Override
    public Optional<Venta> buscarPorFolio(String folio) {
        return EjecutorTransaccional.leer(fabrica, "No se pudo buscar la venta " + folio, sesion -> {
            VentaEntity entidad = sesion.createQuery(HQL_BUSCAR, VentaEntity.class)
                .setParameter("folio", folio)
                .uniqueResult();
            return Optional.ofNullable(entidad).map(VentaRepositoryHibernate::reconstruir);
        });
    }

    // misma tecnica que VentaRepositoryJdbc (ver ADR-006): Venta no tiene constructor "desde datos" y agregarPartida
    // DESCUENTA stock; cada partida usa un Producto con (existencia actual + cantidad) para que no se descuente de nuevo.
    // Limitacion: usa el descuento por defecto (mayoreo).
    private static Venta reconstruir(VentaEntity entidad) {
        Venta venta = new Venta(entidad.getFolio());
        List<DetalleVentaEntity> ordenadas = new ArrayList<>(entidad.getDetalles());
        ordenadas.sort(Comparator.comparingInt(d -> d.getId().getLinea()));
        for (DetalleVentaEntity d : ordenadas) {
            ProductoEntity p = d.getProducto();
            Producto historico = new Producto(p.getCodigo(), p.getNombre(),
                d.getPrecioUnitario().doubleValue(), p.getExistencia() + d.getCantidad());
            venta.agregarPartida(historico, d.getCantidad());
        }
        if (entidad.isCerrada()) {
            venta.cerrar();
        }
        return venta;
    }
}
