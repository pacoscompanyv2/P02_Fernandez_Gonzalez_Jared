# ADR-008: Transaccion de venta con bloqueo pesimista y descuento relativo (Hibernate)

**Estado:** aceptada (C02)  **Equipo:** TCSW-E01

## Contexto
`VentaRepositoryJdbc.guardar` (P08) es atomico, pero escribe la existencia como valor ABSOLUTO calculado en memoria a partir de una lectura anterior:
`UPDATE producto SET existencia = ?`. Si dos ventas leen P1 = 10 y piden 6 cada una, ambas escriben 4: se venden 12 piezas de 10 y se pierde una actualizacion (deuda D2). El dominio descuenta en memoria (`Venta.agregarPartida`), por lo que el adaptador no puede confiar en ese numero.

## Decision
`VentaRepositoryHibernate.guardar` ejecuta, en UNA transaccion:
1. Suma lo pedido por codigo de producto (orden fijo por codigo, `TreeMap`).
2. `find(..., PESSIMISTIC_WRITE)` de cada producto (`SELECT ... FOR UPDATE`): ninguna otra venta puede cambiarlo hasta el commit, y se lee la existencia ACTUAL de la base.
3. Si `existencia < pedido` -> `RepositorioHibernateException("Existencia insuficiente ...")`. Si no, descuenta de forma **relativa** (`existencia - pedido`).
4. Persiste la venta y sus partidas (cascade) con el precio unitario del momento.
5. Commit; ante cualquier error, rollback completo (venta, partidas y existencias).

El orden fijo de bloqueo evita interbloqueos entre ventas que comparten productos.

## Evidencia
`ConcurrenciaVentasIT`: (a) JDBC con lecturas viejas vende 12 de 10 (se documenta, deuda aceptada); (b) Hibernate con las mismas lecturas viejas rechaza la segunda; (c) dos hilos simultaneos: solo una venta pasa y la existencia final es 4. `VentaRepositoryHibernateIT` cubre folio duplicado (23505), producto inexistente y rollback.

## Consecuencias
- (+) Sin ventas por encima de la existencia real, tambien con dos instancias de la aplicacion.
- (+) El dominio no cambia.
- (-) Las ventas del mismo producto se serializan mientras dura la transaccion (cortas).
- (-) JDBC conserva la deuda D2; no se "arregla" para no alterar P08.
