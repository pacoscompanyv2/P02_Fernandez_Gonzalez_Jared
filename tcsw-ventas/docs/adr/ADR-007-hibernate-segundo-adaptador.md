# ADR-007: Hibernate como segundo adaptador de persistencia (JDBC se conserva)

**Estado:** aceptada (C02)  **Equipo:** TCSW-E01

## Contexto
En P08 los puertos `ProductoRepository` y `VentaRepository` se implementaron con JDBC puro. C02 pide Hibernate sobre PostgreSQL.

## Decision
Se agrega `adapter.out.hibernate` como **segundo adaptador de los mismos puertos**; `adapter.out.jdbc` no se toca ni se borra.
- Entidades JPA propias del adaptador (`ProductoEntity`, `VentaEntity`, `DetalleVentaEntity`, `DetalleVentaId`); el dominio no lleva anotaciones.
- `hibernate.hbm2ddl.auto=validate`: `db/schema.sql` sigue siendo la unica fuente del esquema; Hibernate solo lo comprueba al arrancar.
- Una sesion y una transaccion por operacion (`EjecutorTransaccional`): commit al terminar, rollback ante cualquier error.
- Seleccion en la raiz de composicion con `TCSW_PERSISTENCIA=hibernate`; sin ella la aplicacion se comporta como en P08 (JDBC con `TCSW_DB_*`, o memoria).
- Excepcion propia `RepositorioHibernateException` (con `getSqlState()`): el adaptador no expone tipos de Hibernate.
- Reglas ArchUnit (`ArquitecturaHibernateTest`): domain/application y los demas adaptadores no dependen de `org.hibernate` ni `jakarta.persistence`; `java.sql` sigue permitido solo en `adapter.out.jdbc`.

## Consecuencias
- (+) Se puede comparar el mismo contrato con dos tecnologias; el nucleo no cambia.
- (+) Menos SQL escrito a mano; la transaccion de venta (ver ADR-008) usa bloqueo pesimista.
- (-) Dos adaptadores que mantener y una dependencia mas (hibernate-core 6.4.4.Final).
- (-) Las columnas no mapeadas (`venta.creada_en`) las llena PostgreSQL con su DEFAULT.
