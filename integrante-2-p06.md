# Contribucion - Integrante 2 (P06)

**Nombre:** Suriel Francisco Tecpile
**GitHub:** SurielFrancisco
**Patron:** Observer (notificacion al registrar una venta)

## Issue
- **#17** - Notificar con Observer al registrar una venta
- Criterio de cierre: existe el observer; `RegistrarVentaService` notifica al guardar; incluye pruebas de que notifica (exito) y de que NO notifica si falla (error); PR mergeado a main
- Estado: Closed (cerrado por el merge del PR #18)

## Rama y commits
- Rama vinculada: `17-observer-venta`
- Commits del PR (3):
  - `3960418` - chore: Actualizacion de .gitignore
  - `a12f1dbd640bb8597309607f51d00eb916ac627d` - feat: observer en ventas
  - `8504927` - feat: observer en ventas
- Alcance del PR: 5 archivos cambiados (+63)

## Decision de patron
- Variacion concreta: quien debe enterarse cuando se registra una venta
- Patron elegido: Observer (interfaz `VentaObserver`, metodo `onVentaRegistrada(Venta)`); el servicio notifica solo si el guardado tiene exito
- Alternativa comparada: PENDIENTE (Suriel debe indicar que alternativa considero)
- Costo declarado: PENDIENTE (Suriel debe indicarlo)

## Codigo creado o modificado
- `application/observer/VentaObserver.java`
- `application/service/RegistrarVentaService.java` (notifica a los observers al guardar)
- Prueba: `application/service/RegistrarVentaServiceTest.java` (paso de 2 a 4 pruebas: notifica en exito / no notifica en error)
- `.gitignore` (actualizacion)

## Pull Request y revision
- PR #18 - "feat: Notificar con Observer al registrar una venta (#17)" - Closes #17
- Mergeado por: Jared Fernandez Gonzalez (commit de merge 741e1f6)
- Comentario de revision explicito antes del merge: SI ("Buen trabajo!")

## Pruebas
- `mvn clean test` al terminar el Observer (maquina de Suriel) -> 49/49 pruebas en verde, BUILD SUCCESS
- `RegistrarVentaServiceTest` -> 4 pruebas en verde (caso de exito notifica; caso de error no notifica)
- `mvn clean test` en main tras los 3 merges -> 53/53 pruebas en verde, BUILD SUCCESS

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #17 | VERIFICADO |
| Rama | `17-observer-venta` | VERIFICADO |
| Commit final declarado | `a12f1db` | VERIFICADO |
| Ultimo commit del PR | `8504927` (hash completo por confirmar) | PENDIENTE |
| Pull Request | #18 (Closes #17) | VERIFICADO |
| Commit de merge | `741e1f6` | VERIFICADO |
| Revision cruzada (autor distinto a quien mergea) | Jared mergeo PR de Suriel | VERIFICADO |
| Comentario de revision antes del merge | "Buen trabajo!" | VERIFICADO |
| Pruebas del patron | `RegistrarVentaServiceTest` (4: exito y error) | VERIFICADO |
| Suite completa en main | 53/53 | VERIFICADO |
| Comparacion de alternativa y costo | Sin declarar | PENDIENTE |
| Etiqueta | `v1.0-p06` | VERIFICADO |

## Identificador final
- ID (commit): `a12f1dbd640bb8597309607f51d00eb916ac627d`
