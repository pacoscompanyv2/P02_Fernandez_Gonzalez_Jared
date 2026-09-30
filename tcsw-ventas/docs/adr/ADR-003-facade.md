# ADR-003: Facade para registrar ventas

## Estado
Aceptado

## Contexto
Para registrar una venta hay que armar a mano un RegistrarVentaService con sus
2 repositorios (ProductoRepositoryEnMemoria, VentaRepositoryEnMemoria). Cualquier
consumidor nuevo (una UI, una prueba de integracion) tendria que conocer ese cableado
interno para poder usar el nucleo.

## Decision
Se creo VentasFacade, que arma internamente el RegistrarVentaService con sus
adaptadores y expone solo 2 metodos simples: registrarProducto(Producto) y
registrarVenta(String, List<ItemVenta>).

## Alternativas consideradas
Que cada consumidor (por ejemplo una futura UI) arme directamente el
RegistrarVentaService con sus repositorios. Se descarto porque expondria el
cableado interno del nucleo a cualquiera que quisiera usarlo, y cualquier cambio
en como se arma el service obligaria a actualizar cada consumidor.

## Consecuencias
- Un consumidor nuevo del nucleo no necesita conocer los adaptadores concretos.
- El Facade fija la implementacion en memoria dentro de su propio constructor;
  si se necesitara otro adaptador (por ejemplo, una base de datos) habria que
  agregar otra forma de construir el Facade, o inyectar los repositorios en vez
  de crearlos internamente. Queda declarado como limite conocido, no resuelto aqui.
- Evidencia: application/facade/VentasFacade.java, pruebas en VentasFacadeTest
  (caso de exito y caso de error con producto inexistente).