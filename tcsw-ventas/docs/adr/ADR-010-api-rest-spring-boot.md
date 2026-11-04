# ADR-010: API REST con Spring Boot como adaptador de entrada (P09)

**Estado:** aceptada (P09)  **Equipo:** TCSW-E01

## Contexto
P09 pide una API basica de ventas con Spring Boot: controladores, DTO, validacion, contrato, errores consistentes y pruebas. El nucleo (domain/application) ya esta aislado por puertos (ADR-000) y tiene tres adaptadores de salida (memoria, JDBC, Hibernate). Hace falta un segundo adaptador de ENTRADA, junto a Swing.

## Decision
| Tema | Decision | Motivo |
|---|---|---|
| Ubicacion | paquete `adapter.in.rest` en el mismo modulo Maven | un solo `pom`, un solo Sonar; Swing y REST comparten el mismo `VentasFacade` |
| Framework | Spring Boot 3.2.4 (traen Hibernate 6.4.x, la misma version que ya usamos) y **Java 17** (Spring Boot 3 lo exige; antes 11) | version estable y compatible con lo existente |
| Spring solo en el borde | `domain` y `application` siguen siendo Java puro; ArchUnit lo comprueba (`ArquitecturaRestTest`) | el nucleo no se ata al framework |
| Composicion | `ConfiguracionApi` crea el `VentasFacade` con el mismo criterio que `Main` de Swing (`TCSW_PERSISTENCIA`, `TCSW_DB_*`, memoria); es la unica clase REST que ve `adapter.out` | no se duplica logica de negocio |
| DTO | `record` por cada entrada/salida; el dominio nunca se serializa | el contrato lo controla el DTO, no `Producto`/`Venta` |
| Errores | un `ManejadorErrores` (`@RestControllerAdvice`) y un solo formato `ErrorResponse` (`status, error, codigo, mensaje, ruta, timestamp, detalles`) para TODO: negocio, validacion, JSON roto, 404/405/415 y 500 | el cliente programa contra un solo formato; el 500 no filtra la causa interna |
| Excepciones tipadas | `ProductoNoEncontradoException` (hereda `IllegalArgumentException`) y `ExistenciaInsuficienteException` (hereda `IllegalStateException`) en `application.exception` | distinguir 404/409 sin leer el texto del mensaje, sin romper pruebas previas |
| Version de la API | prefijo `/api/v1` | permite evolucionar sin romper clientes |
| Contrato | `openapi.yaml` escrito a mano (contract-first), servido en `/openapi.yaml` y verificado por prueba contra los controladores reales | evita que el contrato y el codigo se separen |

Codigos de estado: 200 consulta, 201 + `Location` al registrar, 400 validacion, 404 no existe, 405 metodo, 409 conflicto (existencia insuficiente, folio repetido), 415 tipo de contenido, 500 inesperado.

## Consecuencias
- (+) Swing y REST usan exactamente el mismo caso de uso y las mismas reglas.
- (+) Las pruebas de controlador corren sin servidor ni base (MockMvc + Mockito).
- (-) Subir a Java 17 obliga a quien tenga JDK 11 a actualizarlo.
- (-) La comprobacion de folio repetido (`buscarVenta` antes de registrar) deja una ventana de carrera entre dos peticiones simultaneas con el mismo folio: la llave primaria de PostgreSQL lo detiene, pero la respuesta seria 500 y no 409 (deuda D-API-1 en `integrante-3-P09.md`).
- (-) La API no tiene autenticacion: escucha solo en `127.0.0.1`. Fuera del alcance de P09.
