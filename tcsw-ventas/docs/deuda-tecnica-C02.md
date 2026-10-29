# Revision de deuda tecnica - C02

Responsable: Suriel Francisco Tecpile. Fecha de la revision: 27/10/26.
Los estados se declaran por separado: **CORREGIDA** (hay commit y prueba), **ACEPTADA** (decision justificada, sigue abierta) y **PENDIENTE** (no se ha revisado). No se marca nada que no se haya hecho.

## 1. Deuda detectada por el equipo

| # | Deuda | Origen | Decision | Evidencia | Estado |
|---|---|---|---|---|---|
| D1 | `venta.cerrada` quedaba `false` en la base al registrar una venta | P08 (tabla `venta`) | Corregida: `RegistrarVentaService` llama `venta.cerrar()` antes de guardar | `RegistrarVentaCierraVentaTest` + consulta en la pestana "Consultar venta" muestra CERRADA | ACEPTADA |
| D2 | Actualizacion perdida en JDBC: `UPDATE producto SET existencia = ?` escribe un valor absoluto calculado con una lectura vieja | P08 (`VentaRepositoryJdbc`) | Se conserva JDBC por contrato; el adaptador Hibernate descuenta de forma relativa con bloqueo (ADR-008). Se documenta como limitacion del adaptador JDBC | prueba de concurrencia de Jesus (ADR-008) | ACEPTADA |
| D3 | Reconstruir una venta usa el descuento por defecto (mayoreo), no el que se uso al vender | P08 (ADR-006) | Aceptada: el dominio no guarda la estrategia; cambiarlo exige modificar `Venta` | ADR-006 / ADR-009 | ACEPTADA |
| D4 | Hibernate sin pool de conexiones (DriverManager) | C02 (ADR-007) | Aceptada: suficiente para un cliente de escritorio de un solo usuario | ADR-007 | ACEPTADA |
| D5 | Las pruebas de la capa Swing no abren ventanas (solo controlador y modelos de tabla) | P07 | Aceptada: las vistas se verifican con la demostracion manual | protocolo visual C02 | ACEPTADA |

## 2. Hallazgos de SonarQube

| # | Severidad | Regla | Archivo:linea | Decision (corregir / aceptar con motivo) | Estado |
|---|---|---|---|---|---|
| S1 | BAJA | java:S106 | `src/main/java/application/observer/NotificadorConsola.java:18` | Aceptar: la salida por consola es intencional y se usa solo como notificador de demostracion; no contiene informacion sensible | ACEPTADA |
| S2 | BAJA | java:S1481 | `src/main/java/adapter/in/swing/VentasController.java:64` | Aceptar: la variable se conserva para mantener legible la secuencia de validacion; no afecta el resultado funcional | ACEPTADA |


## 3. Resultado del segundo analisis
- Issues antes: 2   Issues despues: 2   (de Sonar, no estimados)
