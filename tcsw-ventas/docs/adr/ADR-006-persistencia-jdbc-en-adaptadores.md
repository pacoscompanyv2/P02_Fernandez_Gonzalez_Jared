# ADR-006: Persistencia con JDBC y PostgreSQL en adaptadores de salida

## Estado
Aceptado (P08)

## Contexto
Hasta P07 los datos vivian en memoria (`ProductoRepositoryEnMemoria`, `VentaRepositoryEnMemoria`): al cerrar
la aplicacion Swing se perdia todo. P08 pide persistir y consultar productos con JDBC y PostgreSQL 16 en Docker
Compose, con transacciones y pruebas de integracion, **conservando los puertos del nucleo**
(`ProductoRepository`, `VentaRepository`).

## Decision
1. **JDBC puro con `PreparedStatement`**, sin ORM. Todo el SQL esta parametrizado; ningun dato se concatena en una consulta.
2. **Los adaptadores implementan los puertos que ya existian** (`adapter/out/jdbc/ProductoRepositoryJdbc`, `VentaRepositoryJdbc`).
   `domain` y `application` no cambian. La regla ArchUnit `ArquitecturaPersistenciaTest` impide que usen `java.sql`
   y limita `java.sql` al paquete `adapter.out.jdbc`.
3. **La transaccion vive en el adaptador.** `VentaRepositoryJdbc.guardar(Venta)` abre una conexion con
   `setAutoCommit(false)` y guarda en una sola transaccion: la venta, sus partidas y las nuevas existencias.
   Si algo falla se hace `rollback` y no queda nada a medias. El caso de uso no sabe de transacciones.
4. **Integridad doble:** el dominio valida (`Producto`, `Venta`) y la base tambien (`CHECK`, `PRIMARY KEY`,
   `FOREIGN KEY` en `db/schema.sql`). Si algo se salta el dominio, la base lo rechaza.
5. **Configuracion por entorno** (`TCSW_DB_URL`, `TCSW_DB_USER`, `TCSW_DB_PASSWORD`): ninguna credencial en el codigo ni
   en el repositorio; `.env` esta en `.gitignore` y se versiona solo `.env.example`.
6. **Pruebas de integracion separadas:** clases `*IT` en el perfil `mvn verify -Pintegracion`, contra una base aparte
   `*_test` (el `TRUNCATE` nunca toca los datos de la aplicacion). `mvn clean test` sigue sin necesitar Docker.
7. **`Main` elige el almacenamiento** en un solo punto (`RepositoriosFactory`): con variables `TCSW_DB_*` usa PostgreSQL;
   sin ellas, memoria.

## Alternativas consideradas
- **JPA/Hibernate:** descartado; oculta el SQL (que es justo lo que P08 pide mostrar) y agrega dependencias y configuracion
  que no aportan al objetivo del modulo.
- **Transaccion en `RegistrarVentaService` (puerto de unidad de trabajo):** mas general, pero obligaria a cambiar los puertos
  del nucleo, y P08 pide conservarlos. Se acepto el costo de que la atomicidad cubra "venta + partidas + existencias" y no los observers.
- **Una sola base para app y pruebas:** descartado; las pruebas truncan tablas y borrarian los datos de la demostracion.

## Consecuencias
- El nucleo queda intacto y la persistencia es intercambiable (memoria o PostgreSQL).
- **Limitacion 1 (concurrencia):** el dominio descuenta stock en memoria y el adaptador escribe la existencia resultante
  (`UPDATE ... SET existencia = ?`). Con dos clientes vendiendo el mismo producto a la vez podria perderse una actualizacion.
  Mitigacion futura: `UPDATE ... SET existencia = existencia - ?` con el `CHECK` como red de seguridad, o `SELECT ... FOR UPDATE`.
- **Limitacion 2 (lectura de ventas):** `Venta` no tiene constructor "desde datos" y `agregarPartida` descuenta stock; `buscarPorFolio`
  reconstruye cada partida con `existencia actual + cantidad` para no descontar dos veces, y usa el descuento por defecto (mayoreo),
  no el que se uso al vender. Mitigacion futura: guardar la estrategia en la tabla `venta` y agregar una fabrica de reconstruccion en `domain`.
- **Limitacion 3:** la clave de conexion se toma del entorno; no hay pool de conexiones (cada operacion abre y cierra la suya). Suficiente
  para el alcance de P08; un pool (HikariCP) seria el siguiente paso.
- Evidencia: `adapter/out/jdbc/*`, `db/schema.sql`, `docker-compose.yml`, `ArquitecturaPersistenciaTest`,
  `ProductoRepositoryJdbcIT`, `VentaRepositoryJdbcIT`, `VentaRepositoryJdbcTransaccionTest`, `VentasFacadeJdbcIT`.
