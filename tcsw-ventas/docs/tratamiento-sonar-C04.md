# Tratamiento de hallazgos de Sonar (C04)

SonarQube Community local (localhost:9000). Proyectos: `apicc4` (API, Maven) y `frontcc4` (cliente, `@sonar/scan`).
Analisis ANTES del tratamiento: ambos con Quality Gate **Passed** (captura en el Word de C04).

| Proyecto | Seguridad / Fiabilidad | Mantenibilidad (antes) | Cobertura | Duplicacion |
|---|---|---|---|---|
| apicc4 | 0 / 0 | 32 issues (A) | 73.6 % sobre 993 lineas | 0.0 % |
| frontcc4 | 0 / 0 | 2 issues (A) | 97.2 % sobre 250 lineas | 0.0 % |

Criterio: se corrige lo que es de bajo riesgo y verificable con las pruebas existentes; lo que cambiaria un contrato (nucleo compartido con Swing, firmas de Spring, persistencia) se justifica y no se toca en un candidato de liberacion.
Estados: CORREGIDO (hay cambio en el commit de tratamiento), JUSTIFICADO (no se cambia, con la razon). Que el cambio elimine el hallazgo se confirma solo con el re-analisis: **PENDIENTE** hasta entonces.

## API (32)
| Regla | Donde | Decision | Razon |
|---|---|---|---|
| S125 (codigo comentado) | VentaController:26 | CORREGIDO | falso positivo: era prosa con `(@Valid);`; se reescribio el comentario |
| S6204 (`toList()`) x4 | VentaController:47, VentaResponse:16, ProductoController:28, ManejadorErrores:77 | CORREGIDO | las listas solo se recorren o se serializan (`RegistrarVentaService` solo itera `items`) |
| S6204 | ProductoRepositoryHibernate:53 | JUSTIFICADO | la lista llega al catalogo Swing y no se verifico que nadie la modifique; una lista inmutable podria romperlo |
| S1192 (literal repetido) | ConfiguracionApi:30 | CORREGIDO | constante `MEMORIA` |
| S1135 (TODO) x5 | ProductoController:15, ErrorResponse:7, VentaRepositoryJdbc:17, ProductoRepositoryJdbc:16, RegistrarVentaService:44 | CORREGIDO | falso positivo: es la palabra "todo" en espanol dentro de comentarios, no un pendiente; se reescribieron |
| S6201 (instanceof con patron) | RepositorioHibernateException:31 | CORREGIDO | Java 17 |
| S2638 (@Nullable vs @NonNullApi) x3 | ManejadorErrores:72, 82, 90 | JUSTIFICADO | son sobrescrituras de `ResponseEntityExceptionHandler` de Spring; la firma la impone el framework. No se verifico si agregar `@Nullable` lo resuelve |
| S106 (System.out) | swing/Main:27 | JUSTIFICADO | arranque de la app Swing, no de la API; mensaje informativo de consola |
| S1141 (try anidado) | VentaRepositoryJdbc:51 | JUSTIFICADO | es parte de la transaccion JDBC; refactorizar la persistencia a este nivel arriesga el comportamiento ya verificado (P09) |
| S6208 x2 | VentaRepositoryJdbcTransaccionTest:42, 72 | JUSTIFICADO | estilo en pruebas, sin efecto en el comportamiento |
| S5778 x10 | pruebas de ProductoRepositoryJdbcValidacion, VentasFacade, RegistrarVentaService | JUSTIFICADO | lambdas de `assertThrows` con mas de una llamada; refactor de 10 pruebas fuera de alcance; queda como deuda declarada |
| S6206 (record) x2 | ItemVenta:4, DetalleVenta:4 | JUSTIFICADO | pertenecen al nucleo compartido con Swing; pasarlos a `record` cambia los accesores en ambas interfaces |

Resumen API: 12 CORREGIDOS, 20 JUSTIFICADOS (32).

Hallazgo introducido por el propio tratamiento: el re-analisis (21-nov) reporto S1128 (import `java.util.stream.Collectors` sin usar) en VentaResponse:8, porque el cambio de S6204 dejo ese import. Se elimino. Con eso la condicion New Issues = 0 del Quality Gate de New Code deja de fallar; que asi sea queda PENDIENTE hasta el siguiente analisis.

## Cliente (2)
| Regla | Donde | Decision | Razon |
|---|---|---|---|
| S3776 (complejidad 16 > 15) | core/api-error.ts:14 | CORREGIDO | se extrajo `esSinConexion()`; el comportamiento no cambia (las pruebas de `api-error.spec.ts` lo cubren) |
| S7649 (alias en input) | consulta/consulta.ts:49 | CORREGIDO | el input pasa a llamarse `folio` sin alias y el campo del formulario a `folioEscrito`; la prueba usa `setInput('folio', ...)` |

## Estado de la verificacion
- ANTES del parche: la evidencia de C03 ya versionada (`docs/evidencia/verificar-c03-salida.txt`, 18-nov 07:31, commit 9170d28, etiqueta v0.3-web): 174 unitarias y 56 de integracion sin fallos, 21 y 9 comprobaciones HTTP sin fallas, 51 pruebas del cliente, 5 pasos con 0 fallas. No se archivo una corrida propia de C04 sin el parche: la corrida de las 00:25 ya lo incluia (aplicado a las 00:25:13).
- DESPUES del parche (875ccf9 con los cambios sin commit, 21-nov 00:27): `docs/evidencia/c04-despues-del-parche-salida.txt`: los mismos resultados. VERIFICADO: los cambios no rompen las pruebas existentes.
- Que Sonar ya no reporte los hallazgos corregidos y los numeros finales: PENDIENTE (re-analisis sobre el commit que lleve el tag).
