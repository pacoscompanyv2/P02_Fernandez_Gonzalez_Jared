# Contribucion - Integrante 2 (C02)

**Nombre:** Suriel Francisco Tecpile
**GitHub:** SurielFrancisco
**Alcance:** Swing/MVC: consulta de ventas guardadas (pestana nueva), cierre correcto de la venta registrada y revision de deuda tecnica

## Issue
- **#36** - Swing/MVC: consulta de ventas y cierre de venta registrada
- Criterio de cierre: pestana "Consultar venta" funcionando contra la persistencia, `venta.cerrada=true` tras registrar, pruebas unitarias del facade/controlador/modelo de tabla, ADR-009 y `docs/deuda-tecnica-C02.md`; `mvn clean test` en verde y PR integrado a main

## Rama y commits
- Rama: `swing-consulta-venta`
- Commit de codigo: `ce782b6` - feat: consulta de ventas en Swing y cierre de venta registrada (#36)
- Commit de documentacion: ver historial del PR #37

## Decision tecnica
- MVC: la vista solo habla con `VentasController`; el controlador valida y delega al facade (ADR-009)
- `buscarVenta` es solo lectura: consultar no modifica existencias (hay prueba)
- Una venta registrada se cierra; una venta vacia se rechaza

## Codigo creado o modificado
- `VentasFacade.buscarVenta`, `VentasController.consultarVenta`, `RegistrarVentaService` (cierra la venta)
- `ConsultaVentaPanel`, `PartidasTableModel`, `MainFrame` (pestanas)
- Pruebas: `VentasFacadeBuscarVentaTest`, `RegistrarVentaCierraVentaTest`, `VentasControllerConsultarVentaTest`, `PartidasTableModelTest`
- `docs/adr/ADR-009-mvc-consulta-de-venta.md`, `docs/deuda-tecnica-C02.md`

## Pull Request y revision
- PR #37 - "feat: consulta de ventas en Swing y cierre de venta registrada (#36)" - Closes #36
- Revisor asignado: Jesus Armando Andres Tablilla
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- Unitarias (`mvn clean test`): 117 pruebas

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #36 | VERIFICADO |
| Rama | `swing-consulta-venta` | VERIFICADO |
| Commit de codigo | `ce782b6` | VERIFICADO |
| Pull Request | #37 (Closes #36) | VERIFICADO |
| Pruebas unitarias | 117 | VERIFICADO |
| Pestana de consulta probada a mano con la app | captura de la demostracion | VERIFICADO |
| Comentario de revision antes del merge | revisor: Jesus | NO_VERIFICADO |

## Limitaciones declaradas
- La vista Swing no tiene pruebas automaticas de ventana; se evidencia con captura de la app en ejecucion
- El analisis de Sonar y su tabla de decisiones estan en `docs/deuda-tecnica-C02.md`
