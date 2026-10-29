# Contribucion - Integrante 1 (C02), parte de ventas con Hibernate

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** `VentaRepositoryHibernate` transaccional con bloqueo pesimista, pruebas de integracion y de concurrencia, seleccion de adaptador en `Main`, demostracion antes/despues y scripts de reproduccion de C02

## Nota de autoria
Esta parte estaba asignada al integrante 3 (Jesus Armando Andres Tablilla). Como no se entrego a tiempo, la implemento el integrante 1 para cumplir el corte. El trabajo, los commits y el PR son de Jared; este documento no atribuye nada de esto a Jesus.

## Issue
- **#38** - VentaRepositoryHibernate transaccional, concurrencia y demostracion antes/despues
- Criterio de cierre: `VentaRepositoryHibernate` pasa los mismos casos que JDBC, `ConcurrenciaVentasIT` demuestra que una segunda venta sobre stock agotado se rechaza, `Main` elige adaptador con `TCSW_PERSISTENCIA`, `scripts/verificar-c02.sh` reproduce todo desde cero; `mvn clean verify -Pintegracion` en verde y PR integrado a main

## Rama y commits
- Rama: `hibernate-ventas-transaccion`
- Commit de codigo: `12b39d8` - feat: VentaRepositoryHibernate transaccional, concurrencia y demo (#38)
- Commit de documentacion: en el historial del PR #39

## Decision tecnica
- Bloqueo pesimista + descuento relativo en una sola transaccion (ADR-008); orden fijo de bloqueo contra interbloqueos
- JDBC no se modifica; su limitacion se documenta con una prueba (`jdbcConLecturaVieja_vendeMasDeLoQueHay_deudaConocida`)
- La demo corre en dos JVM con un reinicio de PostgreSQL entre ellas: prueba persistencia real, no memoria

## Codigo creado o modificado
- `adapter/out/hibernate/VentaRepositoryHibernate.java`, `RepositoriosHibernateFactory.java` (agrega `ventas()`), `adapter/in/swing/Main.java` (selecciona adaptador)
- `VentaRepositoryHibernateIT`, `ConcurrenciaVentasIT`, `VentasFacadeHibernateIT`, `demo/DemoPersistencia`
- `scripts/demo-antes-despues.sh`, `scripts/verificar-c02.sh`, `docs/adr/ADR-008-transaccion-venta-bloqueo-pesimista.md`, `docs/evidencia/demo-antes-despues-*.txt`

## Pull Request y revision
- PR #39 - "feat: VentaRepositoryHibernate transaccional, concurrencia y demo (#38)" - Closes #38
- Revisor asignado: ninguno (el PR no tuvo revisor; lo integro su propio autor)
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- Unitarias (`mvn clean test`): 117 pruebas
- Integracion (`mvn clean verify -Pintegracion`, PostgreSQL 16 en Docker): 50 pruebas

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #38 | VERIFICADO |
| Rama | `hibernate-ventas-transaccion` | VERIFICADO |
| Commit de codigo | `12b39d8` | VERIFICADO |
| Pull Request | #39 (Closes #38) | VERIFICADO |
| Pruebas unitarias | 117 | VERIFICADO |
| Pruebas de integracion en PostgreSQL 16 | 50 | VERIFICADO |
| Demostracion antes/despues (2 JVM + reinicio de PostgreSQL) | `docs/evidencia/demo-antes-despues-hibernate.txt` | VERIFICADO |
| Comentario de revision antes del merge | sin revisor; PR integrado por su autor | NO_VERIFICADO |

## Limitaciones declaradas
- La prueba de dos hilos depende del planificador del sistema; el resultado esperado (una venta pasa) es determinista por el bloqueo, pero el orden de cual gana no
- `verificar-c02.sh` hace `down -v`: borra los datos locales de PostgreSQL
- La demo usa la misma base para JDBC y Hibernate, por eso las existencias se descuentan de forma acumulada entre las dos corridas
- Revision: en el PR #39 no hubo revisor ni comentario; el merge lo hizo el autor. En el PR #35 el revisor asignado integro sin dejar comentario de revision
