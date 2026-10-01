# Matriz de trazabilidad - C01

| Criterio R02 | Patron/pieza | Codigo | Commit | Prueba |
|---|---|---|---|---|
| Modelo de dominio | Venta, Producto, Dinero | `domain/*` | (base P01-P02) | VentaTest, ProductoTest, DineroTest, DetalleVentaTest (33 casos) |
| Arquitectura hexagonal | Puertos y adaptadores | `application/port/*`, `adapter/out/memory/*` | af35d6e (puertos, #7/#8), commit adaptadores P05 | ArquitecturaHexagonalTest (2), ProductoRepositoryEnMemoriaTest, VentaRepositoryEnMemoriaTest |
| Patrones - Strategy/Factory | Descuento intercambiable | `domain/descuento/*` | fb19333 (#15/#16) | DescuentoStrategyTest (6 casos) |
| Patrones - Observer | Notificacion al registrar | `application/observer/*` | a12f1db (#17/#18) | RegistrarVentaServiceTest (notifica exito / no notifica error) |
| Patrones - Facade | Punto de entrada simple | `application/facade/*` | d538a4a (#19/#20) | VentasFacadeTest (exito / producto inexistente) |
| Analisis critico Singleton | Por que no se usa | `antipatron/*`, `docs/adr/ADR-004-*` | d538a4a, fix 6b9072f | ConfiguracionGlobalSingletonTest (demo del problema) |
| Calidad y Sonar | 2 hallazgos reales corregidos | `DescuentoStrategyFactory.java`, `NotificadorConsola.java` | beb9be2 | Sonar TCSW-VENTAS-C01: Quality Gate Passed, 87% cobertura |
| Etiqueta del corte | v0.1-arquitectura | - | beb9be2 | `git tag -l v0.1-arquitectura` |

Total de pruebas en main: 53/53, BUILD SUCCESS.
