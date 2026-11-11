# Contribucion - Integrante 2 (P10)

**Nombre:** Suriel Francisco Tecpile
**GitHub:** SurielFrancisco
**Alcance:** carrito del cliente Angular: servicio con signals (limite por existencia, cambiar cantidad, quitar, vaciar, total estimado, armado de la solicitud) y su pantalla

## Issue
- **#48** - Cliente Angular: carrito
- Criterio de cierre: carrito que no deja pasar de la existencia, con total estimado y listo para armar la solicitud de venta; pruebas del servicio y de la pantalla en verde; PR integrado a main

## Rama y commits
- Rama: `cliente-carrito`
- Commit de codigo: `a214b41` - feat: carrito del cliente Angular (#48)

## Decision tecnica
- El estado del carrito vive en un servicio con signals; el catalogo, el menu, la pantalla del carrito y el formulario de venta lo comparten
- Ninguna regla de negocio en el cliente: solo se evita pedir mas de la existencia mostrada y mas del limite de la API (10000). Existencias, descuento y folio los decide la API
- El total que se muestra es una estimacion (sin descuentos): el total final llega en la respuesta de la API
- `aRequest(folio)` arma el cuerpo de `POST /api/v1/ventas`, asi el formulario del integrante 3 no conoce la forma interna del carrito

## Codigo creado o modificado
- `core/carrito.service.ts` (completa la version minima de la base)
- `carrito/carrito.ts` (reemplaza el marcador)
- Pruebas: `core/carrito.service.spec.ts`, `carrito/carrito.spec.ts`

## Pull Request y revision
- PR #49 - "feat: carrito del cliente Angular (#48)" - Closes #48
- Revisor asignado: Jesus Armando Andres Tablilla
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- `ng test`: 37 pruebas (todo el proyecto)

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #48 | VERIFICADO |
| Rama | `cliente-carrito` | VERIFICADO |
| Commit de codigo | `a214b41` | VERIFICADO |
| Pull Request | #49 (Closes #48) | VERIFICADO |
| `ng test`: 37 pruebas, 0 fallos | salida de la terminal | VERIFICADO |
| Carrito probado en el navegador (agregar, +/-, quitar, tope de existencia) | capturas | VERIFICADO |
| Comentario de revision antes del merge | revisor: Jesus | NO_VERIFICADO |

## Limitaciones declaradas
- El total mostrado no incluye descuentos; el real lo calcula la API
- El carrito vive en memoria: se pierde al recargar la pagina (no hay almacenamiento local)
- La existencia que se valida es la que mostro el catalogo al cargarse; si otra venta la agota, la API responde 409 al enviar
