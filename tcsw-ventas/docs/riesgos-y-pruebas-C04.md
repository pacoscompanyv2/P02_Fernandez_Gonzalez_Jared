# Riesgos y pruebas — C04 (candidato `v1.0-rc`)

Autoría: Jared Fernández González (integrante 1). El trabajo de C04 lo realizó una sola persona; no se
generan archivos de contribución para los integrantes 2 y 3 porque no hubo aportaciones suyas.

Este documento liga cada riesgo relevante del producto con las pruebas que lo cubren, declara lo que
**no** está cubierto y separa lo verificado de lo pendiente.

## 1. Resultados de las pruebas (corrida posterior al tratamiento de Sonar)

| Conjunto | Pruebas | Fallas | Estado |
|---|---|---|---|
| API, unitarias (surefire, 35 clases) | 174 | 0 | VERIFICADO |
| API, integración (failsafe, 9 clases, PostgreSQL real) | 56 | 0 | VERIFICADO |
| Cliente Angular (Vitest, 9 archivos `.spec.ts`) | 51 | 0 | VERIFICADO |
| `verificar-c03.sh` (21 + 9 comprobaciones HTTP) | 30 | 0 | VERIFICADO |
| `verify-stack.sh` sobre el stack en Docker | 11 | 0 | VERIFICADO |
| `probar-reversa.sh` (regreso a `v0.3-web`) | 21 | 0 | VERIFICADO |

