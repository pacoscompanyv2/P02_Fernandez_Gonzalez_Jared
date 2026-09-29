# ADR-002: Observer para notificar el registro de una venta

## Estado
Aceptado

## Contexto
Al registrar una venta puede interesar que otras partes del sistema se enteren
(por ejemplo, registrar un log, o mas adelante enviar una notificacion), sin que
⁠ RegistrarVentaService ⁠ tenga que saber quien esta escuchando ni como.

## Decision
Se agrego la interfaz ⁠ VentaObserver ⁠ con el metodo ⁠ onVentaRegistrada(Venta) ⁠.
⁠ RegistrarVentaService ⁠ mantiene una lista de observadores y los notifica solo
despues de guardar la venta con exito. Se incluyo ⁠ NotificadorConsola ⁠ como
implementacion de ejemplo.

## Alternativas consideradas
Llamar directamente a un metodo de logging o notificacion dentro de ⁠ registrar(...) ⁠.
Se descarto porque acoplaria el caso de uso a una implementacion concreta; con
Observer se pueden agregar o quitar "escuchas" sin modificar ⁠ RegistrarVentaService ⁠.

## Consecuencias

- Se puede agregar un nuevo observador (por ejemplo, notificacion por correo) sin tocar el caso de uso.
- Si un registro falla, ningun observador es notificado (se probo explicitamente).
- Los observadores corren de forma sincrona dentro de `registrar(...)`; si algun observador fuera lento, retrasaria la respuesta. No es un problema con el observador actual (`NotificadorConsola`), pero queda declarado como limite.
- Evidencia: `application/observer/*`, pruebas en `RegistrarVentaServiceTest` (`notificaAlObserverCuandoRegistraConExito`, `noNotificaAlObserverSiElRegistroFalla`).

