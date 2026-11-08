# Contribucion - Integrante 1 (P09)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** base de la API REST: Spring Boot, composicion, consulta de productos, formato de error unico, excepciones tipadas y ADR-010

## Issue
- **#40** - API REST base: Spring Boot, productos y errores consistentes
- Criterio de cierre: `GET /api/v1/productos` y `/{codigo}` funcionando; todo error con el mismo formato; excepciones tipadas del caso de uso; ArchUnit del borde REST; `mvn clean test` en verde y PR integrado a main

## Rama y commits
- Rama: `api-base-productos`
- Commit de codigo: `f434539` - feat: API REST base con Spring Boot, productos y errores consistentes (#40)

## Decision tecnica
- La API es un adaptador de entrada (`adapter.in.rest`) sobre el mismo `VentasFacade` que usa Swing (ADR-010)
- Un unico `ManejadorErrores` produce el mismo `ErrorResponse` para negocio, validacion, JSON roto, 404/405/415 y 500
- `ProductoNoEncontradoException` y `ExistenciaInsuficienteException` permiten responder 404/409 sin leer textos; heredan de las excepciones anteriores y no rompen pruebas previas

## Codigo creado o modificado
- `ApiApplication`, `ConfiguracionApi`, `ProductoController`, `ProductoResponse`, `ErrorResponse`, `ManejadorErrores`, `CodigoError` (catalogo de codigos), `VentaNoEncontradaException`, `FolioDuplicadoException`
- `application.exception.*` y `RegistrarVentaService` (lanza las excepciones tipadas)
- `pom.xml` (Spring Boot 3.2.4, Java 17), `application.properties`, `scripts/ejecutar-api.sh`
- Pruebas: `ProductoControllerTest`, `ManejadorErroresTest`, `RegistrarVentaExcepcionesTest`, `ArquitecturaRestTest`, `ApiArranqueTest`
- `docs/adr/ADR-010-api-rest-spring-boot.md`

## Pull Request y revision
- PR #41 - "feat: API REST base con Spring Boot, productos y errores consistentes (#40)" - Closes #40
- Revisor asignado: Suriel Francisco Tecpile
- Comentario de revision explicito en GitHub antes del merge: VERIFICADO

## Pruebas
- Unitarias (`mvn clean test`): 141 pruebas

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #40 | VERIFICADO |
| Rama | `api-base-productos` | VERIFICADO |
| Commit de codigo | `f434539` | VERIFICADO |
| Pull Request | #41 (Closes #40) | VERIFICADO |
| Pruebas unitarias | 141 | VERIFICADO |
| API probada con curl (GET productos, 404) | captura de la terminal | VERIFICADO |
| Comentario de revision antes del merge | revisor: Suriel | VERIFICADO |

## Limitaciones declaradas
- Sin autenticacion: la API escucha solo en 127.0.0.1 (fuera del alcance de P09)
- El almacenamiento en memoria no es seguro entre hilos; para varias peticiones simultaneas se usa PostgreSQL
