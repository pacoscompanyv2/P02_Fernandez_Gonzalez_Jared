# Protocolo visual C03 - capturas que respaldan el documento

Las capturas viven en el documento de entrega (`C03_EQUIPO_01.docx`), no en el repositorio.

| # | Captura | Quien | Que debe verse | Estado |
|---|---|---|---|---|
| 1 | `verificar-c03.sh` completo | Integrante 1 | `5 pasos, 0 fallas` | VERIFICADO |
| 2 | Catalogo con productos | Integrante 1 | P1, P2, P3 con precio y existencia (datos de la API por el proxy) | VERIFICADO |
| 3 | Carrito con tres productos | Integrante 1 | total estimado 599.00 y la leyenda de que el total final lo calcula la API | VERIFICADO |
| 4 | Venta registrada | Integrante 1 | venta 1 CERRADA con total 569.00 (descuento por mayoreo aplicado por la API) | VERIFICADO |
| 5 | Consulta del folio 1 | Integrante 1 | estado, total y partidas | VERIFICADO |
| 6 | Segunda venta | Integrante 1 | venta 2 con 14 x P2 y 1 x P1, total 5,050.00 (dos partidas, sin descuento) | VERIFICADO |
| 7 | Catalogo sin existencia | Integrante 1 | P2 con existencia 0 y boton "Agregar" deshabilitado | VERIFICADO |
| 8 | Carrito vacio en Venta | Integrante 1 | "No hay nada que vender. Ir al catalogo" | VERIFICADO |
| 9 | Busqueda sin resultados | Integrante 1 | "No hay productos que coinciden" | VERIFICADO |
| 10 | Consulta con folio vacio | Integrante 1 | `FOLIO_VACIO: Escribe el folio de la venta.` | VERIFICADO |
| 11 | Consulta de folio inexistente | Integrante 1 | `VENTA_NO_ENCONTRADA: Venta no encontrada: 67` | VERIFICADO |
| 12 | Hallazgo: API apagada | Integrante 1 | `ERROR_HTTP: La API respondio 500` (evidencia del hueco, antes de la correccion) | VERIFICADO como hallazgo |
| 13 | "Sin conexion" corregido | Integrante 2 | mensaje `SIN_CONEXION` con la API apagada | PENDIENTE |
| 14 | Issue, PR y revision de la correccion | Integrantes 2 y 3 | `Closes #N`, comentario de revision de Jesus | PENDIENTE |
| 15 | Formulario de venta con folio de solo espacios | Integrante 3 | mensaje de validacion | PENDIENTE |
| 16 | `verificar-c03.sh` en las maquinas de los integrantes 2 y 3 | Integrantes 2 y 3 | `5 pasos, 0 fallas` | PENDIENTE |
| 17 | Issue #52 y su PR | Integrante 1 | `Closes #52`, revision de un companero | PENDIENTE |
| 18 | `git log --oneline` y etiqueta `v0.3-web` | Integrante 1 | etiqueta sobre el commit final | PENDIENTE |

Nota: el 409 `EXISTENCIA_INSUFICIENTE` no se captura en pantalla porque el cliente impide pedir mas de la existencia (captura 7); esta cubierto en `verificar-p09.sh`.

Reglas: sin `.env`, sin claves, sin tokens; cerrar o tapar las pestanas ajenas del navegador antes de capturar.
