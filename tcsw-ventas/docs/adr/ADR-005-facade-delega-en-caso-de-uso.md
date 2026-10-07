# ADR-005: La fachada delega el registro de ventas en el caso de uso

## Estado
Aceptado. Actualiza lo descrito en ADR-003 sobre el cableado interno de la fachada.

## Contexto
Al integrar el cliente Swing, `VentasFacade` era el unico punto de entrada que usaria el controlador.
En ese momento `VentasFacade.registrarVenta(String, List<ItemVenta>)` construia la `Venta` por su cuenta
con descuento por mayoreo fijo y no pasaba por `RegistrarVentaService`. Consecuencias:
- los observers del caso de uso (Observer, ADR-002) no se notificaban al vender desde la interfaz;
- la estrategia de descuento (ADR-001) no era configurable desde fuera;
- la validacion de existencias quedaba repartida: `Venta.agregarPartida` descuenta por linea, de modo que si la segunda linea no tenia existencia, la primera ya se habia descontado.

## Decision
1. `VentasFacade.registrarVenta` delega en `RegistrarVentaService.registrar`. El constructor de dos argumentos arma el servicio por defecto; el de tres lo recibe inyectado. La fachada expone `agregarObserver`.
2. `RegistrarVentaService` recibe un `DescuentoStrategy` por constructor (el de dos argumentos conserva el descuento por mayoreo).
3. El servicio valida todo antes de modificar inventario: producto existente, cantidad positiva y existencia suficiente (sumando lineas repetidas del mismo producto). Solo despues crea la venta y descuenta.
4. El controlador hace una validacion temprana al agregar al carrito (lo ya agregado mas lo nuevo no puede superar la existencia) para dar retroalimentacion inmediata en la interfaz.

## Alternativas consideradas
- Dejar la logica en la fachada: se descarto porque duplica el caso de uso y evita Observer y Strategy.
- Compensar el descuento (revertir) si falla una linea: se descarto porque es mas fragil que validar antes de mutar.
- Validar solo en el controlador: se descarto porque la regla pertenece al caso de uso; el controlador solo la anticipa.

## Consecuencias
- Un solo camino para registrar ventas: observers, descuento y validacion se aplican igual desde cualquier consumidor.
- El registro es atomico respecto a existencias dentro del flujo normal.
- Limite conocido: el repositorio en memoria no es transaccional ni seguro ante concurrencia; dos registros simultaneos podrian competir por la misma existencia. No se resuelve aqui.
- Se elimino `VentasFacade.registrarVenta(Venta)`, que solo se usaba internamente.
- Evidencia: `RegistrarVentaServiceTest` (inventario atomico, lineas repetidas, estrategia inyectada), `VentasFacadeTest` (observers, rechazo sin descuento), `VentasControllerTest` (existencia y carrito conservado).
