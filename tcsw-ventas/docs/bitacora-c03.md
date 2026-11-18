# Bitacora C03 - Integracion web (equipo TCSW-E01)

| Fecha | Hecho | Referencia |
|---|---|---|
| 2026-11-12 | Abre la ventana de C03 (cierre jueves 19-nov 23:45, sin prorroga) | guia de la actividad |
| 2026-11-13 | Se analiza la guia contra lo ya construido: la API (P09) y el cliente (P10) ya estan integrados; faltan la verificacion integral, la evidencia de reuso del nucleo y la trazabilidad por integrante | analisis del equipo |
| 2026-11-14 | Se crea `verificar-c03.sh` (5 pasos). En la primera corrida el puerto 4200 estaba ocupado por un `ng serve` del propio integrante 1; se detuvo y se repitio | terminal |
| 2026-11-14 | Corrida completa del script: 5 pasos, 0 fallas (174 unitarias, 56 de integracion, 14 + 9 comprobaciones HTTP, 49 pruebas del cliente) | `docs/evidencia/verificar-c03-salida.txt` |
| 2026-11-14 | Script y su salida a `main`, directo y sin issue; se agrega `cliente-angular/.scannerwork/` a `.gitignore` | `0a7390c` |
| 2026-11-14 | Recorrido en el navegador (integrante 1): catalogo, carrito, venta, consulta, errores. Hallazgo: con la API apagada el cliente muestra `ERROR_HTTP` (500 del proxy) en lugar de "sin conexion" | capturas en el Word |
| 2026-11-14 | Se prepara el parche de la correccion (51 pruebas en verde en local) y se entrega a Suriel con su plantilla; Jesus recibe la revision y su prueba propia | paquete de reparto |
| 2026-11-14 | Issue #52 para la documentacion de cierre | #52 |
| (pendiente) | PR de Suriel, revision de Jesus, corridas propias del script, etiqueta `v0.3-web` y entrega en Eminus | por completar |
