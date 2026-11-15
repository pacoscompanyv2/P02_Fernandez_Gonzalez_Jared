# Contribucion - Integrante 1 (C03)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** verificacion integral de la integracion web, evidencia de reuso del nucleo, documentacion de cierre y coordinacion del reparto

## Que hice
- `tcsw-ventas/scripts/verificar-c03.sh`: levanta PostgreSQL, corre `mvn clean verify -Pintegracion`, el recorrido HTTP de la API, `ng build` y `ng test`, y el recorrido del cliente por el proxy; guarda `docs/evidencia/verificar-c03-salida.txt`
- Corrida completa en mi maquina (14-nov): 5 pasos, 0 fallas
- Recorrido en el navegador con exito, vacio y error (capturas en el Word)
- Detecte el hueco "API apagada se muestra como `ERROR_HTTP`" y prepare el parche que aplica Suriel
- Documentacion de cierre: indice de evidencias, bitacora, protocolo visual y `reuso-nucleo-C03.md`

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue de la documentacion de cierre | #52 | VERIFICADO |
| Pull request de la documentacion | enlazado desde #52 (`Closes #52`) | VERIFICADO al abrirse |
| Revision de un companero | en el PR | PENDIENTE |
| Script y su salida | commit `0a7390c`, directo a `main`, sin issue ni PR | VERIFICADO (sin revision) |
| Base del cliente y API (que C03 integra) | issue #46, PR #47 (P10) y `integrante-1-P09.md` | VERIFICADO |

## Pruebas

| Prueba | Resultado | Estado |
|---|---|---|
| `mvn clean verify -Pintegracion` | 174 unitarias y 56 de integracion, 0 fallos | VERIFICADO |
| `verificar-p09.sh --solo-memoria` | 14 comprobaciones, 0 fallas | VERIFICADO |
| `ng build` y `ng test` | 9 archivos, 49 pruebas, 0 fallos | VERIFICADO |
| `verificar-p10.sh` | 9 comprobaciones, 0 fallas | VERIFICADO |
| `verificar-c03.sh` completo | 5 pasos, 0 fallas | VERIFICADO |
| Parche de "sin conexion" | 51 pruebas en verde en local; no integrado a `main` | NO_VERIFICADO en `main` |

## Reuso del nucleo (lo que entiendo)
La API y Swing entran por `VentasFacade`; el descuento por mayoreo y el redondeo estan en el dominio. El cliente solo estima el total y la API calcula el final (599.00 estimado contra 569.00 final). Detalle en `reuso-nucleo-C03.md`.

## Limitaciones declaradas
- El script y su salida se subieron directo a `main`, sin issue ni PR
- La corrida guardada es sobre `08497b8`, antes de agregar el script a `main`
- No se ejecuto Sonar en C03
- La etiqueta `v0.3-web` y la entrega en Eminus siguen PENDIENTES
- Lo de Suriel y Jesus esta PENDIENTE hasta que lo ejecuten
