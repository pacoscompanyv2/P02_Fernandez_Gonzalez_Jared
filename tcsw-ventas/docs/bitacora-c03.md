# Bitacora C03 - Integracion web (equipo TCSW-E01)

| Fecha | Hecho | Referencia |
|---|---|---|
| 2026-11-12 | Abre la ventana de C03 (cierre jueves 19-nov 23:45, sin prorroga) | guia de la actividad |
| 2026-11-13 | Analisis de la guia contra lo construido: API (P09) y cliente (P10) ya integrados; faltan verificacion integral, evidencia de reuso del nucleo y documentacion | analisis del equipo |
| 2026-11-14 | Se crea `verificar-c03.sh` (5 pasos). Primera corrida con el puerto 4200 ocupado por un `ng serve` propio; se detuvo y se repitio. Resultado: 5 pasos, 0 fallas (con `--solo-memoria` en el paso 3) | terminal |
| 2026-11-14 | Script y salida a `main`, directo y sin issue; `cliente-angular/.scannerwork/` a `.gitignore` | `0a7390c` |
| 2026-11-14 | Recorrido en el navegador. Hallazgo: con la API apagada el cliente mostraba `ERROR_HTTP` (500 del proxy) en lugar de "sin conexion" | capturas en el Word |
| 2026-11-14 | Issue #52 y PR #53 con la documentacion de cierre | #52, #53 |
| 2026-11-18 | El PR #53 se fusiona sin revision de otro integrante. Los integrantes 2 y 3 no registraron aportes en C03; el integrante 1 asume el resto del corte | merge `477c0d2` |
| 2026-11-18 | Correccion "sin conexion": 51 pruebas en verde; directo a `main`, sin issue | `9170d28` |
| 2026-11-18 | Se detecta que la corrida de C03 omitia la persistencia (`--solo-memoria`); el script pasa a correr `verificar-p09.sh` completo cuando hay Docker y guarda las tres salidas | `bea54f4` |
| 2026-11-18 | Corrida final: 5 pasos, 0 fallas (174 unitarias, 56 de integracion, 21 comprobaciones de la API con persistencia tras reiniciar, 51 pruebas del cliente, 9 por el proxy). `SIN_CONEXION` visto en el navegador | `docs/evidencia/verificar-c03-*.txt` |
| 2026-11-18 | Un `git commit --amend` sobre el commit equivocado dejo la rama local divergente; se detecto con `git reflog`, se descarto la copia local y `origin/main` conservo los commits correctos | reflog |
| (pendiente) | Actualizacion de documentos, etiqueta `v0.3-web` y entrega en Eminus | por completar |
