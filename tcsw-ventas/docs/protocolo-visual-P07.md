# Protocolo visual - P07 Cliente Swing

Ejecutor: Jesus Armando Andres Tablilla | Fecha: 2026-10-10 | Rama: `25-carrito-registro-de-venta-con-swingworker-y-ventana-principal` | Java 21
Las capturas viven en `docs/capturas/`. No contienen datos personales; el terminal de fondo muestra solo salida de Maven.

## Preparacion

    cd tcsw-ventas
    mvn clean compile
    java -cp target/classes adapter.in.swing.Main

Datos iniciales: P1 Playera Deportiva (150.0, existencia 20), P2 Pantalon Mezclilla (350.0, 15), P3 Gorra Urbana (99.0, 30).

## Recorrido

| # | Paso | Resultado esperado | Resultado observado | Estado | Captura |
|---|---|---|---|---|---|
| 1 | Abrir la aplicacion | Ventana con catalogo de 3 productos y carrito vacio | Ventana "tcsw-ventas - Cliente Swing" con 3 productos y carrito vacio | VERIFICADO | `01-ventana-inicial.png` |
| 2 | Seleccionar P1, cantidad 2, "Agregar al carrito" | El carrito muestra P1 x 2 | Fila `P1 / 2` en el carrito | VERIFICADO | `02-agregar-al-carrito.png` |
| 3 | "Registrar venta" con folio vacio | Aviso para capturar folio | Aviso "Captura un folio antes de registrar la venta." (caso de error) | VERIFICADO | `03-registrar-sin-folio.png` |
| 4 | "Registrar venta" con folio F-001 | Mensaje con folio y total | "Venta registrada con exito. Folio: F-001. Total: $300,00"; boton deshabilitado mientras corre el `SwingWorker` | VERIFICADO | `04-registrar-con-folio.png` |
| 5 | Tras la venta, agregar P1 con cantidad 19 | Rechazo por existencia; catalogo con existencia 18 y carrito vacio | Error "Existencia insuficiente para P1: disponible 18, en carrito 0"; el catalogo muestra 18 (caso de error y refresco) | VERIFICADO | `05-existencia-insuficiente.png` |
| 6 | "Quitar seleccion" sobre una fila del carrito | La fila desaparece | No se capturo; cubierto por `VentasControllerTest.quitarDelCarritoExito` | NO_VERIFICADO (visual) | - |
| 7 | "Registrar venta" con carrito vacio y folio | Error "El carrito esta vacio" | No se capturo; cubierto por `VentasControllerRegistrarVentaTest` | NO_VERIFICADO (visual) | - |

## Verificacion despues de la correccion de Sonar
La correccion (`transient` en campos y `WindowConstants`) no cambia el comportamiento, pero se hizo despues de las capturas. Repetir en `main` final:

| # | Paso | Estado | Fecha | Captura |
|---|---|---|---|---|
| A | Abrir la ventana desde `main` y agregar P1 x 2 | PENDIENTE | | |
| B | Registrar con folio y comprobar el mensaje de venta | PENDIENTE | | |

## Relacion con el saber teorico
- El registro usa `SwingWorker` para no bloquear el hilo de despacho de eventos (EDT): `doInBackground` corre fuera del EDT y `done` muestra el resultado de vuelta en el EDT.
- La vista no contiene reglas de negocio: valida el controlador y el caso de uso (ver ADR-005).
- El carrito vive en el controlador y no es seguro ante hilos; es una limitacion declarada.
