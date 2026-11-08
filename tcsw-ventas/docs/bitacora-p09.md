# Bitacora P09 - API de ventas (equipo TCSW-E01)

| Fecha | Hecho | Referencia |
|---|---|---|
| 2026-11-03 | Se define el reparto: base de la API (integrante 1), ventas (integrante 2), contrato y pruebas de extremo a extremo (integrante 3); se decide Spring Boot 3.2.4 con Java 17 como adaptador de entrada | ADR-010 |
| 2026-11-04 | Issue #40; primera corrida: 3 pruebas fallaban con 400 porque Spring no leia `@PathVariable` sin `-parameters`; se corrige con `maven.compiler.parameters=true` y nombres explicitos | commit `f434539` |
| 2026-11-04 | Documentacion del integrante 1 y ADR-010; el PR #41 se integra con el comentario de revision de Suriel | `48cdcc7`, merge `f1650ce` |
| 2026-11-06 | Issue #42, endpoints de ventas con validacion; 161 pruebas en verde; PR #43 integrado por Jesus (sin comentario de revision humano) | `4edb9ef`, `61d9313`, merge `430f12f` |
| 2026-11-06 | Issue #44; contrato OpenAPI, prueba de contrato y `verificar-p09.sh`: 21 comprobaciones, 0 fallas; 174 pruebas unitarias | `52da8a2` |
| 2026-11-07 | PR #45 integrado por Jared | merge `40ecaa0` |
| 2026-11-08 | Cierre: pruebas de integracion, Sonar, documentacion final y etiqueta `v0.4-api` | <HASH_FINAL> |
