package adapter.in.rest.controller;

import adapter.in.rest.dto.ProductoResponse;
import application.exception.ProductoNoEncontradoException;
import application.facade.VentasFacade;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

// adaptador de entrada: traduce HTTP <-> casos de uso. No tiene reglas de negocio: todo lo decide VentasFacade.
@RestController
@RequestMapping(path = "/api/v1/productos", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductoController {

    private final VentasFacade facade;

    public ProductoController(VentasFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return facade.listarProductos().stream().map(ProductoResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/{codigo}")
    public ProductoResponse obtener(@PathVariable("codigo") String codigo) {
        return facade.buscarProducto(codigo)
            .map(ProductoResponse::from)
            .orElseThrow(() -> new ProductoNoEncontradoException(codigo));
    }
}
