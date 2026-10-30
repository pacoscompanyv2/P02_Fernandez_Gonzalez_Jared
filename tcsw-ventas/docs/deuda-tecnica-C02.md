# Revision de deuda tecnica - C02

Responsable: Suriel Francisco Tecpile. Fecha de la revision: 27/10/26 (analisis inicial) y 29/10/26 (analisis sobre main, proyecto `cc2-ventas`).
Los estados se declaran por separado: **CORREGIDA** (hay commit y prueba), **ACEPTADA** (decision justificada, sigue abierta) y **PENDIENTE** (no se ha revisado). No se marca nada que no se haya hecho.

## 1. Deuda detectada por el equipo

| # | Deuda | Origen | Decision | Evidencia | Estado |
|---|---|---|---|---|---|
| D1 | `venta.cerrada` quedaba `false` en la base al registrar una venta | P08 (tabla `venta`) | Corregida: `RegistrarVentaService` llama `venta.cerrar()` antes de guardar | `RegistrarVentaCierraVentaTest` + consulta en la pestana "Consultar venta" muestra CERRADA; la demo antes/despues lee `cerrada=true` desde PostgreSQL tras reiniciar | CORREGIDA |
| D2 | Actualizacion perdida en JDBC: `UPDATE producto SET existencia = ?` escribe un valor absoluto calculado con una lectura vieja | P08 (`VentaRepositoryJdbc`) | Se conserva JDBC por contrato; el adaptador Hibernate descuenta de forma relativa con bloqueo (ADR-008). Se documenta como limitacion del adaptador JDBC | prueba de concurrencia de Jesus (ADR-008) | ACEPTADA |
| D3 | Reconstruir una venta usa el descuento por defecto (mayoreo), no el que se uso al vender | P08 (ADR-006) | Aceptada: el dominio no guarda la estrategia; cambiarlo exige modificar `Venta` | ADR-006 / ADR-009 | ACEPTADA |
| D4 | Hibernate sin pool de conexiones (DriverManager) | C02 (ADR-007) | Aceptada: suficiente para un cliente de escritorio de un solo usuario | ADR-007 | ACEPTADA |
| D5 | Las pruebas de la capa Swing no abren ventanas (solo controlador y modelos de tabla) | P07 | Aceptada: las vistas se verifican con la demostracion manual | protocolo visual C02 | ACEPTADA |

## 2. Hallazgos de SonarQube

Proyecto local `cc2-ventas` (contenedor `sonarqube`, http://localhost:9000). Analisis del 29/10/26 sobre `main`: **18 issues** (0 seguridad, 0 fiabilidad, 18 mantenibilidad). El analisis inicial de Suriel del 27/10 listaba solo 2 (S106 y S1481) porque se hizo sobre su rama, antes de integrar Hibernate y las pruebas nuevas.

| # | Sev. | Regla | Archivo:linea | Decision | Estado |
|---|---|---|---|---|---|
| S1 | MAJOR | java:S106 | `adapter/in/swing/Main.java:27` | Aceptar: el mensaje de arranque por consola indica que adaptador esta activo (se usa en la demo) y no contiene datos sensibles | ACEPTADA |
| S2 | MINOR | java:S1128 | `adapter/out/hibernate/VentaRepositoryHibernate.java:10` | Corregir: se quita el import `java.math.BigDecimal` sin uso (commit `7902d12`) | CORREGIDA |
| S3 | MINOR | java:S1128 | `ConcurrenciaVentasIT.java:5` | Corregir: se quita el import `domain.Producto` sin uso (commit `7902d12`) | CORREGIDA |
| S4 | MAJOR | java:S5778 | `VentasFacadeHibernateIT.java:46` | Corregir: la lista de items se arma fuera de la lambda de `assertThrows` (commit `7902d12`) | CORREGIDA |
| S5-S7 | INFO | java:S1135 | `VentaRepositoryJdbc.java:17`, `ProductoRepositoryJdbc.java:16`, `RegistrarVentaService.java:42` | Aceptar: falso positivo; Sonar toma la palabra "todo" ("todo el SQL...", "se guarda todo", "se valida TODO antes...") como un comentario TODO. No hay tareas pendientes | ACEPTADA |
| S8 | MAJOR | java:S1141 | `VentaRepositoryJdbc.java:51` | Aceptar: el `try` interno es el que hace `rollback`; extraerlo cambia codigo de P08 ya entregado y probado (riesgo mayor que el beneficio) | ACEPTADA |
| S9-S18 | MAJOR | java:S5778 (x10) | pruebas de P07/P08: `ProductoRepositoryJdbcValidacionTest:36`, `VentasFacadeTest:44,85,93`, `RegistrarVentaServiceTest:58,87,104,118,131,156` | Aceptar: `assertThrows` con lambdas que arman datos dentro; solo afecta pruebas ya entregadas y no cambia su resultado | ACEPTADA |

## 3. Resultado del segundo analisis
- Issues antes: 18   Issues despues: 15   (de Sonar, no estimados; 3 corregidas, 15 aceptadas con motivo)
- Quality Gate: Passed; cobertura 71.6% sobre 861 lineas; duplicacion 0.0%
- Nota: el primer analisis del 29/10 mostro 47.9% de cobertura y el segundo 71.6%; la diferencia no se investigo
