# Contribucion - Integrante 1 (C02)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** Hibernate como segundo adaptador (sin quitar JDBC): dependencia, `SessionFactory` desde entorno, entidades JPA, `ProductoRepositoryHibernate`, regla ArchUnit y ADR-007

## Issue
- **#33** - Hibernate como segundo adaptador: entidades JPA y ProductoRepositoryHibernate
- Criterio de cierre: `ProductoRepositoryHibernate` cumple el contrato de `ProductoRepository` contra PostgreSQL 16 (`ProductoRepositoryHibernateIT`), `hbm2ddl=validate` arranca contra `db/schema.sql`, ArchUnit impide que el nucleo use Hibernate/JPA, `mvn clean verify -Pintegracion` en verde y PR integrado a main

## Rama y commits
- Rama: `hibernate-base-productos`
- Commit de codigo: `e158a6e` - feat: Hibernate como segundo adaptador y ProductoRepositoryHibernate (#33)
- Commit de documentacion: commit directo a main, segun indicacion del docente (la documentacion se agrego despues del merge del PR #35)

## Decision tecnica
- JDBC se conserva: ambos adaptadores implementan el mismo puerto (ver ADR-007)
- Entidades separadas del dominio; `validate` en vez de `update` para que `db/schema.sql` mande
- Una sesion + transaccion por operacion; rollback ante error; `RepositorioHibernateException` con SQLSTATE

## Codigo creado o modificado
- `src/main/java/adapter/out/hibernate/*` (9 clases)
- `src/test/java/adapter/out/hibernate/*` (base, IT de productos, prueba unitaria de la fabrica)
- `src/test/java/arch/ArquitecturaHibernateTest.java`
- `src/test/java/adapter/out/jdbc/PostgresTestBase.java` (ahora publica para reusarla)
- `pom.xml` (hibernate-core 6.4.4.Final), `docs/adr/ADR-007-hibernate-segundo-adaptador.md`

## Pull Request y revision
- PR #35 - "feat: Hibernate como segundo adaptador y ProductoRepositoryHibernate (#33)" - Closes #33
- Revisor asignado: Suriel Francisco Tecpile
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- Unitarias (`mvn clean test`): 106 pruebas
- Integracion (`mvn clean verify -Pintegracion`, PostgreSQL 16 en Docker): 36 pruebas

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #33 | VERIFICADO |
| Rama | `hibernate-base-productos` | VERIFICADO |
| Commit de codigo | `e158a6e` | VERIFICADO |
| Pull Request | #35 (Closes #33) | VERIFICADO |
| Pruebas unitarias | 106 | VERIFICADO |
| Pruebas de integracion en PostgreSQL 16 | 36 | VERIFICADO |
| Comentario de revision antes del merge | revisor asignado: Suriel (PR #35 sin comentario de revision) | NO_VERIFICADO |

## Limitaciones declaradas
- Hibernate usa `PostgreSQLDialect` fijo y sin pool de conexiones (DriverManager): suficiente para el taller, no para produccion
- `venta.creada_en` no esta mapeada
- El PR #34 se cerro por error; el mismo cambio se integro con el PR #35. El mensaje del commit de codigo `e158a6e` no incluye `(#33)`; el issue #33 se cierra con el commit de documentacion (`Closes #33`)
- Las cifras de pruebas (106 unitarias, 36 de integracion) son las de la corrida en la rama `hibernate-base-productos` antes del PR
