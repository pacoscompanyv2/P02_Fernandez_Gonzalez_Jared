# Protocolo visual - P08 Persistencia

Ejecutor: Jesus Armando Andres Tablilla | Fecha: 2026-10-24 | Rama: `3-jdbc-ventas-transaccion` | Java 21 | PostgreSQL 16 en Docker Compose
Las capturas estan en el documento final de entrega (`P08_EQUIPO_01`). Los datos son sinteticos (P1, P2, P3 y el folio F-001).

## Preparacion

    cd tcsw-ventas
    cp .env.example .env                  # y poner una clave local propia
    docker compose up -d --wait
    set -a; . ./.env; set +a
    mvn -q -DskipTests compile dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/libs
    java -cp "target/classes:target/libs/*" adapter.in.swing.Main

La consola imprime `Almacenamiento: PostgreSQL`. Datos iniciales (solo si la base esta vacia): P1 Playera Deportiva (150.0, existencia 20), P2 Pantalon Mezclilla (350.0, 15), P3 Gorra Urbana (99.0, 30).

## Recorrido

| # | Paso | Resultado esperado | Resultado observado | Estado | Captura |
|---|---|---|---|---|---|
| 1 | Seleccionar P1, cantidad 2, "Agregar al carrito", folio F-001 | El carrito muestra P1 x 2; el catalogo muestra P1 con existencia 20 | Catalogo P1 = 20; fila `P1 / 2` en el carrito; folio `F-001` | VERIFICADO | "Registra venta con folio F-001" |
| 2 | "Registrar venta" | Mensaje de exito con folio y total | No se capturo el mensaje; el efecto se comprueba en los pasos 3 y 4 | NO_VERIFICADO (visual) | - |
| 3 | Cerrar la aplicacion y abrirla de nuevo | El catalogo conserva la existencia: P1 baja de 20 a 18 y el carrito esta vacio | Catalogo P1 = 18, P2 = 15, P3 = 30, carrito vacio (segun el pie de captura del ejecutor) | VERIFICADO | "Existencias actualizadas y persistentes" |
| 4 | Consultar PostgreSQL con `psql` (`producto`, `venta`, `detalle_venta`) | Existencias 18 / 15 / 30; una venta F-001 con total 300.00; una partida P1 x 2 a 150.00 | `producto`: P1 18, P2 15, P3 30; `venta`: F-001, 300.00, `cerrada = f`; `detalle_venta`: F-001, linea 1, P1, cantidad 2, 150.00 | VERIFICADO | "Persistencia desde terminal" |
| 5 | Registrar otra venta con el mismo folio F-001 (P1 x 3) | Rechazo por clave duplicada; la transaccion se revierte y la existencia no cambia | Dialogo "No se pudo guardar la venta F-001 (transaccion revertida)"; el catalogo sigue en P1 = 18 (caso de error) | VERIFICADO | "Error al querer registrar otra venta con el mismo folio" |
| 6 | Registrar con un producto sin existencia suficiente | Rechazo; la base no cambia | No se capturo; cubierto por `VentasFacadeJdbcIT` | NO_VERIFICADO (visual) | - |
| 7 | Reproducir los pasos 1 a 5 en la maquina de Jared | Mismos resultados | No se ejecuto | NO_VERIFICADO | - |

## Verificacion posterior al cierre de P08

| # | Paso | Estado | Fecha | Captura |
|---|---|---|---|---|
| A | Abrir la aplicacion desde `main` final (`ac92607`) en otra maquina y repetir el paso 1 y el 3 | PENDIENTE | | |
| B | Regenerar `docs/evidencia/postgres-salida-sanitizada.txt` despues de una venta | PENDIENTE | | |

## Observaciones
- La columna `cerrada` de la venta queda en `f`: el caso de uso heredado no invoca `Venta.cerrar()`. Es coherente con el dominio y esta declarada como limitacion en el indice de evidencias.
- La consola de la aplicacion no se capturo en el recorrido de Jesus; el modo de almacenamiento se comprueba por las filas que aparecen en PostgreSQL.

## Relacion con el saber teorico
- La venta es una unidad de trabajo atomica: venta, partidas y existencias se guardan en una sola transaccion (`setAutoCommit(false)`, `commit`, `rollback`). El paso 5 muestra el rollback: no queda una venta a medias ni se descuenta dos veces la existencia.
- La interfaz no cambio: el nucleo hexagonal conserva sus puertos y solo cambio el adaptador de salida (memoria o PostgreSQL), elegido en `RepositoriosFactory`.
- El SQL esta parametrizado con `PreparedStatement`; un nombre con SQL inyectado se guarda como texto literal (`ProductoRepositoryJdbcIT`).
