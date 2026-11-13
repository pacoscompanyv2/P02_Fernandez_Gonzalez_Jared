# Contribucion - Integrante 3 (P10)

**Nombre:** Jesus Armando Andres Tablilla
**GitHub:** JesusAndres765
**Alcance:** formulario reactivo de venta, consulta de venta por folio, pruebas de ambos con casos de error y script de recorrido cliente + API

## Issue
- **#50** - Cliente Angular: formulario de venta, consulta y recorrido de integracion
- Criterio de cierre: la venta se registra desde el formulario contra la API y muestra la confirmacion; los errores 400/404/409 y la falta de conexion se muestran con mensaje claro; consulta por folio; `scripts/verificar-p10.sh` pasa; `ng test` en verde; PR integrado a main

## Rama y commits
- Rama: `cliente-venta-consulta`
- Commit de codigo: `ac33f16` - feat: formulario de venta, consulta y recorrido del cliente Angular (#) (el mensaje quedo sin el numero de issue; el issue es el #50)

## Decision tecnica
- Formulario reactivo con un solo campo (folio): obligatorio, maximo 30 y no solo espacios; el boton se deshabilita si el formulario es invalido, el carrito esta vacio o hay un envio en curso
- El cuerpo de la peticion lo arma `CarritoService.aRequest`, el formulario no conoce la forma interna del carrito
- Si la API responde error, el carrito se conserva para corregir y reintentar; si responde 201 se muestra la venta tal cual la devolvio la API (estado y total reales) y se vacia el carrito
- La consulta acepta el folio por la URL (`?folio=`) con `withComponentInputBinding`
- `verificar-p10.sh` prueba a traves del proxy de Angular (puerto 4200), no directo a la API, para comprobar la integracion real

## Codigo creado o modificado
- `venta/venta.ts` y `consulta/consulta.ts` (reemplazan los marcadores)
- Pruebas: `venta/venta.spec.ts`, `consulta/consulta.spec.ts`
- `cliente-angular/scripts/verificar-p10.sh`, `docs/evidencia/verificar-p10-salida.txt`

## Pull Request y revision
- PR #51 - "feat: formulario de venta, consulta y recorrido del cliente Angular" (el titulo quedo sin el numero de issue) - Closes #50
- Revisor: Jared Fernandez Gonzalez aparecio solo como revisor sugerido por GitHub; no se solicito revision formal
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- `ng test`: 49 pruebas (todo el proyecto)
- `verificar-p10.sh`: 9 comprobaciones

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #50 | VERIFICADO |
| Rama | `cliente-venta-consulta` | VERIFICADO |
| Commit de codigo | `ac33f16` (mensaje sin numero de issue) | VERIFICADO |
| Pull Request | #51 (Closes #50) | VERIFICADO |
| `ng test`: 49 pruebas, 0 fallos | salida de la terminal | VERIFICADO |
| `verificar-p10.sh`: 9 comprobaciones, 0 fallas | captura y `docs/evidencia/verificar-p10-salida.txt` | VERIFICADO |
| Venta registrada desde el navegador y consulta del folio | capturas | VERIFICADO |
| Comentario de revision antes del merge | revisor: Jared | NO_VERIFICADO |

## Limitaciones declaradas
- El recorrido del navegador (clics en el formulario) se probo a mano y con pruebas de componente; no hay pruebas automaticas de extremo a extremo con un navegador real
- `verificar-p10.sh` comprueba pagina y proxy con `curl`; no ejecuta el JavaScript del cliente
- El total que muestra la confirmacion es el de la API; el carrito solo estima
