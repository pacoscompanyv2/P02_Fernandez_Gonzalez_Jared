# tcsw-ventas

Prototipo de ventas — Experiencia educativa Tecnologias para la Construccion de Software.
Actividad P01 / Modulo M01: entidad `Producto` con encapsulamiento, invariantes y pruebas.
Actividad P02 / Modulo M02: `Venta` y `DetalleVenta` como objetos colaborantes, mas el objeto
de valor `Dinero` para montos.

## Entorno requerido

- Java 11
- Maven 3.9.9 (o compatible)
- Git

Verificar versiones:

```
java -version
javac -version
mvn -version
```

## Clonar

```
git clone <URL_DEL_REPOSITORIO>
cd tcsw-ventas
```

## Compilar y probar

```
mvn clean test
```

Resultado esperado: `BUILD SUCCESS` y 23 pruebas ejecutadas sin fallos.

## Estructura

```
tcsw-ventas/
  pom.xml
  README.md
  src/main/java/domain/Producto.java
  src/main/java/domain/DetalleVenta.java
  src/main/java/domain/Venta.java
  src/main/java/domain/Dinero.java
  src/test/java/domain/ProductoTest.java
  src/test/java/domain/DetalleVentaTest.java
  src/test/java/domain/VentaTest.java
  src/test/java/domain/DineroTest.java
```

## Modelo Producto

Atributos: `codigo`, `nombre`, `precio`, `existencia`.

Invariantes protegidas por el constructor:
- codigo y nombre no vacios
- precio >= 0
- existencia >= 0

`descontar(cantidad)` rechaza cantidades <= 0 y cantidades mayores a la existencia
disponible, sin modificar el estado cuando la operacion se rechaza.

`aumentar(cantidad)` rechaza cantidades <= 0.

## Modelo Dinero

Objeto de valor inmutable para montos. Envuelve un `BigDecimal` con escala fija
de 2 decimales. Dos instancias con el mismo monto son iguales (`equals`/`hashCode`
por valor, no por identidad). Rechaza montos negativos.

## Modelo DetalleVenta

Compone un `Producto` con una cantidad. Invariantes protegidas por el constructor:
- producto no nulo
- cantidad > 0

`subtotal()` regresa un `Dinero` calculado como precio del producto por cantidad.

## Modelo Venta

Compone una lista de `DetalleVenta`. No es subtipo de nada: usa composicion,
no herencia, para reflejar que una venta contiene detalles.

- `agregarPartida(producto, cantidad)`: descuenta existencia del producto (delega
  en `Producto.descontar`, que ya valida existencia suficiente) y agrega el detalle.
  Rechaza agregar partidas si la venta ya esta cerrada.
- `calcularTotal()`: suma los subtotales de todas las partidas.
- `cerrar()`: marca la venta como cerrada; rechaza cerrar una venta sin partidas.

## Pruebas incluidas

Producto:
1. Creacion valida de un producto
2. Rechazo de precio negativo
3. Rechazo de existencia negativa
4. Rechazo de nombre vacio
5. Descuento de cantidad disponible
6. Rechazo de cantidad cero al descontar
7. Rechazo de descuento mayor a la existencia
8. La existencia se conserva despues de un rechazo

DetalleVenta:
1. Calculo de subtotal
2. Rechazo de producto nulo
3. Rechazo de cantidad cero o menor

Venta:
1. Agregar partida descuenta existencia del producto
2. Calculo de total con varias partidas
3. Rechazo de partida sin existencia suficiente
4. Rechazo de agregar partida a una venta cerrada
5. Rechazo de cerrar una venta sin partidas
6. Total es cero sin partidas
7. Rechazo de folio vacio
8. Una venta nueva no esta cerrada

Dinero:
1. Suma de dos montos
2. Multiplicacion por cantidad
3. Rechazo de monto negativo
4. Dos montos iguales son iguales

## Estado de verificacion

El codigo se compilo de forma aislada con `javac` (Java 21, con target 11) sin errores.
Las pruebas con `mvn clean test` deben correrse en tu maquina siguiendo el comando de
arriba antes de declarar MODULO_M02_VERIFICADO.
