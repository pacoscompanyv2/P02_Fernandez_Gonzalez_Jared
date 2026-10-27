package adapter.out.hibernate;

import application.port.out.ProductoRepository;
import domain.Producto;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// segundo adaptador del MISMO puerto ProductoRepository (el primero es ProductoRepositoryJdbc). ver ADR-007.
// Hibernate arma el SQL a partir de las entidades; los parametros siempre van enlazados (no se concatena nada).
public class ProductoRepositoryHibernate implements ProductoRepository {

    private final SessionFactory fabrica;

    public ProductoRepositoryHibernate(SessionFactory fabrica) {
        if (fabrica == null) {
            throw new IllegalArgumentException("La SessionFactory no puede ser nula");
        }
        this.fabrica = fabrica;
    }

    // upsert, igual que el repositorio JDBC y el de memoria: si el codigo existe se actualiza
    @Override
    public void guardar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        EjecutorTransaccional.ejecutar(fabrica, "No se pudo guardar el producto " + producto.getCodigo(), sesion -> {
            ProductoEntity existente = sesion.find(ProductoEntity.class, producto.getCodigo());
            if (existente == null) {
                sesion.persist(ProductoEntity.desde(producto));
            } else {
                existente.copiarDe(producto);   // entidad "managed": Hibernate emite el UPDATE al hacer commit
            }
            return null;
        });
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return EjecutorTransaccional.leer(fabrica, "No se pudo buscar el producto " + codigo, sesion -> {
            ProductoEntity entidad = sesion.find(ProductoEntity.class, codigo);
            return Optional.ofNullable(entidad).map(ProductoEntity::aDominio);
        });
    }

    @Override
    public List<Producto> listarTodos() {
        return EjecutorTransaccional.leer(fabrica, "No se pudo listar los productos", sesion ->
            sesion.createQuery("from ProductoEntity p order by p.codigo", ProductoEntity.class)
                .getResultList().stream().map(ProductoEntity::aDominio).collect(Collectors.toList()));
    }
}
