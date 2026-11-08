package adapter.in.rest.controller;

import adapter.in.rest.dto.ItemVentaRequest;
import adapter.in.rest.dto.VentaRequest;
import adapter.in.rest.dto.VentaResponse;
import adapter.in.rest.error.FolioDuplicadoException;
import adapter.in.rest.error.VentaNoEncontradaException;
import application.facade.VentasFacade;
import application.port.in.ItemVenta;
import domain.Venta;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

// adaptador de entrada de ventas: HTTP <-> VentasFacade. La forma del JSON se valida en el DTO (@Valid);
// existencias, productos y descuento los decide el caso de uso. Los errores los traduce ManejadorErrores.
@RestController
@RequestMapping(path = "/api/v1/ventas", produces = MediaType.APPLICATION_JSON_VALUE)
public class VentaController {

    private final VentasFacade facade;

    public VentaController(VentasFacade facade) {
        this.facade = facade;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest solicitud) {
        String folio = solicitud.folio().trim();
        // el folio es la llave de la venta: repetirlo es un conflicto (409), no un error del servidor
        if (facade.buscarVenta(folio).isPresent()) {
            throw new FolioDuplicadoException(folio);
        }
        List<ItemVenta> items = solicitud.items().stream()
            .map(VentaController::aItem)
            .collect(Collectors.toList());
        Venta venta = facade.registrarVenta(folio, items);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{folio}")
            .buildAndExpand(venta.getFolio())
            .toUri();
        return ResponseEntity.created(ubicacion).body(VentaResponse.from(venta));
    }

    @GetMapping("/{folio}")
    public VentaResponse obtener(@PathVariable("folio") String folio) {
        return facade.buscarVenta(folio)
            .map(VentaResponse::from)
            .orElseThrow(() -> new VentaNoEncontradaException(folio));
    }

    private static ItemVenta aItem(ItemVentaRequest item) {
        return new ItemVenta(item.codigoProducto().trim(), item.cantidad());
    }
}
