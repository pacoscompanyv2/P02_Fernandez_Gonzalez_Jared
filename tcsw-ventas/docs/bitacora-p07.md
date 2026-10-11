# Bitacora P07 - Cliente Swing

Repositorio: https://github.com/pacoscompanyv2/P02_Fernandez_Gonzalez_Jared
Comando de pruebas: `cd tcsw-ventas && mvn clean test`

| Fecha y hora | Quien | Accion | Resultado |
|---|---|---|---|
| 2026-10-05 23:42 | Jared | Estado base antes de P07 | 55 pruebas, BUILD SUCCESS |
| 2026-10-06 00:09 | Jared | Repeticion del estado base | 55 pruebas, BUILD SUCCESS |
| 2026-10-06 00:14 | Jared | `VentasControllerTest` (4 pruebas) | 58 pruebas, BUILD SUCCESS |
| 2026-10-06 | Jared | Revision de diseno: la fachada no pasaba por `RegistrarVentaService` y el inventario podia descontarse a medias | Hallazgos 1 y 2 (ver ADR-005) |
| 2026-10-06 00:24 | Jared | Correcciones y 11 pruebas nuevas | 69 pruebas, BUILD SUCCESS |
| 2026-10-06 | Jared | Commit `62f1518` (#21), PR #22; doc `906e112` | 9 archivos, +442 / -22 |
| 2026-10-07 aprox. | Suriel | Merge del PR #22 (`a1b7412`) | issue #21 cerrado |
| segun su captura | Suriel | Catalogo (issue #23): `ProductoTableModel`, `CatalogoPanel`, pruebas; commit `0ee7979`, doc `f6bf01c`, PR #24 | 78 pruebas, BUILD SUCCESS |
| 2026-10-10 | Suriel | PR #24 mergeado, issue #23 cerrado | - |
| 2026-10-10 14:49 | Jesus | Carrito, `SwingWorker` y `MainFrame` (issue #25): commit `b3037bb`, doc `082ede8`, PR #26 | 85 pruebas, BUILD SUCCESS |
| 2026-10-10 14:50 | Jesus | `mvn clean compile` y recorrido visual de la ventana | 5 capturas (ver protocolo visual) |
| 2026-10-10 | Jared | Merge del PR #26 (`7bc7d7b`), comentario "buen trabajo!!"; issue #25 cerrado | - |
| 2026-10-11 11:29 | Jared | `mvn clean test` en `main` integrado | 85 pruebas, BUILD SUCCESS |
| 2026-10-11 | Jared | Correccion de nombres de rama y de PR/revision en las contribuciones 2 y 3 (`50a0e68`, `10076a3`, `f753958`) | docs coherentes con GitHub |
| 2026-10-11 12:28 | Jared | Sonar, analisis 1 (proyecto `TCSW-VENTAS-p07`, commit `f753958`) | Quality Gate Passed, 17 issues, cobertura 66.1% |
| 2026-10-11 | Jared | Correccion `java:S1948` y `WindowConstants`, commit `202e83c` | 85 pruebas, BUILD SUCCESS (salida de `mvn clean verify` de las 13:18) |
| 2026-10-11 13:06 | Jared | Sonar, analisis intermedio sobre el arbol de trabajo (antes del commit) | Quality Gate Passed, 10 issues (-7) |
| 2026-10-11 13:18 | Jared | Sonar, analisis 2 sobre el commit `202e83c` | Quality Gate Passed, 10 issues (+0 respecto al anterior) |
| (al cerrar) | Jared | Doc final y etiqueta `v0.2-swing` | pendiente de registrar fecha |

## Errores y decisiones
- El codigo de reparto inicial asumia constructores y metodos inexistentes en el repositorio (`new VentasFacade()`, `new RegistrarVentaService(p, v, d)`, `service.registrarVenta`, `DescuentoStrategyFactory.crearSinDescuento()`, `calcularTotal()` como numero). Se detecto comparando con el codigo real antes de subirlo y los tests se adaptaron a los constructores reales.
- Total de venta: `calcularTotal()` devuelve `Dinero`, no un numero; el mensaje usa `calcularTotal().valor()`. El importe se muestra con la configuracion regional del equipo (`$300,00`).
- El catalogo no se actualizaba tras una venta; se agrego un callback del carrito al catalogo (`setAlRegistrarVenta`). Se comprobo en la captura posterior: existencia de P1 baja de 20 a 18.
- El boton "Registrar venta" se deshabilita mientras corre el `SwingWorker` para evitar doble registro.
- La correccion de Sonar se hizo directo en `main` y no por PR; queda trazada por el mensaje del commit.
- Las ramas reales de Suriel y Jesus son las generadas desde sus issues (`23-...`, `25-...`); las contribuciones se corrigieron para reflejarlo.

## Pendiente al cerrar
- Repetir el recorrido corto de la ventana en `main` final (posterior a la correccion de Sonar).
- Crear y subir la etiqueta `v0.2-swing`; registrar su hash en el envio de Eminus.
