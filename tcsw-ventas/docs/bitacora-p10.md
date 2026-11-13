# Bitacora P10 - Cliente Angular (equipo TCSW-E01)

| Fecha | Hecho | Referencia |
|---|---|---|
| 2026-11-09 | Se define el reparto: base y catalogo (integrante 1), carrito (integrante 2), venta, consulta y recorrido (integrante 3); se decide Angular 21 con proxy de desarrollo en lugar de CORS para no tocar la API | ADR-011 |
| 2026-11-10 | Issue #46; `ng new` fallo con un error de `npm install` (`edgesOut`); se resuelve con `legacy-peer-deps=true` en `.npmrc`. Catalogo, servicios, `describirError` y ADR-011; 25 pruebas | `0c5fb0c`, `59a2ed8` |
| 2026-11-10 | PR #47 integrado por Suriel (sin comentario de revision) | merge `bf5d1b0` |
| 2026-11-10 | Issue #48; carrito con limite por existencia y total estimado; 37 pruebas. PR #49 integrado por Jesus (sin comentario de revision) | `a214b41`, `e7bc562`, merge `2aed823` |
| 2026-11-11 | Issue #50; formulario de venta, consulta por folio y `verificar-p10.sh` (9 comprobaciones, 0 fallas); 49 pruebas. El commit de codigo quedo sin numero de issue | `ac33f16`, `9d7cf48` |
| 2026-11-11 | PR #51 integrado por Jared | merge `8cdef29` |
| 2026-11-12 | Verificacion final sobre `main`: `ng build` y `ng test` (49) en verde. El puerto 4200 estaba ocupado por un contenedor de otro proyecto; se detuvo durante la prueba y se volvio a iniciar | salida de la terminal |
| 2026-11-12 | `verificar-p10.sh` daba 9/0 pero dejaba la API y `ng serve` vivos (el `kill` por grupo fallaba en silencio con `setsid`), lo que bloqueaba una segunda corrida. Se corrige limpiando por puerto de escucha; directo a `main`, sin issue ni PR | `23c7df2` |
| 2026-11-12 | Cierre: documentacion final, Sonar del cliente (PENDIENTE) y etiqueta | etiqueta de cierre (ver git tag) |
