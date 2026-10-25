# Contribucion - Integrante 3 (P08)

**Nombre:** Jesus Armando Andres Tablilla
**GitHub:** JesusAndres765
**Alcance:** `VentaRepositoryJdbc` con transaccion, conexion de `Main` a PostgreSQL y pruebas de transaccion, integracion y recorrido completo por la fachada

## Issue
- **#31** - VentaRepositoryJdbc con transacciones y conexion de Main a PostgreSQL
- Criterio de cierre: `VentaRepositoryJdbc` guarda venta, partidas y existencias en una sola transaccion (rollback comprobado); `RepositoriosFactory` conecta `Main` a PostgreSQL con respaldo en memoria; pruebas `VentaRepositoryJdbcTransaccionTest`, `VentaRepositoryJdbcIT` y `VentasFacadeJdbcIT` en verde; PR integrado a main

## Rama y commits
- Rama: `jdbc-ventas-transaccion`
- Commit de codigo: `7fd9922` - feat: VentaRepositoryJdbc con transaccion y Main con PostgreSQL (#31)
- Commit de documentacion: ver historial del PR #32

## Decision tecnica
- `guardar(Venta)` es **una transaccion**: `setAutoCommit(false)` -> insertar venta -> actualizar existencias -> insertar partidas -> `commit`; ante cualquier error `rollback` (y la conexion se cierra siempre con try-with-resources)
- El dominio ya descuenta stock en memoria; el adaptador refleja la existencia resultante en la misma transaccion. Si un producto no existe en la base (`UPDATE` afecta 0 filas) se aborta **toda** la venta
- `buscarPorFolio` lee venta + partidas + producto en **una sola consulta** y reconstruye la `Venta` sin tocar el dominio: cada partida se arma con `existencia actual + cantidad` para que `agregarPartida` no descuente dos veces. Limitacion: usa el descuento por defecto (ver ADR-006)
- Control de transaccion probado **sin base de datos** con una conexion falsa (`java.lang.reflect.Proxy`): se comprueba que se llama `commit` o `rollback` segun el caso
- Raiz de composicion: `RepositoriosFactory` decide PostgreSQL o memoria segun las variables `TCSW_DB_*`; `Main` solo cambio en como obtiene los repositorios y en la siembra (el catalogo inicial solo se carga si esta vacio, para no reiniciar existencias en cada arranque); `VentasController` y `MainFrame` no se tocaron

## Codigo creado o modificado
- `adapter/out/jdbc/VentaRepositoryJdbc.java`
- `adapter/out/jdbc/RepositoriosFactory.java`
- `adapter/in/swing/Main.java` (usa `RepositoriosFactory`, imprime el almacenamiento y siembra el catalogo solo si esta vacio)
- Pruebas: `VentaRepositoryJdbcTransaccionTest` (5, sin Docker), `VentaRepositoryJdbcIT` (9), `VentasFacadeJdbcIT` (4)

## Pull Request y revision
- PR #32 - "feat: VentaRepositoryJdbc con transaccion y Main con PostgreSQL (#31)" - Closes #31
- Revisor asignado: Jared Fernandez Gonzalez
- Comentario de revision explicito en GitHub antes del merge: [E_REV]

## Pruebas
- Unitarias (`mvn clean test`): 101 pruebas, 0 fallos, 0 errores
- Integracion (`mvn clean verify -Pintegracion`, PostgreSQL 16 en Docker): 27 pruebas, 0 fallos, 0 errores
- Casos de error cubiertos: folio duplicado (23505, sin doble descuento), producto inexistente (rollback total: ni venta, ni partidas, existencias intactas), existencia insuficiente y producto inexistente por la fachada (la base no cambia)
- Recorrido manual: aplicacion Swing con PostgreSQL; una venta sobrevive al cerrar y reabrir la aplicacion (ver capturas y `docs/evidencia/`)

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #31 | VERIFICADO |
| Rama | `jdbc-ventas-transaccion` | VERIFICADO |
| Commit de codigo | `7fd9922` | VERIFICADO |
| Pull Request | #32 (Closes #31) | VERIFICADO |
| Pruebas unitarias | 101 en verde | VERIFICADO |
| Pruebas de integracion en PostgreSQL 16 | 27 en verde | VERIFICADO |
| Comentario de revision antes del merge | revisor: Jared | [E_REV] |

## Limitaciones declaradas
- Concurrencia: la existencia se escribe como valor absoluto; dos clientes vendiendo a la vez podrian pisarse (ver ADR-006)
- `buscarPorFolio` reconstruye con el descuento por defecto, no con el usado al vender
