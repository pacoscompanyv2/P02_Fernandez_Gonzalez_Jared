# Contribucion - Integrante 1 (P06)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Patron:** Strategy + Factory (descuento de Venta)

## Issue
- **#15** - Aplicar Strategy y Factory al descuento de Venta
- Criterio de cierre: existen la estrategia de descuento, su fabrica, y Venta usa la estrategia en vez del `if` hardcodeado, con pruebas de exito, error y comparacion de alternativa, PR mergeado a main
- Estado: Closed (cerrado por el merge del PR #16)

## Rama y commits
- Rama vinculada: `15-aplicar-strategy-y-factory-al-descuento-de-venta`
- Commit: `fb193333d4406569650889b591887306da239c86` - feat: aplicar Strategy y Factory al descuento de Venta (#15)
- Alcance del commit: 6 archivos cambiados (+130 / -21)

## Decision de patron
- Variacion concreta: la regla de descuento de `Venta`
- Alternativa comparada: `if` hardcodeado dentro de `Venta` (lo que el issue pide reemplazar)
- Patron elegido: Strategy (una estrategia por regla de descuento) + Factory (elige la estrategia)
- Costo declarado: mas archivos y clases que un `if` simple (6 archivos, +130 / -21); se justifica porque agregar un descuento nuevo ya no obliga a modificar `Venta`

## Codigo creado o modificado
- `domain/descuento/DescuentoStrategy.java`
- `domain/descuento/DescuentoPorMayoreo.java`
- `domain/descuento/SinDescuento.java`
- `domain/descuento/DescuentoStrategyFactory.java`
- `domain/Venta.java` (usa la estrategia en lugar del `if`)
- Prueba: `domain/descuento/DescuentoStrategyTest.java`

## Pull Request y revision
- PR #16 - "feat: aplicar Strategy y Factory al descuento de Venta (#15)" - Closes #15
- Mergeado por: Jesus Armando Andres Tablilla (commit de merge 8801690)
- Comentario de revision explicito antes del merge: NO (merge directo; solo hay un comentario del autor, "Modificaciones agregadas")

## Pruebas
- `DescuentoStrategyTest` -> 6 pruebas en verde (intercambio y extension de estrategia)
- `mvn clean test` en main tras los 3 merges -> 53/53 pruebas en verde, BUILD SUCCESS

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #15 | VERIFICADO |
| Rama | `15-aplicar-strategy-y-factory-al-descuento-de-venta` | VERIFICADO |
| Commit | `fb19333` | VERIFICADO |
| Pull Request | #16 (Closes #15) | VERIFICADO |
| Commit de merge | `8801690` | VERIFICADO |
| Revision cruzada (autor distinto a quien mergea) | Jesus mergeo PR de Jared | VERIFICADO |
| Comentario de revision antes del merge | No hubo | NO_VERIFICADO |
| Pruebas del patron | `DescuentoStrategyTest` (6) | VERIFICADO |
| Suite completa en main | 53/53 | VERIFICADO |
| Etiqueta | `v1.0-p06` | VERIFICADO |

## Identificador final
- ID (commit): `fb193333d4406569650889b591887306da239c86`
