# Contribucion - Integrante 1 (P07)

**Nombre:** Jared Fernandez Gonzalez
**GitHub:** pacoscompanyv2
**Alcance:** controlador base del cliente Swing, listado de productos y correcciones de integridad en el nucleo

## Issue
- **#21** - Base del controlador Swing y listado de productos
- Criterio de cierre: existe `VentasController` funcional (listar, agregar al carrito, quitar, registrar venta) con sus pruebas unitarias, y `VentasFacade` expone la consulta de productos.

## Rama y commits
- Rama: `1-swing-controlador-base`
- Commit: `62f1518` - feat: controlador base Swing y listado de productos (#21)
- Alcance del commit: 9 archivos cambiados (+442 / -22)

## Decision de arquitectura
- El controlador (`adapter.in.swing.VentasController`) no tiene reglas de negocio: valida entradas (codigo vacio, cantidad <= 0, folio vacio, carrito vacio, existencia) y delega en `VentasFacade`.
- Direccion de dependencias: vista Swing -> controlador -> fachada -> caso de uso y puertos. El dominio no conoce Swing (verificado por `ArquitecturaHexagonalTest`).
- El carrito vive en el controlador como estado de la interfaz; `obtenerCarrito()` devuelve una copia para no exponer la lista interna.
- Ver ADR-005 para la decision de que la fachada delegue en el caso de uso.

## Hallazgos corregidos durante la revision
1. **Fachada fuera del caso de uso:** `VentasFacade.registrarVenta` construia la `Venta` con descuento fijo y no notificaba observers. Ahora delega en `RegistrarVentaService`, que recibe la estrategia de descuento por constructor.
2. **Inventario a medias:** `Venta.agregarPartida` descuenta existencia por linea, asi que un carrito con un segundo producto sin existencia dejaba el primero ya descontado. Ahora el servicio valida productos y existencias (sumando lineas repetidas) antes de descontar, y el controlador rechaza al agregar lo que exceda la existencia.
- Limitacion conocida: el repositorio en memoria no es transaccional ni concurrente; la validacion protege el flujo normal, no accesos simultaneos.

## Codigo creado o modificado
- `application/port/out/ProductoRepository.java` (listarTodos)
- `adapter/out/memory/ProductoRepositoryEnMemoria.java`
- `application/service/RegistrarVentaService.java` (validacion previa, estrategia inyectable)
- `application/facade/VentasFacade.java` (delegacion al caso de uso, agregarObserver)
- `adapter/in/swing/VentasController.java`
- `adapter/in/swing/Main.java`

## Pruebas
- `VentasControllerTest` (7): listar, facade nulo, quitar del carrito, indice invalido, existencia insuficiente, acumulado del carrito, carrito conservado si el registro falla.
- `RegistrarVentaServiceTest` (9): se agregaron 5 (inventario atomico, lineas repetidas, cantidad no positiva, estrategia inyectada, estrategia nula).
- `VentasFacadeTest` (6): se agregaron 3 (observers por la fachada, no descontar si se rechaza, servicio nulo).
- Caso positivo y casos negativos incluidos en cada clase.
- `mvn clean test` en la rama -> **69 pruebas, 0 fallos, 0 errores, BUILD SUCCESS** (2026-10-06 00:24).

## Estado de verificacion
- VERIFICADO: compilacion y 69 pruebas en la rama `1-swing-controlador-base`; reglas de `ArquitecturaHexagonalTest`.
- NO_VERIFICADO: recorrido visual completo y comportamiento de `CatalogoPanel`, `CarritoPanel`, `RegistrarVentaWorker` y `MainFrame` (partes de los integrantes 2 y 3).
- NO_VERIFICADO: analisis de Sonar para P07.
- PENDIENTE: PR, revision y merge; evidencia visual conjunta; commit o tag final de equipo.

## Pull Request y revision
- PR: #22 - "feat: controlador base Swing y listado de productos (#21)" - Closes #21
- Revision: Suriel (<comentario o aprobacion; completar tras la revision>)

## Identificador final
- ID (commit de mi parte): `62f1518`
- Commit o tag final del equipo: `<TAG_O_COMMIT_FINAL>`
