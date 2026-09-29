# ADR-001: Strategy y Factory para el descuento de Venta

## Estado
Aceptado

## Contexto
`Venta.calcularTotal()` traia el descuento harcodeado (`if partidas.size() >= 3`).
Cualquier regla de descuento nueva (temporada, cliente frecuente, etc.) obligaria
a modificar esa clase y sus pruebas existentes, violando abierto/cerrado.

## Decision
Se extrajo la regla a una interfaz `DescuentoStrategy` con implementaciones
intercambiables (`DescuentoPorMayoreo`, `SinDescuento`), y una `DescuentoStrategyFactory`
que decide cual usar segun un criterio. `Venta` recibe la estrategia por constructor,
con `DescuentoPorMayoreo` como valor por defecto para no romper el comportamiento previo.

## Alternativas consideradas
Un enum `TipoDescuento` con un `switch` dentro de `calcularTotal()`. Se descarto porque
cada regla nueva obligaria a editar `Venta` de nuevo; con Strategy se agrega una clase
sin tocar `Venta` ni sus pruebas.

## Consecuencias
- Se puede agregar una regla de descuento nueva sin tocar `Venta`.
- Se agrega una capa de indireccion (una interfaz mas) para un caso que hoy solo tiene
  2 variantes; el beneficio se nota mas cuando aparezca una tercera regla real.
- Evidencia: `domain/descuento/*`, pruebas en `DescuentoStrategyTest` (6 casos,
  incluye comparacion directa del intercambio de estrategia sobre la misma Venta).
