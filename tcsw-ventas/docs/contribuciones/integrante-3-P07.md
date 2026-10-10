# Contribucion - Integrante 3 (P07)

**Nombre:** Jesus Armando Andres Tablilla
**GitHub:** JesusAndres765
**Alcance:** carrito, registro de venta con SwingWorker y ensamblado de la ventana principal

## Issue
- **#** - Carrito, registro de venta con SwingWorker y ventana principal
- Criterio de cierre: existen `CarritoPanel`, `RegistrarVentaWorker` y `MainFrame` integrando todo el sistema en una ventana interactiva.

## Rama y commits
- Rama: `3-swing-carrito-registro`
- Commit: `b3037bb` - feat: carrito, registro con SwingWorker y ventana principal (#)

## Decision de arquitectura
- `CarritoPanel` y `CarritoTableModel` siguen el mismo esquema modelo-vista que el catalogo: la vista no tiene reglas de negocio y delega en `VentasController`.
- `RegistrarVentaWorker` (SwingWorker) ejecuta el registro en `doInBackground` (fuera del EDT) y muestra el resultado en `done` (de vuelta en el EDT). Los errores del dominio se muestran tal cual y el carrito se conserva para corregirlo.
- El boton "Registrar venta" se deshabilita mientras corre el worker para evitar un doble registro.
- `MainFrame` ensambla catalogo y carrito. Conecta `CatalogoPanel -> CarritoPanel` (al agregar) y `CarritoPanel -> CatalogoPanel` (al vender, para refrescar las existencias) con callbacks, sin que los paneles se conozcan entre si.
- `Main` es la raiz de composicion: crea los repositorios en memoria, la fachada y el controlador, y abre la ventana con `SwingUtilities.invokeLater`.
- Se usa Swing, no JavaFX.

## Correcciones respecto al borrador inicial
- El total de la venta se mostraba con `String.format("%.2f", venta.calcularTotal())`, pero `calcularTotal()` devuelve `Dinero`, no un numero; habria lanzado `IllegalFormatConversionException` en `done()`. Ahora se usa `calcularTotal().valor()`.
- El catalogo no se refrescaba tras una venta; se agrego el callback `setAlRegistrarVenta`.
- `Main` usa los constructores reales de la fachada con repositorios en memoria.

## Codigo creado o modificado
- `adapter/in/swing/CarritoTableModel.java`
- `adapter/in/swing/CarritoPanel.java`
- `adapter/in/swing/RegistrarVentaWorker.java`
- `adapter/in/swing/MainFrame.java`
- `adapter/in/swing/Main.java` (modificado: ahora abre la ventana)

## Pruebas
- `CarritoTableModelTest` (3): filas, columnas y valores; actualizar con notificacion; lista nula.
- `VentasControllerRegistrarVentaTest` (4): venta valida; la venta descuenta la existencia; carrito vacio; folio nulo o vacio conservando el carrito.
- `mvn clean test` en la rama -> **85 pruebas, 0 fallos, BUILD SUCCESS** (esperado: 85).

## Estado de verificacion
- VERIFICADO: compilacion y pruebas de los modelos y del controlador con `mvn clean test`.
- PENDIENTE: recorrido visual completo en ventana (agregar, quitar, registrar, errores) y captura de pantallas.
- Limitacion conocida: el carrito del controlador no es seguro ante hilos; el registro corre en segundo plano y el boton se deshabilita, pero no se protege contra otras modificaciones simultaneas.
- NO_VERIFICADO: Sonar para P07.

## Pull Request y revision
- PR: # - Closes #
- Revision: <REVISOR> (<comentario o aprobacion>)

## Identificador final
- ID (commit de mi parte): `b3037bb`
