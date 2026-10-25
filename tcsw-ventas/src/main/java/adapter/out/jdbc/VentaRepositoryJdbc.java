package adapter.out.jdbc;

import application.port.out.VentaRepository;
import domain.DetalleVenta;
import domain.Producto;
import domain.Venta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

// adaptador de salida de ventas. guardar() es UNA transaccion: o se guarda todo (venta + partidas + existencias)
// o no se guarda nada. ver ADR-006.
public class VentaRepositoryJdbc implements VentaRepository {

    private static final String SQL_INSERTAR_VENTA =
        "INSERT INTO venta (folio, total, cerrada) VALUES (?, ?, ?)";
    private static final String SQL_ACTUALIZAR_EXISTENCIA =
        "UPDATE producto SET existencia = ? WHERE codigo = ?";
    private static final String SQL_INSERTAR_DETALLE =
        "INSERT INTO detalle_venta (folio, linea, codigo, cantidad, precio_unitario) VALUES (?, ?, ?, ?, ?)";
    // una sola consulta (un solo snapshot): venta + partidas + datos del producto
    private static final String SQL_BUSCAR =
        "SELECT v.cerrada, d.codigo, p.nombre, d.precio_unitario, p.existencia, d.cantidad "
        + "FROM venta v "
        + "LEFT JOIN detalle_venta d ON d.folio = v.folio "
        + "LEFT JOIN producto p ON p.codigo = d.codigo "
        + "WHERE v.folio = ? ORDER BY d.linea";

    private final ConexionJdbc conexion;

    public VentaRepositoryJdbc(ConexionJdbc conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexion no puede ser nula");
        }
        this.conexion = conexion;
    }

    @Override
    public void guardar(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }
        try (Connection c = conexion.abrir()) {
            c.setAutoCommit(false);
            try {
                insertarVenta(c, venta);
                actualizarExistencias(c, venta);
                insertarDetalles(c, venta);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                revertir(c, e);
                throw e;
            }
        } catch (SQLException e) {
            throw new RepositorioException(
                "No se pudo guardar la venta " + venta.getFolio() + " (transaccion revertida)", e);
        }
    }

    @Override
    public Optional<Venta> buscarPorFolio(String folio) {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, folio);
            try (ResultSet rs = ps.executeQuery()) {
                return reconstruir(folio, rs);
            }
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo buscar la venta " + folio, e);
        }
    }

    // ---------- pasos de la transaccion de guardar ----------

    private static void insertarVenta(Connection c, Venta venta) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERTAR_VENTA)) {
            ps.setString(1, venta.getFolio());
            ps.setBigDecimal(2, BigDecimal.valueOf(venta.calcularTotal().valor()));
            ps.setBoolean(3, venta.isCerrada());
            ps.executeUpdate();
        }
    }

    // el dominio ya descontó el stock EN MEMORIA (Venta.agregarPartida); aqui se refleja en la base.
    // un producto que aparece en varias partidas es la misma instancia, asi que se actualiza una vez.
    private static void actualizarExistencias(Connection c, Venta venta) throws SQLException {
        Map<String, Producto> distintos = new LinkedHashMap<>();
        for (DetalleVenta partida : venta.getPartidas()) {
            distintos.put(partida.getProducto().getCodigo(), partida.getProducto());
        }
        try (PreparedStatement ps = c.prepareStatement(SQL_ACTUALIZAR_EXISTENCIA)) {
            for (Producto producto : distintos.values()) {
                ps.setInt(1, producto.getExistencia());
                ps.setString(2, producto.getCodigo());
                if (ps.executeUpdate() != 1) {
                    // 0 filas = el producto no existe en la base: se aborta TODA la venta
                    throw new RepositorioException("El producto " + producto.getCodigo() + " no existe en la base");
                }
            }
        }
    }

    private static void insertarDetalles(Connection c, Venta venta) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERTAR_DETALLE)) {
            int linea = 1;
            for (DetalleVenta partida : venta.getPartidas()) {
                ps.setString(1, venta.getFolio());
                ps.setInt(2, linea++);
                ps.setString(3, partida.getProducto().getCodigo());
                ps.setInt(4, partida.getCantidad());
                ps.setBigDecimal(5, BigDecimal.valueOf(partida.getProducto().getPrecio()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // si el rollback mismo falla no se pierde el error original: queda como "suppressed"
    private static void revertir(Connection c, Exception causa) {
        try {
            c.rollback();
        } catch (SQLException ex) {
            causa.addSuppressed(ex);
        }
    }

    // ---------- lectura ----------

    // Venta no tiene constructor "desde datos" y agregarPartida DESCUENTA stock. para no tocar el dominio,
    // cada partida se reconstruye con un Producto cuya existencia es (existencia actual + cantidad):
    // al agregar la partida vuelve a quedar en la existencia actual de la base. Limitacion: usa el descuento
    // por defecto (mayoreo), no el que se uso al vender. ver ADR-006.
    private static Optional<Venta> reconstruir(String folio, ResultSet rs) throws SQLException {
        Venta venta = null;
        boolean cerrada = false;
        while (rs.next()) {
            if (venta == null) {
                venta = new Venta(folio);
                cerrada = rs.getBoolean("cerrada");
            }
            String codigo = rs.getString("codigo");
            if (codigo != null) {
                int cantidad = rs.getInt("cantidad");
                Producto historico = new Producto(codigo, rs.getString("nombre"),
                    rs.getBigDecimal("precio_unitario").doubleValue(), rs.getInt("existencia") + cantidad);
                venta.agregarPartida(historico, cantidad);
            }
        }
        if (venta != null && cerrada) {
            venta.cerrar();
        }
        return Optional.ofNullable(venta);
    }
}
