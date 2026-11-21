# Indice de evidencias C04 contra R05 (8 pts)

Estados: VERIFICADO (hay salida o captura que lo muestra), NO_VERIFICADO (no se pudo), PENDIENTE (aun no se ejecuta).
Rama de trabajo: `feat/c04-stack` (issue #54, PR #55). Commits de referencia en `contribuciones/integrante-1-C04.md`.

| Criterio R05 | Pts | Evidencia | Estado |
|---|---|---|---|
| Integracion | 2 | `scripts/verify-stack.sh` (11 comprobaciones, 0 fallas), `docs/evidencia/verify-stack-salida.txt`, `matriz-fronteras-C04.md` | VERIFICADO |
| Pruebas | 1.5 | 174 unitarias + 56 de integracion (API) y 51 del cliente, 0 fallas; `riesgos-y-pruebas-C04.md` (matriz riesgo -> prueba, huecos declarados) | VERIFICADO (Swing sin pruebas automaticas: declarado) |
| Sonar | 1 | `tratamiento-sonar-C04.md`; `docs/evidencia/c04-despues-del-parche-salida.txt`; Overviews de `apicc4` y `frontcc4` | VERIFICADO sobre el commit del parche; analisis final sobre el commit del tag PENDIENTE |
| Integracion continua | 1 | `.github/workflows/ci.yml`; corridas verdes en GitHub Actions (PR #55) | VERIFICADO; corrida final sobre `main` PENDIENTE |
| Documentacion | 1 | `README`, `despliegue-v1.0-rc.md`, `notas-version-v1.0-rc.md` | PENDIENTE hasta el commit final de documentos |
| Reproducibilidad | 1 | job `stack` del CI (VERIFICADO); corrida desde clon limpio con `./scripts/verify-module.sh M14` | clon limpio PENDIENTE |
| Version | 0.5 | tag anotado `v1.0-rc`; versiones fijas en `despliegue-v1.0-rc.md` | PENDIENTE (tag aun no creado) |
| Reversa (evidencia minima) | - | `plan-reversa-v1.0-rc.md`, `docs/evidencia/probar-reversa-salida.txt` (`REVERSA_VERIFICADA`) | VERIFICADO |

No verificado, declarado: `verify-module.sh` OFICIAL de la materia (no disponible; se uso un script propio solo para M14);
contribuciones de los integrantes 2 y 3 (no hubo).