Fuente: salidas guardadas en `docs/evidencia/` y la corrida de CI de GitHub Actions (PR #55).

## 2. Cobertura

| Medición | Valor | Fuente |
|---|---|---|
| API, Sonar (`apicc4`) | 73.6 % sobre 993 líneas | SonarQube local |
| API, JaCoCo | 74.3 % (738 de 993 líneas) | `target/site/jacoco/jacoco.csv` |
| Cliente, Sonar (`frontcc4`) | 97.2 % sobre 251 líneas | SonarQube local |

La diferencia entre 73.6 % y 74.3 % se debe a que Sonar y JaCoCo no aplican exactamente las mismas
exclusiones; ambas cifras provienen de la misma ejecución de pruebas.

**Dónde está el hueco.** De las 255 líneas sin cubrir, 176 (69 %) pertenecen a seis clases de la capa
visual Swing con 0.0 %: `CarritoPanel` (52), `CatalogoPanel` (37), `ConsultaVentaPanel` (36),
`RegistrarVentaWorker` (20), `Main` (17) y `MainFrame` (14). Excluyendo esas seis clases, el resto del
backend queda en 738 de 817 líneas (≈ 90 %). Este cálculo sale de las cifras de JaCoCo de arriba.

## 3. Matriz riesgo → prueba

La asignación de pruebas se hizo con base en los nombres de las clases de prueba y en los resultados de
las corridas; el número entre paréntesis es la cantidad de pruebas de cada clase.

| # | Riesgo | Pruebas que lo cubren | Estado |
|---|---|---|---|
| R1 | Importes o descuentos mal calculados | `DineroTest` (8), `DetalleVentaTest` (3), `VentaTest` (12), `DescuentoStrategyTest` (6) | VERIFICADO |
| R2 | Existencia insuficiente o inventario mal descontado | `ProductoTest` (10), `RegistrarVentaServiceTest` (9), `RegistrarVentaExcepcionesTest` (2), `ProductoRepositoryJdbcIT` (11), `ProductoRepositoryHibernateIT` (9), `verify-stack.sh` (existencia −1 tras la venta) | VERIFICADO |
| R3 | Venta guardada a medias si algo falla | `RegistrarVentaCierraVentaTest` (2), `VentaRepositoryJdbcTransaccionTest` (5), `VentaRepositoryJdbcIT` (9), `VentaRepositoryHibernateIT` (9) | VERIFICADO |
| R4 | Dos ventas simultáneas sobre la misma existencia | `ConcurrenciaVentasIT` (3) | VERIFICADO |
| R5 | Folio duplicado | `VentaControllerTest` (17), `ManejadorErroresTest` (11), `verify-stack.sh` (409 `FOLIO_DUPLICADO`) | VERIFICADO |
| R6 | La API rompe su contrato (códigos HTTP, forma de los errores, `openapi.yaml`) | `ContratoApiTest` (5), `ProductoControllerTest` (4), `VentaControllerTest` (17), `ManejadorErroresTest` (11), `VentaResponseTest` (3), `ApiRecorridoTest` (8), `ApiVentasHibernateIT` (6) | VERIFICADO |
| R7 | Datos de entrada inválidos aceptados | `ProductoRepositoryJdbcValidacionTest` (4), `VentaControllerTest`, `verify-stack.sh` (400 `VALIDACION`, 404 `VENTA_NO_ENCONTRADA`) | VERIFICADO |
| R8 | Persistencia distinta entre JDBC, Hibernate y el esquema | `EsquemaPostgresIT` (3), `VentasFacadeJdbcIT` (4), `VentasFacadeHibernateIT` (2), `ConfiguracionJdbcTest` (5), `RepositoriosHibernateFactoryTest` (3) | VERIFICADO |
| R9 | Se rompe la arquitectura hexagonal (dependencias indebidas entre capas) | `ArquitecturaHexagonalTest`, `ArquitecturaHibernateTest`, `ArquitecturaPersistenciaTest`, `ArquitecturaRestTest` (ArchUnit, 10 pruebas en total) | VERIFICADO |
| R10 | La API no arranca o arranca mal configurada | `ApiArranqueTest` (3), `ConfiguracionJdbcTest` (5) | VERIFICADO |
| R11 | El candidato no se construye o no se ejecuta desde cero | CI (`api`, `cliente`, `stack`), `verify-stack.sh` (11), prueba de clon limpio | VERIFICADO (CI y stack); clon limpio PENDIENTE hasta correrla en el commit del tag |
| R12 | Regreso a la versión anterior pierde datos o no funciona | `probar-reversa.sh` (`REVERSA_VERIFICADA`, ventas 3 antes y 3 después) | VERIFICADO |
| R13 | Fallas en el cliente web (carrito, consulta, errores de red o de API) | 9 archivos `.spec.ts`, 51 pruebas, entre ellos `carrito.service.spec.ts` (11), `api-error.spec.ts` (9), `venta.spec.ts` (8) | VERIFICADO |
| R14 | Capa Swing: lógica de tablas y controlador | `CarritoTableModelTest`, `PartidasTableModelTest`, `ProductoTableModelTest`, `VentasController*Test` (29 pruebas en total) | VERIFICADO (lógica) |
| R15 | Capa Swing: ventanas y paneles (`CarritoPanel`, `CatalogoPanel`, `ConsultaVentaPanel`, `RegistrarVentaWorker`, `MainFrame`, `Main`) | Ninguna prueba automatizada (0.0 %) | NO CUBIERTO / PENDIENTE de revisión manual |

## 4. Lo que no está cubierto (declaración explícita)

1. **Interfaz gráfica Swing (R15).** 176 líneas sin ninguna prueba automática. La lógica que la alimenta
   (modelos de tabla y controlador) sí tiene pruebas; las ventanas no. No se afirma que funcionen de
   forma verificada. Cualquier revisión manual que se haga se registra aparte en la bitácora, con fecha
   y resultado.
2. **Ramas de error en Hibernate.** `EjecutorTransaccional` (8 líneas sin cubrir), `SesionesHibernate`
   (7), `DetalleVentaId` (8) y `RepositoriosHibernateFactory` (6): rutas de falla poco frecuentes.
3. **Piezas menores:** `RepositoriosFactory` de JDBC (7, 0.0 %), `NotificadorConsola` (4, 0.0 %) y
   algunas líneas de `Dinero`, `Producto` y `ConfiguracionApi`.
4. **Cliente web:** no hay prueba de navegador real (extremo a extremo); solo pruebas de componentes y
   servicios en jsdom, más la comprobación de la página y del proxy `/api` en `verify-stack.sh`.
5. **`verify-module.sh`:** el script usado es de elaboración propia y solo comprueba el módulo M14. El
   script oficial del profesor no estuvo disponible; no se declara como equivalente.

## 5. Decisión sobre Sonar y cobertura

Se decidió no perseguir un porcentaje de cobertura mayor ni cerrar los 20 hallazgos abiertos de la API.
Los 20 están analizados y justificados uno por uno en `tratamiento-sonar-C04.md`; la puerta de calidad
(Quality Gate) pasa en los dos proyectos. El criterio de pruebas se atiende con la matriz de la sección 3:
los riesgos de negocio (importes, inventario, atomicidad, concurrencia, contrato de la API) están cubiertos
y lo no cubierto está declarado en la sección 4.
