# Indice de evidencias - P09 API de ventas (equipo TCSW-E01)

Estados: VERIFICADO = hay evidencia; NO_VERIFICADO = no hay evidencia; PENDIENTE = falta hacerlo. Documento completo con capturas: `P09_EQUIPO_01.docx` (Eminus).

## Mapa criterio R01 -> evidencia

| Criterio (pts) | Evidencia | Estado |
|---|---|---|
| Funcionamiento (0.8) | `scripts/verificar-p09.sh`: 21 comprobaciones, 0 fallas (memoria y PostgreSQL con Hibernate, con reinicio de la API); `docs/evidencia/verificar-p09-salida.txt`; curl de los integrantes 1 y 2 | VERIFICADO |
| Solucion tecnica (0.5) | ADR-010; paquete `adapter.in.rest` (controladores, DTO, `ManejadorErrores`); `openapi.yaml`; `ArquitecturaRestTest` | VERIFICADO |
| Pruebas (0.3) | `ProductoControllerTest`, `VentaControllerTest`, `ManejadorErroresTest`, `ContratoApiTest`, `ApiRecorridoTest`, `ApiArranqueTest`, `ApiVentasHibernateIT`; casos de error 400/404/405/409/415 | VERIFICADO (unitarias); integracion: ver resumen final |
| Calidad y Sonar (0.2) | Reporte de Sonar y decision sobre cada hallazgo | VERIFICADO: proyecto P09-API en SonarQube local, analisis del 08-nov sobre 40ecaa0 con pruebas unitarias e integracion. Quality Gate Passed; Seguridad 0, Fiabilidad 0, Hotspots 0, duplicacion 0.0 %, cobertura 73.6 %; 32 issues de mantenibilidad (calificacion A) sin corregir y aceptados por ser de estilo, sin riesgo funcional: Stream.toList, lambdas en pruebas y la palabra "todo" en comentarios |
| Git y evidencia (0.2) | 3 issues, 3 PR, 3 `integrante-N-P09.md`, README de reproduccion, etiqueta `v0.4-api` | VERIFICADO con las limitaciones de la seccion siguiente |

## Trazabilidad por integrante

| Integrante | Issue | Commits | PR | Revision |
|---|---|---|---|---|
| 1 Jared | #40 | `f434539`, `48cdcc7` | #41 (integra Suriel) | comentario de Suriel visible: VERIFICADO |
| 2 Suriel | #42 | `4edb9ef`, `61d9313` | #43 (integra Jesus) | sin comentario de revision humano (Copilot no pudo por cuota): NO_VERIFICADO |
| 3 Jesus | #44 | `52da8a2` | #45 (integra Jared) | <REV45> |

## Limitaciones
- El commit `52da8a2` no lleva el numero de issue y la descripcion del PR #45 dice `Closes #45` (no #44): trazabilidad incompleta en esa parte
- El PR #43 no tiene comentario de revision humano
- La documentacion de cierre se subio directamente a `main`, igual que en C02 (criterio indicado por el docente en ese corte)
