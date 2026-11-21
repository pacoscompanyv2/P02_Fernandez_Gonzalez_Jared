# Matriz por frontera (C04, recorrido vertical "registrar venta")

Recorrido: cliente Angular -> HTTP/OpenAPI -> adaptador REST -> fachada/caso de uso -> repositorio (Hibernate) -> PostgreSQL, y regreso.
Estado: VERIFICADO solo lo que tenga la salida correspondiente en `docs/evidencia/`; el resto PENDIENTE.

| Frontera | Entrada | Salida esperada | Como se comprueba | Evidencia | Estado |
|---|---|---|---|---|---|
| Formulario Angular | folio y partidas del usuario | solicitud valida o mensaje de validacion | pruebas del cliente (51) | `verificar-c03-salida.txt` | VERIFICADO en C03 |
| HTTP (via nginx) | `POST /api/v1/ventas` + JSON | 201 + JSON de la venta | `verify-stack.sh` seccion 3 y 5 | `verify-stack-salida.txt` | PENDIENTE |
| Adaptador REST (DTO) | cuerpo invalido | 400 `VALIDACION` | `verify-stack.sh` seccion 4; pruebas de la API | idem / `verificar-c03-salida.txt` | PENDIENTE / VERIFICADO en C03 |
| Caso de uso | folio repetido; producto sin existencia | 409 `FOLIO_DUPLICADO` / `EXISTENCIA_INSUFICIENTE` | `verify-stack.sh` seccion 4; `verificar-p09.sh` | idem | PENDIENTE / VERIFICADO en C03 |
| Persistencia | venta + descuento de existencia | fila de venta y existencia menos 1 | `verify-stack.sh` (existencia antes/despues) y `verificar-p09.sh` fase B (reinicio) | idem | PENDIENTE / VERIFICADO en C03 |
| Cliente tras el error | API caida | `SIN_CONEXION` con reintento | captura de C03 (en el Word de C03) | Word C03 | VERIFICADO en C03 |

Diagnostico: ante un fallo se registra hecho observado, hipotesis, prueba que distingue causas, correccion y retest en `bitacora-c04.md`; no se cambia mas de una capa a la vez.
