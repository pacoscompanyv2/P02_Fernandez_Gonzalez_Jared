# Contribucion - Integrante 1 (C03)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** todo el corte C03: verificacion integral, correccion de "sin conexion", evidencia de persistencia y de reuso del nucleo, documentacion de cierre y entrega. Los integrantes 2 y 3 no registraron aportes en C03.

## Que hice
- `tcsw-ventas/scripts/verificar-c03.sh`: levanta PostgreSQL, corre `mvn clean verify -Pintegracion`, el recorrido de la API con persistencia tras reiniciar, `ng build` y `ng test`, y el recorrido del cliente por el proxy; guarda tres salidas en `docs/evidencia/`
- Detecte que la primera corrida omitia la persistencia y corregi el script
- Detecte en el navegador que "API apagada" se veia como `ERROR_HTTP` y lo corregi en `cliente-angular/src/app/core/api-error.ts` con 3 pruebas nuevas (de 49 a 51)
- Recorrido en el navegador con exito, vacio y error
- Documentacion de cierre: indice de evidencias, bitacora, protocolo visual y `reuso-nucleo-C03.md`

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Script y salidas | `0a7390c`, `bea54f4` (directo a `main`, sin issue ni PR) | VERIFICADO, sin revision |
| Correccion "sin conexion" | `9170d28` (directo a `main`, sin issue ni PR) | VERIFICADO, sin revision |
| Documentacion de cierre | issue #52, PR #53 | VERIFICADO; fusionado sin revision de otro integrante |
| Base del cliente y API que C03 integra | issue #46, PR #47 (P10) y `integrante-1-P09.md` | VERIFICADO |

## Pruebas (corrida del 18-nov-2026 con `verificar-c03.sh`)

| Prueba | Resultado | Estado |
|---|---|---|
| `mvn clean verify -Pintegracion` | 174 unitarias y 56 de integracion, 0 fallos | VERIFICADO |
| `verificar-p09.sh` completo | 21 comprobaciones, 0 fallas (incluye persistencia tras reiniciar la API en PostgreSQL) | VERIFICADO |
| `ng build` y `ng test` | 9 archivos, 51 pruebas, 0 fallos | VERIFICADO |
| `verificar-p10.sh` | 9 comprobaciones, 0 fallas | VERIFICADO |
| `verificar-c03.sh` completo | 5 pasos, 0 fallas | VERIFICADO |

## Reuso del nucleo (lo que entiendo)
La API y Swing entran por `VentasFacade`; el descuento por mayoreo (5 % con 3 o mas partidas) y el redondeo estan en el dominio. El cliente solo estima el total y la API calcula el final (599.00 estimado contra 569.00). Detalle en `reuso-nucleo-C03.md`.

## Error propio y decision
Un `git commit --amend` ejecutado sobre el commit equivocado dejo mi rama local divergente de GitHub. Lo detecte con `git status` y `git reflog`, comprobe que el contenido era identico y descarte la copia local; no se reescribio historial publicado.

## Limitaciones declaradas
- Script, correccion y parte de la evidencia se subieron directo a `main`, sin issue ni PR
- El PR #53 se fusiono sin revision de otro integrante
- No se ejecuto Sonar en C03; `verify-module.sh M13` (citado en la guia) no existe en el repositorio
- Folio de solo espacios en el formulario de venta: no se probo
- La etiqueta `v0.3-web` y la entrega en Eminus se hacen despues de este documento
