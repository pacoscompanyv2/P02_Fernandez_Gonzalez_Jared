# Contribucion - Integrante 2 (P09)

**Nombre:** Suriel Francisco Tecpile
**GitHub:** SurielFrancisco
**Alcance:** endpoints de ventas (`POST /api/v1/ventas`, `GET /api/v1/ventas/{folio}`), DTO con validacion y pruebas del controlador

## Issue
- **#42** - API REST: registrar y consultar ventas con DTO y validacion
- Criterio de cierre: `POST /api/v1/ventas` devuelve 201 con `Location`; datos invalidos devuelven 400 con detalle por campo; producto inexistente 404; existencia insuficiente y folio repetido 409; `GET /api/v1/ventas/{folio}` 200/404; `mvn clean test` en verde y PR integrado a main

## Rama y commits
- Rama: `api-ventas`
- Commit de codigo: `4edb9ef` - feat: endpoints de ventas con DTO y validacion (#42)

## Decision tecnica
- La forma del JSON se valida en el DTO (`@Valid`, Bean Validation); las reglas de negocio siguen en el caso de uso (ADR-010)
- Se responde 201 + `Location` al registrar; el cuerpo es la venta ya cerrada con su total
- Folio repetido: se consulta antes de registrar y se responde 409 (no 500)
- El controlador solo habla con `VentasFacade`; no conoce repositorios

## Codigo creado o modificado
- `VentaController`, `VentaRequest`, `ItemVentaRequest`, `VentaResponse`, `PartidaResponse`
- Pruebas: `VentaControllerTest`, `VentaResponseTest`

## Pull Request y revision
- PR #43 - "feat: endpoints de ventas con DTO y validacion (#42)" - Closes #42
- Revisor asignado: Jesus Armando Andres Tablilla
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- Unitarias (`mvn clean test`): 161 pruebas

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #42 | VERIFICADO |
| Rama | `api-ventas` | VERIFICADO |
| Commit de codigo | `4edb9ef` | VERIFICADO |
| Pull Request | #43 (Closes #42) | VERIFICADO |
| Pruebas unitarias | 161 | VERIFICADO |
| Recorrido con curl (201, 409, 400, 404) | captura de la terminal | VERIFICADO |
| Comentario de revision antes del merge | revisor: Jesus | NO_VERIFICADO |

## Limitaciones declaradas
- La comprobacion de folio repetido y el registro no son atomicos: dos peticiones simultaneas con el mismo folio podrian llegar a la base (la llave primaria lo detiene, pero la respuesta seria 500). Ver D-API-1 en `integrante-3-P09.md`
- No hay paginacion ni busqueda de ventas por fecha: fuera del alcance de P09
