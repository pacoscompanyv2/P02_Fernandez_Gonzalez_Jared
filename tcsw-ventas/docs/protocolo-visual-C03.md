# Protocolo visual C03 - capturas que respaldan el documento

Las capturas viven en el documento de entrega (`C03_EQUIPO_01.docx`), no en el repositorio.

| # | Captura | Que debe verse | Estado |
|---|---|---|---|
| 1 | `verificar-c03.sh` completo (18-nov) | `commit: 9170d28`, 174 y 56 pruebas, 21 comprobaciones, 51 pruebas, 9 comprobaciones, `5 pasos, 0 fallas` | VERIFICADO |
| 2 | Persistencia: fase B de `verificar-p09.sh` | venta 201 CERRADA, existencia 18, tras reiniciar la API la venta sigue 200 CERRADA y la existencia en 18, 409, `cerrada = t` en PostgreSQL | VERIFICADO |
| 3 | Catalogo con productos | P1, P2, P3 con precio y existencia (datos de la API por el proxy) | VERIFICADO |
| 4 | Carrito con tres productos | total estimado 599.00 y la leyenda de que el total final lo calcula la API | VERIFICADO |
| 5 | Venta registrada | venta 1 CERRADA con total 569.00 (descuento por mayoreo aplicado por la API) | VERIFICADO |
| 6 | Consulta del folio 1 | estado, total y partidas | VERIFICADO |
| 7 | Segunda venta | 14 x P2 y 1 x P1, total 5,050.00 (dos partidas, sin descuento) | VERIFICADO |
| 8 | Catalogo sin existencia | P2 con existencia 0 y "Agregar" deshabilitado | VERIFICADO |
| 9 | Carrito vacio en Venta | "No hay nada que vender. Ir al catalogo" | VERIFICADO |
| 10 | Busqueda sin resultados | "No hay productos que coinciden" | VERIFICADO |
| 11 | Consulta con folio vacio | `FOLIO_VACIO: Escribe el folio de la venta.` | VERIFICADO |
| 12 | Consulta de folio inexistente | `VENTA_NO_ENCONTRADA: Venta no encontrada: 67` | VERIFICADO |
| 13 | Hallazgo (14-nov) | `ERROR_HTTP: La API respondio 500` con la API apagada | VERIFICADO como hallazgo |
| 14 | Correccion (18-nov) | `SIN_CONEXION: No se pudo conectar con la API. Revisa que este en marcha en el puerto 8080.` con boton Reintentar | VERIFICADO |
| 15 | Issue #52 y PR #53 | issue cerrado; PR fusionado (sin revision de otro integrante) | PENDIENTE de captura |
| 16 | `git log --oneline` con la etiqueta `v0.3-web` | etiqueta sobre el commit final | PENDIENTE |

Nota: el 409 `EXISTENCIA_INSUFICIENTE` no se captura en pantalla porque el cliente impide pedir mas de la existencia (captura 8); esta cubierto en `verificar-p09.sh`.

Reglas: sin `.env`, sin claves, sin tokens; cerrar o tapar las pestanas ajenas del navegador antes de capturar.
