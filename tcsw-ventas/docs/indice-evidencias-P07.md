# Indice de evidencias - P07 Cliente Swing (R01)

Equipo: Jared Fernandez Gonzalez (pacoscompanyv2), Suriel Francisco Tecpile (SurielFrancisco), Jesus Armando Andres Tablilla (JesusAndres765)
Repositorio: https://github.com/pacoscompanyv2/P02_Fernandez_Gonzalez_Jared
Etiqueta final: `v0.2-swing` (rama `main`)
Estados usados: VERIFICADO = con evidencia; NO_VERIFICADO = no se ejecuto; PENDIENTE = falta hacerlo.

## Resumen por criterio de R01

| Criterio | Pts | Evidencia | Donde | Estado |
|---|---|---|---|---|
| Funcionamiento | 0.8 | Recorrido: catalogo, agregar al carrito, aviso de folio vacio, registro de venta, error por existencia insuficiente, catalogo actualizado (P1 de 20 a 18) | `docs/protocolo-visual-P07.md`, `docs/capturas/` | VERIFICADO (rama de Jesus, 2026-10-10) |
| Funcionamiento (despues de la correccion de Sonar) | - | Abrir la ventana en `main` final y repetir el recorrido corto | `docs/protocolo-visual-P07.md` (seccion final) | PENDIENTE |
| Solucion tecnica | 0.5 | Arquitectura hexagonal conservada (`ArquitecturaHexagonalTest`, 2 pruebas); vista -> controlador -> fachada -> caso de uso; `SwingWorker` para registrar la venta; modelos de tabla separados de las vistas | `docs/adr/ADR-005-facade-delega-en-caso-de-uso.md`, `adapter/in/swing/*` | VERIFICADO |
| Pruebas | 0.3 | 85 pruebas, 0 fallos, BUILD SUCCESS en `main` (2026-10-11 11:29). Casos de error: facade nulo, indice invalido, cantidad cero o negativa, producto inexistente, existencia insuficiente, carrito conservado si el registro falla, carrito vacio, folio vacio | salida de `mvn clean test`, `src/test/java/**` | VERIFICADO |
| Calidad y Sonar | 0.2 | Analisis 1: Quality Gate Passed, 17 issues de mantenibilidad. Hallazgo atendido (regla `java:S1948` y uso de `WindowConstants`): analisis 2, Quality Gate Passed, -7 issues | proyecto `TCSW-VENTAS-p07` en SonarQube local, capturas en `docs/capturas/` | VERIFICADO |
| Git y evidencia | 0.2 | Un issue, una rama, un PR y commits por integrante; contribuciones; guia de reproduccion; etiqueta final | tabla de trazabilidad abajo, `README-reproduccion.md` | VERIFICADO (revisiones: ver nota) |

## Trazabilidad por integrante

| Integrante | Issue | Rama | Commit de codigo | Commit de doc | PR | Pruebas en su rama | Contribucion |
|---|---|---|---|---|---|---|---|
| 1 Jared | #21 | `1-swing-controlador-base` | `62f1518` | `906e112` | #22 (merge `a1b7412`) | 69 | `docs/contribuciones/integrante-1-P07.md` |
| 2 Suriel | #23 | `23-vista-de-catalogo-en-swing` | `0ee7979` | `f6bf01c` | #24 | 78 | `docs/contribuciones/integrante-2-P07.md` |
| 3 Jesus | #25 | `25-carrito-registro-de-venta-con-swingworker-y-ventana-principal` | `b3037bb` | `082ede8` | #26 (merge `7bc7d7b`) | 85 | `docs/contribuciones/integrante-3-P07.md` |

Los tres issues (#21, #23, #25) y los tres PR (#22, #24, #26) estan cerrados y mergeados a `main`.

## Revisiones de los PR (lo que realmente hay en GitHub)
- PR #22: mergeado por Suriel; sin comentario de revision.
- PR #24: sin comentario de revision en GitHub.
- PR #26: mergeado por Jared; comentario "buen trabajo!!".
- Limite declarado: las revisiones formales son escasas; la trazabilidad se apoya en issue, commits, PR y pruebas.

## Resultados de pruebas (evidencia reproducible)

| Punto | Pruebas | Resultado |
|---|---|---|
| Estado base antes de P07 | 55 | BUILD SUCCESS |
| Rama de Jared | 69 | BUILD SUCCESS |
| Rama de Suriel | 78 | BUILD SUCCESS |
| Rama de Jesus (2026-10-10 14:49) | 85 | BUILD SUCCESS |
| `main` integrado (2026-10-11 11:29) | 85 | BUILD SUCCESS |

Desglose en `main` (85): `ArquitecturaHexagonalTest` 2, `VentasControllerTest` 7, `VentasControllerAgregarCarritoTest` 5, `VentasControllerRegistrarVentaTest` 4, `ProductoTableModelTest` 4, `CarritoTableModelTest` 3, `VentaRepositoryEnMemoriaTest` 2, `ProductoRepositoryEnMemoriaTest` 2, `ConfiguracionGlobalSingletonTest` 2, `DetalleVentaTest` 3, `VentaTest` 12, `DineroTest` 8, `DescuentoStrategyTest` 6, `ProductoTest` 10, `VentasFacadeTest` 6, `RegistrarVentaServiceTest` 9.

## Calidad (Sonar Community Build, proyecto `TCSW-VENTAS-p07`)

| Analisis | Fecha | Quality Gate | Issues | Seguridad | Fiabilidad | Cobertura | Duplicacion |
|---|---|---|---|---|---|---|---|
| 1 (commit `f753958`) | 2026-10-11 12:28 | Passed | 17 (mantenibilidad) | 0 | 0 | 66.1% (411 lineas) | 0.0% |
| 2 (commit `202e83c`) | 2026-10-11 13:18 | Passed | 10 (-7) | 0 | 0 | 0.0% | 0.0% |

- Hallazgo atendido: regla `java:S1948` (campos no serializables en clases Swing, que son serializables) resuelto con `transient` en 6 campos de `CarritoPanel`, `CatalogoPanel`, `CarritoTableModel` y `ProductoTableModel`, mas `WindowConstants.EXIT_ON_CLOSE` en `MainFrame`. Commit: `202e83c`.
- Limites declarados: la cobertura (66.1%) es menor que la del corte anterior porque las vistas Swing (`CatalogoPanel`, `CarritoPanel`, `MainFrame`, `Main`, `RegistrarVentaWorker`) no tienen pruebas automaticas; los modelos de tabla y el controlador si estan cubiertos. Sonar Community no analiza inyecciones criticas. Quedan 10 issues de mantenibilidad sin atender.

## Reproduccion
Ver `README-reproduccion.md`, seccion P07.
