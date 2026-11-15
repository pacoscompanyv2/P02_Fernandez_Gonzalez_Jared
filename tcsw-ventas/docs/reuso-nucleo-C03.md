# Reuso del nucleo - C03

Como la API y el cliente web reutilizan las reglas del dominio sin duplicarlas.

## Un solo punto de entrada
- `application.facade.VentasFacade` expone: `registrarProducto`, `listarProductos`, `buscarProducto`, `registrarVenta`, `buscarVenta`, `agregarObserver`
- El adaptador REST (`ProductoController`, `VentaController`) recibe la fachada en su constructor y no conoce los puertos de salida
- El adaptador Swing (`adapter/in/swing`: `Main`, `VentasController`) usa la misma fachada
- Endpoints REST: `GET /api/v1/productos`, `GET /api/v1/productos/{codigo}`, `POST /api/v1/ventas`, `GET /api/v1/ventas/{folio}`

## Las reglas viven solo en el dominio
- `domain/descuento`: `DescuentoStrategy`, `DescuentoPorMayoreo` (5 % si la venta tiene 3 o mas partidas), `SinDescuento` y `DescuentoStrategyFactory`
- `Venta.total` aplica el descuento y luego `Dinero.redondeadoEntero()`
- Comprobado en el navegador (14-nov): carrito de 3 productos con total estimado 599.00; la API registra la venta con total 569.00 (599 x 0.95 = 569.05, redondeado a 569.00). Una venta de 2 partidas (14 x 350 + 1 x 150) da 5,050.00 sin descuento

## El cliente no repite reglas de negocio
- `core/carrito.service.ts`: "ninguna regla de negocio (la API decide existencias, descuento y folios)"; solo evita pedir mas de la existencia que mostro el catalogo
- El total del carrito se muestra como "Total estimado ... el total final lo calcula la API y puede incluir descuentos"
- Declaracion honesta: sumar subtotales en el cliente es una estimacion de presentacion, no una segunda implementacion del descuento. Si se quisiera cero calculo en el cliente, el total estimado tendria que pedirse a la API; no se hizo

## Reglas de arquitectura que lo protegen (`arch/ArquitecturaRestTest`, 4 pruebas)
1. `domain` y `application` no dependen de Spring, Jackson ni validacion
2. Controladores, DTO y errores no conocen los adaptadores de salida
3. Los controladores entran por la fachada, no por los puertos de salida
4. Swing y REST son adaptadores independientes entre si

Estas pruebas corren dentro de `mvn clean verify`, que termino sin fallos el 14-nov (174 unitarias y 56 de integracion). El resultado individual de cada regla no se guardo: NO_VERIFICADO por separado.

## Manejo de errores compartido
- La API traduce las excepciones del nucleo a un formato unico con `codigo`: `VALIDACION`, `VENTA_NO_ENCONTRADA`, `EXISTENCIA_INSUFICIENTE`, `FOLIO_DUPLICADO` (`ManejadorErrores`)
- El cliente usa `describirError` para mostrar ese formato o, si no llega, `SIN_CONEXION`, `ERROR_HTTP` o `ERROR_DESCONOCIDO`
