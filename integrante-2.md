# Contribucion - Integrante 2

**Nombre:** Suriel Francisco Tecpile
**GitHub:** SurielFrancisco

## Issue
- **#2** - Agregar Producto.actualizarPrecio(double)
- Criterio de cierre: metodo con validacion y pruebas en verde, PR mergeado

## Rama y commits
- Rama vinculada: `2-actualizar-precio`
- Commit: `c05bf3d38671545879e017f5b0b0f3441b120ed0` - feat: actualizar precio de Producto con validacion (#2)

## Pull Request y revision
- PR #5 - "feat: actualizar precio de Producto con validacion (#2)" - Closes #2
- Mergeado por: Jared Fernandez Gonzalez (commit de merge ba59f26)

## Pruebas
- `mvn clean test` -> 33/33 pruebas en verde (incluye actualizaPrecioCorrectamente y rechazaPrecioCeroONegativo)
- Caso normal: actualiza el precio correctamente
- Caso error: rechaza precio cero o negativo (IllegalArgumentException)
