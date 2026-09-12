# Contribucion - Integrante 1

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2

## Issue
- **#1** - Agregar Dinero.aplicarRecargo(BigDecimal)
- Criterio de cierre: existe el metodo con validacion y pruebas en verde, PR mergeado a main

## Rama y commits
- Rama vinculada: `1-recargo-fijo`
- Commit: `ce3d4663a96d919b588fc91f93849367a01e4cfe` - feat: aplicar recargo fijo a Dinero (#1)

## Pull Request y revision
- PR #4 - "feat: aplicar recargo fijo a Dinero (#1)" - Closes #1
- Mergeado por: Jesus Armando Andres Tablilla (commit de merge 942174c)

## Pruebas
- `mvn clean test` -> 33/33 pruebas en verde (incluye aplicaRecargoCorrectamente y rechazaRecargoNegativo)
- Caso normal: aplica recargo correctamente
- Caso error: rechaza recargo negativo (IllegalArgumentException)
