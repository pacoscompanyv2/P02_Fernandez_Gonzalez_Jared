# Contribucion - Integrante 2 (P08)

**Nombre:** Suriel Francisco Tecpile
**GitHub:** SurielFrancisco
**Alcance:** pruebas de integracion de `ProductoRepositoryJdbc` contra PostgreSQL 16, caso de error y evidencia reproducible (scripts de verificacion y salida de PostgreSQL sanitizada)

## Issue
- **#29** - Pruebas de integracion de productos contra PostgreSQL y evidencia sanitizada
- Criterio de cierre: `ProductoRepositoryJdbcIT` con casos positivos y negativos (existencia negativa, codigo largo, SQL inyectado), `scripts/verificar-p08.sh` y `scripts/evidencia-postgres.sh`; `mvn clean verify -Pintegracion` en verde y PR integrado a main

## Rama y commits
- Rama: `jdbc-pruebas-integracion`
- Commit de codigo: `c87cca7` - test: integracion de ProductoRepositoryJdbc y scripts de evidencia (#29)
- Commit de documentacion: ver historial del PR #30

## Decision tecnica
- Las pruebas de integracion corren contra una **base PostgreSQL real** (no un doble ni una base en memoria), en una base `*_test` aparte; asi se prueba lo que de verdad ejecuta el motor: `ON CONFLICT`, tipos `NUMERIC`, `CHECK`
- Cada prueba parte de tablas vacias (`TRUNCATE` en `PostgresTestBase`), por lo que son independientes y repetibles
- Positivas: guardar y recuperar, upsert sin duplicar, listado ordenado, persistencia con un repositorio nuevo
- Negativas / limite: codigo inexistente, `guardar(null)`, codigo mas largo que la columna (22001), existencia negativa que se salta el dominio (23514)
- **PreparedStatement:** un nombre con `x'); DROP TABLE producto;--` se guarda como texto literal y la tabla sigue existiendo; un codigo de busqueda `' OR '1'='1` no regresa filas
- La salida de PostgreSQL se genera con un script que enmascara la clave y las rutas privadas

## Codigo creado o modificado
- `src/test/java/adapter/out/jdbc/ProductoRepositoryJdbcIT.java` (11 pruebas)
- `scripts/verificar-p08.sh` (levanta PostgreSQL, corre `mvn clean verify -Pintegracion`, genera la evidencia)
- `scripts/evidencia-postgres.sh` (genera `docs/evidencia/postgres-salida-sanitizada.txt`)
- `docs/evidencia/postgres-salida-sanitizada.txt`

## Pull Request y revision
- PR #30 - "test: integracion de ProductoRepositoryJdbc y scripts de evidencia (#29)" - Closes #29
- Revisor asignado: Jesus Armando Andres Tablilla
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- Unitarias (`mvn clean test`): 96 pruebas, 0 fallos, 0 errores
- Integracion (`mvn clean verify -Pintegracion`, PostgreSQL 16 en Docker): 14 pruebas (incluye las 11 de `ProductoRepositoryJdbcIT`), 0 fallos, 0 errores

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #29 | VERIFICADO |
| Rama | `jdbc-pruebas-integracion` | VERIFICADO |
| Commit de codigo | `c87cca7` | VERIFICADO |
| Pull Request | #30 (Closes #29) | VERIFICADO |
| Pruebas unitarias | 96 en verde | VERIFICADO |
| Pruebas de integracion en PostgreSQL 16 | 14 en verde | VERIFICADO |
| Salida PostgreSQL sanitizada | `docs/evidencia/postgres-salida-sanitizada.txt` | VERIFICADO |
| Comentario de revision antes del merge | revisor: Jesus | NO_VERIFICADO |

## Limitaciones declaradas
- Las pruebas *IT necesitan Docker y las variables de `.env`; sin ellas fallan con un mensaje claro (no se saltan en silencio)
- `docs/evidencia/postgres-salida-sanitizada.txt` muestra la estructura y los rechazos de integridad; los datos de la aplicacion se agregan en la corrida final del equipo
