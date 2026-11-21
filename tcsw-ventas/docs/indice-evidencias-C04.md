# Indice de evidencias C04 contra R05 (8 pts)

Estados: VERIFICADO (hay salida o captura que lo muestra), NO_VERIFICADO (no se pudo), PENDIENTE (aun no se ejecuta). Se actualiza conforme se corre cada cosa.

| Criterio R05 | Pts | Evidencia | Estado |
|---|---|---|---|
| Integracion | 2 | `scripts/verify-stack.sh`, `docs/evidencia/verify-stack-salida.txt`, `matriz-fronteras-C04.md` | PENDIENTE |
| Pruebas | 1.5 | reporte de pruebas (API y cliente), `riesgos-y-pruebas-C04.md` | PENDIENTE |
| Sonar | 1 | analisis sobre el commit rc y `tratamiento-sonar-C04.md` | PENDIENTE |
| Integracion continua | 1 | `.github/workflows/ci.yml` y corrida en GitHub Actions | PENDIENTE |
| Documentacion | 1 | `README`, `despliegue-v1.0-rc.md`, `notas-version-v1.0-rc.md` | PENDIENTE (borrador) |
| Reproducibilidad | 1 | corrida desde clon limpio (`bitacora-c04.md`) y job `stack` del CI | PENDIENTE |
| Version | 0.5 | tag `v1.0-rc` y versiones fijas | PENDIENTE |
| Reversa (evidencia minima) | - | `plan-reversa-v1.0-rc.md`, `docs/evidencia/probar-reversa-salida.txt` | PENDIENTE |

No verificado desde ya: `verify-module.sh` oficial de la materia (no disponible); contribuciones de los integrantes 2 y 3 (no hubo).
