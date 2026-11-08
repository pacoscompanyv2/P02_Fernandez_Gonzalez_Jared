# Protocolo visual P09 - capturas que respaldan el documento

| # | Captura | Quien | Que debe verse |
|---|---|---|---|
| 1 | Issue #40, PR #41, commits `f434539` y `48cdcc7` | Integrante 1 | titulo, `Closes #40`, merge por Suriel y su comentario |
| 2 | `mvn clean test` (141) y API en marcha | Integrante 1 | clases de la API con 0 fallos; `Almacenamiento: memoria` |
| 3 | 4 respuestas con curl (200, 200, 404, 405) | Integrante 1 | mismo formato de error en 404 y 405 |
| 4 | Issue #42, PR #43, commit `61d9313` | Integrante 2 | `Closes #42`; solicitud de revision a Jesus |
| 5 | `mvn clean test` (161) | Integrante 2 | `BUILD SUCCESS`, 0 fallos |
| 6 | curl de ventas: 201, 200, 409, 400, 404, 409, 404 | Integrante 2 | `Location`, `CERRADA`, `detalles` por campo |
| 7 | Issue #44, PR #45 | Integrante 3 | PR vinculado al issue |
| 8 | `mvn clean test` (174) y codigo de las 3 pruebas | Integrante 3 | `BUILD SUCCESS` |
| 9 | `verificar-p09.sh` completo | Integrante 3 | fases A y B, `21 comprobaciones, 0 fallas` |
| 10 | `mvn clean verify -Pintegracion` sobre `main` | Integrante 1 | `Tests run` y `BUILD SUCCESS` |
| 11 | Sonar: Quality Gate y lista de hallazgos | Integrante 1 | proyecto `p09-api` |
| 12 | `git log --oneline` y etiqueta `v0.4-api` | Integrante 1 | hash final y etiqueta |

Reglas: sin `.env`, sin claves, sin tokens; los hallazgos de Sonar se atienden o se aceptan con motivo.
