# Indice de evidencias - P10 Cliente Angular (equipo TCSW-E01)

Estados: VERIFICADO = hay evidencia; NO_VERIFICADO = no hay evidencia; PENDIENTE = falta hacerlo. Documento completo con capturas: `P10_EQUIPO_01.docx` (Eminus).

## Mapa criterio R01 -> evidencia

| Criterio (pts) | Evidencia | Estado |
|---|---|---|
| Funcionamiento (0.8) | `cliente-angular/scripts/verificar-p10.sh`: 9 comprobaciones, 0 fallas a traves del proxy de Angular (pagina, catalogo, 404, venta 201, consulta 200, 409 por folio repetido, 409 por existencia, 400 sin partidas, 404 por folio inexistente); `docs/evidencia/verificar-p10-salida.txt`; recorrido en el navegador de los tres integrantes (capturas en el Word) | VERIFICADO. Excepcion: el mensaje de "sin conexion" en el navegador es NO_VERIFICADO (solo pruebas unitarias) |
| Solucion tecnica (0.5) | ADR-011 (Angular 21, componentes independientes, signals, rutas diferidas, proxy de desarrollo en lugar de CORS); `core/` con modelos, `describirError` y servicios HTTP; la API no se modifico | VERIFICADO |
| Pruebas (0.3) | 49 pruebas en 9 archivos (Vitest con jsdom y `HttpTestingController`): `api-error` 7, `carrito.service` 11, `productos.service` 4, `ventas.service` 3, `consulta` 4, `app` 2, `catalogo` 5, `carrito` 5, `venta` 8. Corrida final sobre `main` (`8cdef29`): 9 archivos y 49 pruebas en verde, `ng build` sin errores | VERIFICADO (unitarias y de componente). NO_VERIFICADO: cobertura (no se midio) y pruebas de extremo a extremo en navegador real |
| Calidad y Sonar (0.2) | Reporte de Sonar del cliente y decision sobre cada hallazgo | PENDIENTE |
| Git y evidencia (0.2) | 3 issues (#46, #48, #50), 3 PR (#47, #49, #51), 3 `integrante-N-P10.md`, bloque P10 en `README-reproduccion.md`, etiqueta de cierre | VERIFICADO con las limitaciones de la seccion siguiente. Etiqueta: PENDIENTE hasta crearla |

## Trazabilidad por integrante

| Integrante | Issue | Commits | PR | Revision |
|---|---|---|---|---|
| 1 Jared | #46 | `0c5fb0c`, `59a2ed8` | #47 (integra Suriel, merge `bf5d1b0`) | sin comentario de revision en GitHub: NO_VERIFICADO |
| 2 Suriel | #48 | `a214b41`, `e7bc562` | #49 (integra Jesus, merge `2aed823`) | sin comentario de revision en GitHub: NO_VERIFICADO |
| 3 Jesus | #50 | `ac33f16`, `9d7cf48` | #51 (integra Jared, merge `8cdef29`) | Jared aparecio solo como revisor sugerido, no se solicito revision; sin comentario: NO_VERIFICADO |

Pruebas por rama antes de integrar: integrante 1, 25; integrante 2, 37; integrante 3, 49 (ya con todo integrado).

## Verificacion final sobre main (12-nov)
- `main` en `8cdef29`: `ng build` sin errores y `ng test` con 9 archivos y 49 pruebas en verde.
- `verificar-p10.sh`: 9 comprobaciones, 0 fallas. La corrida que queda guardada es la del script corregido (commit `23c7df2`): termina sola en unos 5 segundos y deja libres los puertos 4200 y 8080.

## Limitaciones
- El commit `ac33f16` no lleva el numero de issue (su mensaje termina en `(#)`) y el titulo del PR #51 tampoco lo lleva: trazabilidad incompleta en esa parte
- Ningun PR de P10 tiene comentario de revision en GitHub
- El commit `23c7df2` (correccion de la limpieza de procesos de `verificar-p10.sh`) se hizo directo a `main`, sin issue, PR ni revision; lo dice su mensaje
- La documentacion de cierre se subio directamente a `main`, igual que en C02 y P09
- "Sin conexion" solo esta probado con pruebas unitarias; en el navegador no se capturo (el servidor de desarrollo estaba apagado y el navegador mostro su propio error)
- No se midio cobertura del cliente y no hay pruebas de extremo a extremo con navegador real
- El proxy solo existe con `ng serve`; no hay despliegue de produccion
