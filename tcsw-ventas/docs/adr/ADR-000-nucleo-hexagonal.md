# ADR-000: Separacion en dominio, aplicacion y adaptadores

## Estado
Aceptado

## Contexto
Hasta P04 el dominio (`Venta`, `Producto`, `Dinero`) no tenia forma de ser usado
sin acoplarse directamente a como se guardan los datos. Si mañana se cambia de
almacenamiento en memoria a una base de datos real, o se agrega una UI, no habia
un punto de entrada estable ni una forma de aislar esos cambios del dominio.

## Decision
Se aplico arquitectura hexagonal: el dominio se mantiene intacto y sin dependencias
externas; se agregaron puertos de entrada (`RegistrarVentaUseCase`) y de salida
(`ProductoRepository`, `VentaRepository`) como interfaces en `application.port`;
`RegistrarVentaService` implementa el caso de uso usando solo esas interfaces;
y `adapter.out.memory` contiene las implementaciones concretas en memoria.
Una prueba de arquitectura (ArchUnit, `ArquitecturaHexagonalTest`) verifica que
`domain` nunca dependa de `application` ni de `adapter`.

## Alternativas consideradas
Mantener el acceso a datos directo desde el dominio (por ejemplo, que `Venta`
supiera guardarse a si misma). Se descarto porque mezclaria reglas de negocio
con detalles de infraestructura, y no se podria probar el caso de uso sin
levantar la infraestructura real.

## Consecuencias
- El dominio se puede probar sin ningun adaptador.
- Cambiar el almacenamiento (por ejemplo a una base de datos) solo requeriria
  un adaptador nuevo, sin tocar dominio ni caso de uso.
- Se agrega una capa de indireccion adicional (interfaces) que un proyecto muy
  pequeño no siempre necesita; aqui se justifica porque el proyecto ya crecio
  a traves de varias entregas (P01-P06).
- Evidencia: `application/port/*`, `application/service/RegistrarVentaService.java`,
  `adapter/out/memory/*`, prueba `arch.ArquitecturaHexagonalTest` (2 casos).
