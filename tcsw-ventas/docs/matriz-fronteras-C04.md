# Matriz por frontera (C04, recorrido vertical "registrar venta")

Recorrido: cliente Angular -> HTTP/OpenAPI -> adaptador REST -> fachada/caso de uso -> repositorio (Hibernate) -> PostgreSQL, y regreso.
Estado: VERIFICADO solo lo que tiene la salida correspondiente en `docs/evidencia/`. "C04" = stack en Docker (`verify-stack-salida.txt`, commit `426f16a`);
"C03/post-parche" = `verificar-c03.sh` (5 pasos, 0 fallas) repetido tras el tratamiento de Sonar (`c04-despues-del-parche-salida.txt`).

| Frontera | Entrada | Salida esperada | Como se comprueba | Evidencia | Estado |
|---|---|---|---|---|---|
| Formulario Angular | folio y partidas del usuario | solicitud valida o mensaje de validacion | pruebas del cliente (51, 0 fallas) | CI y `c04-despues-del-parche-salida.txt` | VERIFICADO |
| HTTP (via nginx) | `POST /api/v1/ventas` + JSON | 201 + JSON de la venta | `verify-stack.sh` (alta de venta y consulta por folio, tambien a traves de `/api` del frontend) | `verify-stack-salida.txt` | VERIFICADO |
| Adaptador REST (DTO) | cuerpo invalido; folio inexistente | 400 `VALIDACION`; 404 `VENTA_NO_ENCONTRADA` | `verify-stack.sh`; pruebas de la API | `verify-stack-salida.txt` | VERIFICADO |
| Caso de uso | folio repetido; producto sin existencia | 409 `FOLIO_DUPLICADO` / `EXISTENCIA_INSUFICIENTE` | `verify-stack.sh` (409 folio duplicado); `verificar-p09.sh`; pruebas de la API | `verify-stack-salida.txt`, `c04-despues-del-parche-salida.txt` | VERIFICADO |
| Persistencia | venta + descuento de existencia | fila de venta y existencia menos 1 | `verify-stack.sh` (existencia antes/despues); `verificar-p09.sh` fase B (reinicio); pruebas de integracion (56) | idem | VERIFICADO |
| Cliente tras el error | API caida | `SIN_CONEXION` con reintento | pruebas del cliente (`api-error.spec.ts`); captura de C03 | CI; Word de C03 | VERIFICADO en pruebas; captura de navegador solo de C03 |

Diagnostico: ante un fallo se registra hecho observado, hipotesis, prueba que distingue causas, correccion y retest en `bitacora-c04.md`; no se cambia mas de una capa a la vez.
Caso real de C04: ver en la bitacora el fallo de `verify-stack.sh` (7 de 11) y el de `npm ci` dentro de la imagen.

No cubierto: la interfaz Swing (ventanas) no tiene pruebas automaticas; ver `riesgos-y-pruebas-C04.md`.
