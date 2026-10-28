# ADR-009: MVC en Swing y consulta de ventas (C02)

**Estado:** aceptada (C02)  **Equipo:** TCSW-E01

## Contexto
El cliente Swing de P07 ya separaba vista y controlador, pero solo permitia vender. C02 pide Swing/MVC completo contra persistencia real: hace falta poder LEER lo guardado (volver a ver una venta despues de reiniciar).

## Decision
| Rol MVC | Clase | Responsabilidad |
|---|---|---|
| Vista | `CatalogoPanel`, `CarritoPanel`, `ConsultaVentaPanel`, `MainFrame` (pestanas) | pintar y capturar; NO conocen repositorios |
| Modelo de tabla | `ProductoTableModel`, `CarritoTableModel`, `PartidasTableModel` | adaptan datos del dominio a `JTable` |
| Controlador | `VentasController` | valida entradas, traduce errores a mensajes, conserva el carrito |
| Modelo / casos de uso | `VentasFacade` -> `RegistrarVentaService`, puertos `*Repository` | reglas y persistencia detras de interfaces |

- Nueva operacion `VentasFacade.buscarVenta(folio)` (solo lectura, delega al puerto `VentaRepository`).
- Nueva operacion `VentasController.consultarVenta(folio)`: folio vacio o inexistente -> `IllegalArgumentException` con mensaje para la vista.
- Una venta registrada por `RegistrarVentaService` se **cierra** (`venta.cerrar()`) antes de guardarse: en P08 quedaba `cerrada=false` en la base aunque ya estaba pagada. Una venta sin partidas ahora se rechaza.

## Consecuencias
- (+) La vista nunca importa `adapter.out.*`; funciona igual con memoria, JDBC o Hibernate.
- (+) El estado `cerrada` de la base coincide con la realidad del negocio.
- (-) La consulta reconstruye la venta con el descuento por defecto (limitacion heredada de ADR-006).
