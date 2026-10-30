# Bitacora C02

Solo se registran horas que aparecen en el historial de Git, en capturas o en la salida de Maven/Sonar. Las horas de commit son las que guarda Git.

| Fecha y hora | Evento | Fuente |
|---|---|---|
| 2026-10-27 01:39 | Commit `e158a6e`: Hibernate como segundo adaptador (PR #35) | `git log` |
| 2026-10-27 08:50 | Merge del PR #35 (`ba9f0c7`) | `git log` |
| 2026-10-27 10:39 | Commit `ce782b6`: consulta de ventas y cierre de venta (#36) | `git log` |
| 2026-10-27 20:31 | Commit `3986504`: documentacion e ADR-009 de Suriel | `git log` |
| 2026-10-27 20:38 | `mvn clean test` en la rama de Suriel: 117 pruebas, BUILD SUCCESS | captura de Suriel |
| 2026-10-29 17:16 | Merge del PR #37 (`334d460`) | `git log` |
| 2026-10-29 17:24 | Commit `3399f03`: documentacion de Jared e ADR-007 (#33) | `git log` |
| 2026-10-29 17:30 | Demo antes/despues con JDBC | salida de `demo-antes-despues.sh` |
| 2026-10-29 17:31 | Commit `12b39d8`: `VentaRepositoryHibernate`, concurrencia y demo (#38) | `git log` |
| 2026-10-29 17:34 | Commit `d0e38b2`: documentacion de la parte de ventas y ADR-008 | `git log` |
| 2026-10-29 17:37 | Merge del PR #39 (`5fffae0`) | `git log` |
| 2026-10-29 17:52 | Commit `685ff3c`: correccion de estados de revision y numero de PR | `git log` |
| 2026-10-29 17:53 | `verificar-c02.sh`: demo con Hibernate | salida del script |
| 2026-10-29 18:00 | Primer analisis de Sonar (`cc2-ventas`): 18 issues, cobertura 47.9% | captura de Sonar |
| 2026-10-29 18:05 | Commit `7902d12`: correcciones segun Sonar | `git log` |
| 2026-10-29 18:07 | `mvn clean verify -Pintegracion` (117 y 50) y segundo analisis de Sonar: 15 issues, cobertura 71.6% | salida de Maven y captura |

## Incidencias
- El PR #34 se cerro manualmente por error; el mismo cambio se integro con el PR #35.
- El commit `e158a6e` no incluye `(#33)` en su mensaje y salio con un correo de ejemplo como autor; la configuracion de Git se corrigio despues.
- En C02 el integrante 3 no entrego su parte; la implemento el integrante 1 (issue #38, PR #39).
- Un token de Sonar se pego en un chat: se revoco y no se uso.
