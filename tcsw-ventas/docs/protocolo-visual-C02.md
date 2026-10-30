# Protocolo visual C02 (demostracion de 3 minutos)

Preparacion: `docker compose up -d --wait`, `.env` exportado, `./scripts/ejecutar-app.sh hibernate`.

| Paso | Que se hace | Que debe verse |
|---|---|---|
| 1 | Abrir la aplicacion con Hibernate | Consola: `Almacenamiento: PostgreSQL (Hibernate)`; ventana con pestanas **Vender** y **Consultar venta** |
| 2 | Pestana **Vender**: agregar `P1 x 2`, folio `C02-DEMO-1`, Registrar | El catalogo baja la existencia de P1 en 2 |
| 3 | Pestana **Consultar venta**: buscar `C02-DEMO-1` | Una partida, total y `Estado: CERRADA` |
| 4 | Buscar un folio inexistente y uno vacio | Avisos sin cerrar la aplicacion |
| 5 | Cerrar la aplicacion y abrirla de nuevo | La existencia sigue descontada y la venta se sigue encontrando |
| 6 | `./scripts/demo-antes-despues.sh hibernate` | `ANTES`, `DESPUES`, reinicio de PostgreSQL y `TRAS REINICIAR` con la venta encontrada |
| 7 | `./scripts/verificar-c02.sh` | Esquema desde cero, 117 unitarias y 50 de integracion, demos y `OK` |

Restricciones: no mostrar `.env`, claves, tokens ni rutas privadas; una captura aislada no sustituye la ejecucion de los pasos 6 y 7.
Estado en esta entrega: pasos 6 y 7 VERIFICADO; pasos 1 a 5 NO_VERIFICADO (no hay capturas de la ventana consultando una venta).
