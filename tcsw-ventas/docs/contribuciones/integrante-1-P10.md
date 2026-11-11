# Contribucion - Integrante 1 (P10)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** base del cliente Angular: proyecto, proxy a la API, modelos, servicios HTTP, manejo de errores, catalogo con busqueda, menu y rutas, ADR-011

## Issue
- **#46** - Cliente Angular base: catalogo y servicios HTTP
- Criterio de cierre: `ng build` y `ng test` en verde; catalogo mostrando los productos reales de la API por el proxy; errores de la API y "sin conexion" mostrados con un mensaje claro; ADR-011; PR integrado a main

## Rama y commits
- Rama: `cliente-base-catalogo`
- Commit de codigo: `0c5fb0c` - feat: cliente Angular base con catalogo y servicios HTTP (#46)

## Decision tecnica
- Proxy de desarrollo en lugar de CORS: la API no cambia (ADR-011)
- Servicios HTTP delgados y un unico `describirError` para todos los errores
- Rutas con carga diferida; `carrito`, `venta` y `consulta` son marcadores que reemplazan los integrantes 2 y 3

## Codigo creado o modificado
- `cliente-angular/` (generado con `ng new`), `proxy.conf.json`, `angular.json` (proxyConfig)
- `core/`: `models`, `api.config`, `api-error`, `productos.service`, `ventas.service`, `carrito.service` (version minima)
- `catalogo/catalogo`, `app`, `app.config`, `app.routes`
- Pruebas: `api-error.spec`, `productos.service.spec`, `ventas.service.spec`, `carrito.service.spec`, `catalogo.spec`, `app.spec`
- `docs/adr/ADR-011-cliente-angular.md`

## Pull Request y revision
- PR #47 - "feat: cliente Angular base con catalogo y servicios HTTP (#46)" - Closes #46
- Revisor asignado: Suriel Francisco Tecpile
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- `ng test`: 25 pruebas

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #46 | VERIFICADO |
| Rama | `cliente-base-catalogo` | VERIFICADO |
| Commit de codigo | `0c5fb0c` | VERIFICADO |
| Pull Request | #47 (Closes #46) | VERIFICADO |
| `ng build` sin errores | salida de la terminal | VERIFICADO |
| `ng test`: 25 pruebas, 0 fallos | salida de la terminal | VERIFICADO |
| Catalogo con datos reales de la API (navegador) | captura | VERIFICADO |
| Comentario de revision antes del merge | revisor: Suriel | NO_VERIFICADO |

## Limitaciones declaradas
- El proxy solo funciona con `ng serve`; no hay despliegue de produccion
- Los modelos son un espejo manual del contrato; sin generacion automatica
- Sin autenticacion (la API tampoco la tiene)
