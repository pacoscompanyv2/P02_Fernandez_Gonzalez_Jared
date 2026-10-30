# Indice de evidencias - C02 (Escritorio y persistencia)

Entrega `C02_EQUIPO_01` · Equipo TCSW-E01 · Etiqueta `v0.2-escritorio`.
Estados: **VERIFICADO** (con evidencia), **NO_VERIFICADO** (no se ejecuto o no hay evidencia), **PENDIENTE** (falta hacerlo).

## Evidencia -> criterio R03

| Criterio R03 | Evidencia | Donde | Estado |
|---|---|---|---|
| Swing y MVC (2) | Pestana "Consultar venta", `VentasController.consultarVenta`, `PartidasTableModel`, ADR-009 | `docs/adr/ADR-009-mvc-consulta-de-venta.md`, `ConsultaVentaPanel.java` | VERIFICADO (codigo y pruebas) |
| Swing y MVC (2) | 9 pruebas unitarias de facade, controlador y modelo de tabla | `VentasFacadeBuscarVentaTest`, `VentasControllerConsultarVentaTest`, `PartidasTableModelTest` | VERIFICADO |
| Swing y MVC (2) | Captura de la consulta devolviendo una venta en la ventana | C02_EQUIPO_01, figuras de Suriel (solo muestra las pestanas) | NO_VERIFICADO |
| Hibernate y PostgreSQL (2) | `ProductoRepositoryHibernate` y `VentaRepositoryHibernate`, `hbm2ddl=validate`, ADR-007 | `adapter/out/hibernate/`, `docs/adr/ADR-007-...` | VERIFICADO |
| Hibernate y PostgreSQL (2) | 18 pruebas de integracion de Hibernate contra PostgreSQL 16 (9 productos + 9 ventas) | `ProductoRepositoryHibernateIT`, `VentaRepositoryHibernateIT` | VERIFICADO |
| Consistencia transaccional (1.5) | Bloqueo pesimista + descuento relativo, ADR-008 | `VentaRepositoryHibernate.java`, `docs/adr/ADR-008-...` | VERIFICADO |
| Consistencia transaccional (1.5) | `ConcurrenciaVentasIT` (3): JDBC vende 12 de 10; Hibernate rechaza la segunda venta; dos hilos, una pasa | `ConcurrenciaVentasIT.java` | VERIFICADO |
| Consistencia transaccional (1.5) | Rollback y folio duplicado (23505) | `VentaRepositoryHibernateIT` | VERIFICADO |
| Consistencia transaccional (1.5) | Demo antes/despues: venta en una JVM, reinicio de PostgreSQL, lectura en otra JVM | `docs/evidencia/demo-antes-despues-{jdbc,hibernate}.txt` | VERIFICADO |
| Pruebas (1) | 117 unitarias y 50 de integracion, 0 fallos, 0 errores | `scripts/resumen-pruebas.sh`, C02_EQUIPO_01 | VERIFICADO |
| Pruebas (1) | Reglas ArchUnit (Hibernate/JPA solo en su adaptador; `java.sql` solo en JDBC) | `ArquitecturaHibernateTest`, `ArquitecturaPersistenciaTest` | VERIFICADO |
| Pruebas (1) | Sonar: Quality Gate Passed, 18 -> 15 issues, cobertura 71.6% | C02_EQUIPO_01, `docs/deuda-tecnica-C02.md` | VERIFICADO |
| Pruebas (1) | Reproduccion desde cero con `verificar-c02.sh` | C02_EQUIPO_01 (captura de la cola de la salida) | VERIFICADO (pasos 1-3 no visibles en la captura; totales confirmados con `resumen-pruebas.sh`) |
| Colaboracion (0.5) | Issues #33, #36, #38; PR #35, #37, #39; commits con `(#N)` | GitHub, C02_EQUIPO_01 | VERIFICADO |
| Colaboracion (0.5) | Comentario de revision en los PR antes del merge | GitHub | NO_VERIFICADO |

## Trazabilidad por integrante

| Integrante | Issue | PR | Commits | Documento |
|---|---|---|---|---|
| 1 (Jared) Hibernate | #33 | #35 (el #34 se cerro por error) | `e158a6e`, `3399f03` | `integrante-1-C02.md` |
| 1 (Jared) parte de ventas | #38 | #39 | `12b39d8`, `d0e38b2`, `685ff3c`, `7902d12` | `integrante-1-C02-parte-ventas.md` |
| 2 (Suriel) Swing/MVC | #36 | #37 | `ce782b6`, `3986504` | `integrante-2-C02.md` |
| 3 (Jesus) | sin issue ni PR en C02 | - | - | sin documento (parte realizada por el integrante 1; el docente indico omitir su evidencia) |

## Limitaciones declaradas
- Ningun PR tiene comentario de revision visible antes del merge (#35, #37, #39); el #39 no tuvo revisor y lo integro su autor.
- La captura de Suriel muestra las pestanas pero no una consulta con resultado.
- La diferencia de cobertura entre los dos analisis de Sonar del 29/10 (47.9% y 71.6%) no se investigo.
