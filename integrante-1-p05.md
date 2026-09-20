# Contribucion - Integrante 1

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2

## Issue
- **#7** - Definir puertos de entrada y salida
- Criterio de cierre: existen las interfaces de puertos (in/out) y los diagramas de dependencias, PR mergeado a main

## Rama y commits
- Rama vinculada: `1-puertos-hexagonal`
- Commit: `af35d6e814afe9123ddcf270b25bc739994d1251` - feat: definir puertos de entrada y salida y diagramas de dependencias (#7)

## Puertos y diagramas creados
- `application/port/in/ItemVenta.java`
- `application/port/in/RegistrarVentaUseCase.java`
- `application/port/out/ProductoRepository.java`
- `application/port/out/VentaRepository.java`
- `docs/diagramas/diagrama-antes.png` y `docs/diagramas/diagrama-despues.png`

## Pull Request y revision
- PR #8 - "feat: definir puertos de entrada y salida y diagramas de dependencias (#7)" - Closes #7
- Mergeado por: Jesus Armando Andres Tablilla (commit de merge 557a36f)

## Pruebas
- `mvn clean test` -> 41/41 pruebas en verde con el nucleo completo integrado (dominio + puertos + servicio + adaptadores + arquitectura)
- Las interfaces no agregan pruebas propias; se valida que compilan y no rompen ninguna prueba existente

## Identificador final
- ID (commit): `af35d6e814afe9123ddcf270b25bc739994d1251`
