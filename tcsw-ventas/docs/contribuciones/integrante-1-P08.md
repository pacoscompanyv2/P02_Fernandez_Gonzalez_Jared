# Contribucion - Integrante 1 (P08)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** base de la persistencia: PostgreSQL 16 en Docker Compose, esquema SQL, configuracion por entorno, `ProductoRepositoryJdbc`, regla ArchUnit y documentacion (ADR-006 y guia de reproduccion)

## Issue
- **#28** - Base de persistencia: PostgreSQL 16, esquema SQL y ProductoRepositoryJdbc
- Criterio de cierre: existen `docker-compose.yml`, `db/schema.sql`, configuracion por variables de entorno y `ProductoRepositoryJdbc` (implementa `ProductoRepository` con `PreparedStatement`); una regla ArchUnit impide que `domain` y `application` usen JDBC; `mvn clean test` en verde y PR integrado a main

## Rama y commits
- Rama: `jdbc-base-postgres`
- Commit de codigo: `4a446fd` - feat: base de persistencia PostgreSQL y ProductoRepositoryJdbc (#28)
- Commit de documentacion: ver historial del PR #27 (docs: contribucion integrante 1, ADR-006 y guia P08)

## Decision de arquitectura
- JDBC puro con `PreparedStatement` (sin ORM); detalle completo y alternativas en `docs/adr/ADR-006-persistencia-jdbc-en-adaptadores.md`
- El adaptador implementa el **puerto existente** `ProductoRepository`; `domain` y `application` no se modificaron
- Integridad en dos capas: validaciones del dominio + `CHECK`/`PRIMARY KEY`/`FOREIGN KEY` en `db/schema.sql`
- Credenciales solo por variables de entorno (`.env` fuera del repositorio; se versiona `.env.example`)
- Base de pruebas separada (`*_test`) para que las pruebas de integracion no borren datos de la aplicacion

## Codigo creado o modificado
- `docker-compose.yml`, `.env.example`, `db/schema.sql`, `db/02-base-pruebas.sh`
- `adapter/out/jdbc/ConexionJdbc.java`, `ConfiguracionJdbc.java`, `RepositorioException.java`, `ProductoRepositoryJdbc.java`
- `pom.xml` (driver `org.postgresql:postgresql` 42.7.4 y perfil `integracion` con failsafe + JaCoCo)
- `scripts/resumen-pruebas.sh`, `.gitignore` (`.env`, `.vscode/`)
- Pruebas: `ConfiguracionJdbcTest` (5), `ProductoRepositoryJdbcValidacionTest` (4), `ArquitecturaPersistenciaTest` (2), `EsquemaPostgresIT` (3), base `PostgresTestBase`
- Documentacion: ADR-006 y bloque P08 de `README-reproduccion.md`

## Pull Request y revision
- PR #27 - "feat: base de persistencia PostgreSQL y ProductoRepositoryJdbc (#28)" - Closes #28
- Revisor asignado: Suriel Francisco Tecpile
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO (PR #27 mergeado por Jesus sin comentario de revision en GitHub)

## Pruebas
- Unitarias (`mvn clean test`, no requieren Docker): 96 pruebas, 0 fallos, 0 errores
- Integracion (`mvn clean verify -Pintegracion` contra PostgreSQL 16 en Docker): 3 pruebas (`EsquemaPostgresIT`), 0 fallos, 0 errores
- Caso de error cubierto: la base rechaza una existencia negativa (SQLSTATE 23514); la configuracion sin variables falla con un mensaje que no revela valores

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #28 | VERIFICADO |
| Rama | `jdbc-base-postgres` | VERIFICADO |
| Commit de codigo | `4a446fd` | VERIFICADO |
| Pull Request | #27 (Closes #28) | VERIFICADO |
| Pruebas unitarias | 96 en verde | VERIFICADO |
| Pruebas de integracion en PostgreSQL 16 | 3 en verde | VERIFICADO |
| Comentario de revision antes del merge | revisor asignado: Suriel (sin comentario en GitHub) | NO_VERIFICADO |

## Limitaciones declaradas
- No hay pool de conexiones: cada operacion abre y cierra su conexion (ver ADR-006)
- `docker compose` y las pruebas *IT requieren Docker; sin Docker solo corren las unitarias
