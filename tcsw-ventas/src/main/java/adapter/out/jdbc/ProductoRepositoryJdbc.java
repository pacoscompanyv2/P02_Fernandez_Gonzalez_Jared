package adapter.out.jdbc;

import application.port.out.ProductoRepository;
import domain.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// adaptador de salida: implementa el MISMO puerto que el de memoria, pero contra PostgreSQL.
// todo el SQL va parametrizado (PreparedStatement): ningun dato del usuario se concatena en una consulta.
public class ProductoRepositoryJdbc implements ProductoRepository {

    private static final String SQL_GUARDAR =
        "INSERT INTO producto (codigo, nombre, precio, existencia) VALUES (?, ?, ?, ?) "
        + "ON CONFLICT (codigo) DO UPDATE SET nombre = EXCLUDED.nombre, "
        + "precio = EXCLUDED.precio, existencia = EXCLUDED.existencia";
    private static final String SQL_BUSCAR =
        "SELECT codigo, nombre, precio, existencia FROM producto WHERE codigo = ?";
    private static final String SQL_LISTAR =
        "SELECT codigo, nombre, precio, existencia FROM producto ORDER BY codigo";

    private final ConexionJdbc conexion;

    public ProductoRepositoryJdbc(ConexionJdbc conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexion no puede ser nula");
        }
        this.conexion = conexion;
    }

    // upsert: si el codigo ya existe se actualiza (igual que el HashMap del repositorio en memoria)
    @Override
    public void guardar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(SQL_GUARDAR)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setBigDecimal(3, BigDecimal.valueOf(producto.getPrecio()));
            ps.setInt(4, producto.getExistencia());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo guardar el producto " + producto.getCodigo(), e);
        }
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(aProducto(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo buscar el producto " + codigo, e);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(aProducto(rs));
            }
            return productos;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo listar los productos", e);
        }
    }

    // mapeo fila -> objeto de dominio (NUMERIC(10,2) -> double, que es lo que usa Producto)
    private static Producto aProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getBigDecimal("precio").doubleValue(),
            rs.getInt("existencia"));
    }
}
