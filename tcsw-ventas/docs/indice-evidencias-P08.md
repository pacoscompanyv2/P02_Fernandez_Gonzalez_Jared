# Indice de evidencias - P08 Persistencia (R01)

Equipo: TCSW-E01 (entrega `P08_EQUIPO_01`)
Integrantes: Jared Fernandez Gonzalez (pacoscompanyv2), Suriel Francisco Tecpile (SurielFrancisco), Jesus Armando Andres Tablilla (JesusAndres765)
Repositorio: https://github.com/pacoscompanyv2/P02_Fernandez_Gonzalez_Jared
Cierre: `main` en `ac92607` (merge del PR #32). Etiqueta final: `v0.3-persistencia`, que se crea sobre el commit que contiene esta documentacion; el hash se registra en el envio de Eminus.
Estados usados: VERIFICADO = con evidencia; NO_VERIFICADO = no se ejecuto o no hay evidencia; PENDIENTE = falta hacerlo.

## Resumen por criterio de R01

| Criterio | Pts | Evidencia | Donde | Estado |
|---|---|---|---|---|
| Funcionamiento | 0.8 | La aplicacion Swing guarda en PostgreSQL 16: venta F-001 (P1 x 2, total 300.00), existencia de P1 de 20 a 18, el dato sigue despues de cerrar y reabrir; un folio repetido se rechaza con "transaccion revertida" y la existencia no cambia; filas reales en `producto`, `venta` y `detalle_venta` | `docs/protocolo-visual-P08.md`, capturas del documento final | VERIFICADO (recorrido en el equipo de Jesus, 2026-10-24) |
| Funcionamiento (reproduccion en otro equipo) | - | Repetir el recorrido en la maquina de Jared | `docs/protocolo-visual-P08.md` (seccion final) | NO_VERIFICADO |
| Solucion tecnica | 0.5 | Puertos `ProductoRepository` y `VentaRepository` conservados; adaptadores JDBC en `adapter.out.jdbc`; `PreparedStatement` en todo el SQL; `guardar(Venta)` en una sola transaccion (commit / rollback); esquema con `PRIMARY KEY`, `FOREIGN KEY` y `CHECK`; configuracion por variables de entorno; `ArquitecturaPersistenciaTest` impide `java.sql` fuera de `adapter.out.jdbc` | `docs/adr/ADR-006-persistencia-jdbc-en-adaptadores.md`, `db/schema.sql`, `adapter/out/jdbc/*` | VERIFICADO |
| Pruebas | 0.3 | 101 unitarias + 27 de integracion contra PostgreSQL 16, 0 fallos, 0 errores, BUILD SUCCESS en `main` (`ac92607`). Casos de error: existencia negativa (23514), codigo demasiado largo (22001), folio duplicado (23505), producto inexistente con rollback total, SQL inyectado como texto, existencia insuficiente por la fachada | salida de `mvn clean verify -Pintegracion` y `./scripts/resumen-pruebas.sh`, `src/test/java/**` | VERIFICADO |
| Calidad y Sonar | 0.2 | Proyecto `P08-VENTAS`, Quality Gate Passed, Overall Code: seguridad 0, fiabilidad 0, mantenibilidad 15 issues, cobertura 72.8% (576 lineas por cubrir), duplicacion 0.0%, 1.3k lineas de codigo | captura de Sonar (Overall Code) en el documento final | VERIFICADO (los 15 issues no se atendieron: ver limites) |
| Git y evidencia | 0.2 | Un issue, una rama, un PR y commits por integrante; contribuciones individuales; ADR-006; guia de reproduccion; salida de PostgreSQL sanitizada; etiqueta final | tabla de trazabilidad abajo, `README-reproduccion.md`, `docs/evidencia/postgres-salida-sanitizada.txt` | VERIFICADO (revisiones: NO_VERIFICADO, ver nota) |

## Trazabilidad por integrante

| Integrante | Issue | Rama | Commit de codigo | Commit de doc | PR | Mergeado por | Pruebas despues del merge | Contribucion |
|---|---|---|---|---|---|---|---|---|
| 1 Jared | #28 | `jdbc-base-postgres` | `4a446fd` | `64599fe` | #27 (merge `1c4e511`, 2026-10-21) | Jesus | 96 unitarias, 3 integracion | `docs/contribuciones/integrante-1-P08.md` |
| 2 Suriel | #29 | `jdbc-pruebas-integracion` | `c87cca7` | `5dd4200`, `3a65703` | #30 (merge `63ace2d`, 2026-10-23) | Jesus | 96 unitarias, 14 integracion | `docs/contribuciones/integrante-2-P08.md` |
| 3 Jesus | #31 | `3-jdbc-ventas-transaccion` | `7fd9922` | `b7dbfc6` | #32 (merge `ac92607`, 2026-10-24) | Jared | 101 unitarias, 27 integracion | `docs/contribuciones/integrante-3-P08.md` |

Los tres issues (#28, #29, #31) y los tres PR (#27, #30, #32) estan cerrados y mergeados a `main`. En GitHub los issues y los PR comparten la misma numeracion: por eso el PR de Jared es #27 y su issue #28.

## Revisiones de los PR (lo que realmente hay en GitHub)
- PR #27: mergeado por Jesus; sin comentario de revision (pestana Conversation en 0; el revisor asignado, Suriel, quedo como revision solicitada).
- PR #30: mergeado por Jesus; sin comentario de revision.
- PR #32: mergeado por Jared; sin comentario de revision (Conversation en 0 al momento del merge).
- Limite declarado: la columna "Comentario de revision antes del merge" queda NO_VERIFICADO en las tres contribuciones. La trazabilidad se apoya en issue, commits, PR enlazados con `Closes #N`, y pruebas; el merge lo hizo una persona distinta del autor en los tres casos.

## Resultados de pruebas (evidencia reproducible)

| Punto | Unitarias | Integracion | Resultado |
|---|---|---|---|
| Estado base antes de P08 (`main` en `5c1a5ee`, 2026-10-20) | 85 | 0 | BUILD SUCCESS |
| Rama de Jared (`4a446fd`) | 96 | 3 | BUILD SUCCESS |
| `main` tras el PR de Suriel (`3a65703`, 2026-10-24) | 96 | 14 | BUILD SUCCESS |
| `main` final (`ac92607`, 2026-10-24) | 101 | 27 | BUILD SUCCESS |

Desglose de las 101 unitarias: `ArquitecturaHexagonalTest` 2, `ArquitecturaPersistenciaTest` 2, `ProductoTableModelTest` 4, `CarritoTableModelTest` 3, `VentasControllerRegistrarVentaTest` 4, `VentasControllerAgregarCarritoTest` 5, `VentasControllerTest` 7, `ConfiguracionJdbcTest` 5, `ProductoRepositoryJdbcValidacionTest` 4, `VentaRepositoryJdbcTransaccionTest` 5, `VentaRepositoryEnMemoriaTest` 2, `ProductoRepositoryEnMemoriaTest` 2, `ConfiguracionGlobalSingletonTest` 2, `DetalleVentaTest` 3, `VentaTest` 12, `DineroTest` 8, `DescuentoStrategyTest` 6, `ProductoTest` 10, `VentasFacadeTest` 6, `RegistrarVentaServiceTest` 9.

Desglose de las 27 de integracion (PostgreSQL 16): `EsquemaPostgresIT` 3, `ProductoRepositoryJdbcIT` 11, `VentaRepositoryJdbcIT` 9, `VentasFacadeJdbcIT` 4.

## Calidad (Sonar Community Build, proyecto `P08-VENTAS`)

| Analisis | Fecha | Quality Gate | Issues | Seguridad | Fiabilidad | Cobertura | Duplicacion |
|---|---|---|---|---|---|---|---|
| 1 (`main` local en `ac92607`, `mvn clean verify -Pintegracion`) | 2026-10-24 22:26 | Passed | 15 (mantenibilidad) | 0 | 0 | 72.8% (576 lineas) | 0.0% |

- Es el primer analisis del proyecto: para el Quality Gate todo el codigo cuenta como nuevo, por eso se reporta la pestana Overall Code.
- La cobertura incluye las pruebas de integracion (JaCoCo `report-completo` en el perfil `integracion`).
- Limites declarados: quedan 15 issues de mantenibilidad sin atender ni clasificar en este documento (PENDIENTE de revisar el detalle); Sonar Community no analiza inyecciones criticas, por eso la defensa contra SQL inyectado se prueba con casos propios (`ProductoRepositoryJdbcIT`).

## Seguridad y datos
- Sin credenciales en el repositorio: `.env` esta en `.gitignore` y se versiona solo `.env.example`; la contrasena de la base y el token de Sonar nunca se escriben en el codigo ni en la documentacion.
- La salida de PostgreSQL en `docs/evidencia/postgres-salida-sanitizada.txt` se genera con `scripts/evidencia-postgres.sh`, que enmascara la clave y las rutas del usuario. Los datos son sinteticos (P1, P2, P3, F-001).
- Las pruebas de integracion usan una base aparte (`*_test`) y se niegan a truncar cualquier otra.

## Limitaciones declaradas
- La columna `venta.cerrada` queda en `false`: `RegistrarVentaService` (heredado de P05 a P07) no invoca `Venta.cerrar()`. El adaptador guarda y recupera ambos estados, comprobado por `VentaRepositoryJdbcIT.unaVentaCerradaSeRecuperaCerrada`. Cambiar el ciclo de vida de la venta queda fuera de P08.
- Concurrencia, lectura con el descuento por defecto y falta de pool de conexiones: ver ADR-006.
- El archivo `postgres-salida-sanitizada.txt` generado el 2026-10-23 muestra estructura y rechazos; sus tablas de datos aparecen vacias porque se genero antes del recorrido de la aplicacion. Los datos persistidos por la aplicacion se muestran en las capturas de Jesus. Regenerar el archivo despues de una venta: PENDIENTE.
- La captura de `mvn clean test` de Jesus (101) no trae las pruebas de integracion; las 27 se muestran en la corrida final de Jared en `main`.

## Reproduccion
Ver `README-reproduccion.md`, seccion P08.
