# Contribucion - Integrante 3 (P09)

**Nombre:** Jesus Armando Andres Tablilla
**GitHub:** JesusAndres765
**Alcance:** contrato OpenAPI, prueba de contrato, pruebas de extremo a extremo (HTTP y PostgreSQL con Hibernate) y script de recorrido reproducible

## Issue
- **#44** - API REST: contrato OpenAPI y pruebas de extremo a extremo
- Criterio de cierre: `openapi.yaml` servido en `/openapi.yaml` y verificado contra los controladores; recorrido por HTTP real en memoria; pruebas contra PostgreSQL con Hibernate (venta cerrada, rollback, persistencia tras reiniciar); `scripts/verificar-p09.sh` reproduce todo; `mvn clean verify -Pintegracion` en verde y PR integrado a main

## Rama y commits
- Rama: `3-api-contrato-pruebas`
- Commit de codigo: `52da8a2` - feat: contrato OpenAPI y pruebas de extremo a extremo de la API
- El mensaje de ese commit no lleva el numero de issue (#44) y la descripcion del PR dice `Closes #45` (su propio numero): la trazabilidad commit-issue de esta parte es incompleta y el issue #44 no se cierra solo al integrar

## Decision tecnica
- Contrato primero: `openapi.yaml` escrito a mano y comprobado por `ContratoApiTest` (rutas, metodos, respuestas, campos de DTO y codigos de error contra el codigo real)
- Tres niveles de prueba: controlador aislado (MockMvc + Mockito), HTTP real en memoria (`ApiRecorridoTest`) y PostgreSQL real (`ApiVentasHibernateIT`)
- El script `verificar-p09.sh` usa la base `*_test` y se niega a correr si el nombre no termina en `_test`
- Ajuste: `@PathVariable("folio")` con nombre explicito en `VentaController.obtener`

## Codigo creado o modificado
- `src/main/resources/static/openapi.yaml`
- Pruebas: `ContratoApiTest`, `ApiRecorridoTest`, `ApiVentasHibernateIT`
- `scripts/verificar-p09.sh`, `docs/evidencia/verificar-p09-salida.txt`

## Pull Request y revision
- PR #45 - "feat: contrato OpenAPI y pruebas de extremo a extremo de la API" - integrado por Jared Fernandez Gonzalez
- Revisor asignado: Jared Fernandez Gonzalez
- Comentario de revision explicito en GitHub antes del merge: NO_VERIFICADO

## Pruebas
- Unitarias (`mvn clean test`): 174 pruebas
- Integracion (`mvn clean verify -Pintegracion`): 56 pruebas

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #44 | VERIFICADO |
| Rama | `3-api-contrato-pruebas` | VERIFICADO |
| Commit de codigo | `52da8a2` | VERIFICADO |
| Pull Request | #45 | VERIFICADO |
| Pruebas unitarias | 174 | VERIFICADO |
| Pruebas de integracion | 56 | VERIFICADO |
| `verificar-p09.sh` (fases A y B): 21 comprobaciones, 0 fallas | captura y `docs/evidencia/verificar-p09-salida.txt` | VERIFICADO |
| Comentario de revision antes del merge | revisor: Jared | NO_VERIFICADO |
| Issue #44 cerrado por el merge del PR | la descripcion del PR apunta a #45 | NO_VERIFICADO |

## Deuda tecnica declarada (API)

| Id | Descripcion | Decision |
|---|---|---|
| D-API-1 | El folio repetido se comprueba antes de registrar; dos peticiones simultaneas con el mismo folio pasan la comprobacion y la llave primaria de PostgreSQL rechaza la segunda con un error de persistencia (respuesta 500, no 409) | ACEPTADA: la base impide el dato duplicado |
| D-API-2 | Con la persistencia en memoria el catalogo y las ventas no son seguros entre hilos (HashMap) | ACEPTADA: la memoria es solo para pruebas y demostracion |
| D-API-3 | La API no tiene autenticacion ni limite de peticiones; escucha solo en 127.0.0.1 | ACEPTADA: fuera del alcance de P09 |
| D-API-4 | Si el stock se agota entre la validacion y el bloqueo (carrera con Hibernate), el adaptador lanza `RepositorioHibernateException` y la API responde 500 en vez de 409; la base queda consistente (hay rollback) | ACEPTADA: exige un error de aplicacion comun a los adaptadores |
| D-API-5 | La composicion (elegir memoria, JDBC o Hibernate y sembrar el catalogo) esta repetida en `Main` de Swing y en `ConfiguracionApi` | PENDIENTE: extraer una fabrica compartida |
| D-API-6 | El total de una venta consultada se recalcula con el descuento por defecto (limitacion heredada de ADR-006) | ACEPTADA |

## Limitaciones declaradas
- `verificar-p09.sh` fase B solo corre con Docker y las variables de `.env`; si se omite queda NO_VERIFICADO en la salida
- La prueba de contrato compara estructura (rutas, campos, codigos); no valida tipos ni formatos de cada campo
