# Contribucion - Integrante 3

**Nombre:** Jesus Armando Andres Tablilla
**GitHub:** JesusAndres765

## Issue
- **#3** - Agregar Venta.cancelarPartida(int)
- Criterio de cierre: metodo con validacion de rango y pruebas en verde, PR mergeado

## Rama y commits
- Rama vinculada: `3-cancelar-partida`
- Commit: `73c7ac49e153f55a4e548e5f23e763ed8fd9a498` - feat: cancelar partida de Venta por indice (#3)

## Pull Request y revision
- PR #6 - "feat: cancelar partida de Venta por indice (#3)" - Closes #3
- Mergeado por: Suriel Francisco Tecpile (commit de merge 7787877)

## Pruebas
- `mvn clean test` -> 33/33 pruebas en verde
- Caso normal: cancela partida correctamente
- Caso error: rechaza indice de partida invalido (IndexOutOfBoundsException)
