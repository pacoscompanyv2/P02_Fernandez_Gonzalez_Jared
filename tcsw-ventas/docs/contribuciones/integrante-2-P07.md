# Contribucion - Integrante 2 (P07)

**Nombre:** Suriel Francisco Tecpile
**GitHub:** SurielFrancisco
**Alcance:** vista de catalogo del cliente Swing (tabla de productos y alta al carrito con validacion de cantidad)

## Issue
- **#23** - Vista de catalogo en Swing
- Criterio de cierre: existen `CatalogoPanel` y `ProductoTableModel` funcionales que listan productos y permiten agregar al carrito con validacion de cantidad.

## Rama y commits
- Rama: `2-swing-catalogo`
- Commit: `0ee7979` - feat: vista de catalogo en Swing (#23)

## Decision de arquitectura
- `ProductoTableModel` es el modelo de la tabla (patron modelo-vista de Swing): solo adapta una lista de `Producto` a filas y columnas, sin logica de negocio.
- `CatalogoPanel` es la vista: no valida reglas de negocio ni conoce repositorios. Pide los productos y agrega al carrito a traves de `VentasController`.
- Las validaciones (cantidad mayor a cero, producto existente, existencia suficiente) viven en el controlador. La vista solo atrapa `IllegalArgumentException` y muestra el mensaje con `JOptionPane`, sin ocultar el error.
- El panel recibe un `Runnable` (`onProductoAgregado`) para avisar al carrito sin acoplarse a `CarritoPanel`.
- `CatalogoPanel.actualizar()` vuelve a leer el catalogo, para ver la existencia nueva despues de una venta.

## Codigo creado
- `adapter/in/swing/ProductoTableModel.java`
- `adapter/in/swing/CatalogoPanel.java` (seleccion unica, tecla de acceso Alt+A, etiqueta asociada al spinner)

## Pruebas
- `ProductoTableModelTest` (4): filas, columnas y valores; actualizar con notificacion; lista nula; fila fuera de rango.
- `VentasControllerAgregarCarritoTest` (5): producto valido; cantidad cero o negativa; producto inexistente; codigo nulo o vacio; mismo producto dos veces dentro de la existencia.
- Casos positivos y negativos en ambas clases.
- `mvn clean test` en la rama -> **78 pruebas, 0 fallos, BUILD SUCCESS** (esperado: 78).

## Estado de verificacion
- VERIFICADO: compilacion y pruebas de los modelos y del controlador con `mvn clean test`.
- PENDIENTE: recorrido visual del catalogo en ventana (se documenta en el protocolo visual de equipo).
- NO_VERIFICADO: Sonar para P07.

## Pull Request y revision
- PR: #24 - Closes #23
- Revision: Jesús 

## Identificador final
- ID (commit de mi parte): `0ee797915e5711106fc1de46979bb502cc1b9fe3`
