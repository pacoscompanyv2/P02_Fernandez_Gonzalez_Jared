# Contribucion - Integrante 3 (P06)

**Nombre:** Jesus Armando Andres Tablilla
**GitHub:** JesusAndres765
**Patron:** Facade + analisis critico de Singleton (antipatron)

## Issue
- **#19** - Facade para registrar ventas y analisis critico de Singleton
- Criterio de cierre: existe el Facade con pruebas de exito/error, y el analisis critico de Singleton con demo y documento, PR mergeado a main
- Estado: aparece Open en las capturas; PENDIENTE confirmar que quedo Closed tras el merge del PR #20

## Rama y commits
- Rama vinculada: `3-facade-singleton`
- Commit: `d538a4a8000cb4772d934ae8924da1221dc4a125` - Facade para registrar ventas, analisis critico de Singleton y ajuste de ArchUnit
- Alcance del commit: 6 archivos cambiados (+140 / -2)

## Decision de patron
- Variacion concreta: punto unico de entrada para registrar ventas (Facade), con dependencias recibidas por constructor
- Alternativa comparada: Singleton (demo en `antipatron.ConfiguracionGlobalSingleton`), descartado porque:
  1. Estado compartido entre pruebas (una prueba ve el valor que dejo otra)
  2. No se puede inyectar un doble de prueba
  3. Dependencia oculta (cualquier clase puede llamar `getInstance()`)
- Costo declarado: el Singleton ahorraria escribir un constructor con parametros, pero las pruebas dejan de ser independientes y se pierde la posibilidad de tener configuraciones distintas en la misma JVM
- Decision: el proyecto conserva la inyeccion de dependencias por constructor

## Codigo creado o modificado
- `application/facade/VentasFacade.java`
- `antipatron/ConfiguracionGlobalSingleton.java` (demo del antipatron)
- `docs/analisis-singleton.md`
- `arch/ArquitecturaHexagonalTest.java` (ajuste de ArchUnit)
- Pruebas: `application/facade/VentasFacadeTest.java` y `antipatron/ConfiguracionGlobalSingletonTest.java`

## Pull Request y revision
- PR #20 - "Facade para registrar ventas, analisis critico de Singleton y ajuste de ArchUnit"
- Mergeado por: Suriel Francisco Tecpile (commit de merge 610afe6)
- Comentario de revision explicito antes del merge: NO (merge directo; el PR no tiene descripcion)

## Pruebas
- `VentasFacadeTest` -> 2 pruebas en verde (exito y error del Facade)
- `ConfiguracionGlobalSingletonTest` -> 2 pruebas; en la corrida con error intencional falla `segundaPruebaVeElValorDejadoPorLaPrimeraEnVezDeUnEstadoLimpio` (esperado 10.0, fue 0.0) y demuestra el antipatron
- Corrida negativa conservada: `mvn clean test` -> Tests run: 53, Failures: 1, BUILD FAILURE (26-sep-2026, 15:32)
- `mvn clean test` en main tras los 3 merges -> 53/53 pruebas en verde, BUILD SUCCESS

## Trazabilidad

| Elemento | Referencia | Estado |
|---|---|---|
| Issue | #19 | VERIFICADO (abierto) |
| Cierre del issue tras el merge | Captura lo muestra Open | PENDIENTE |
| Rama | `3-facade-singleton` | VERIFICADO |
| Commit | `d538a4a` | VERIFICADO |
| Pull Request | #20 | VERIFICADO |
| Commit de merge | `610afe6` | VERIFICADO |
| Revision cruzada (autor distinto a quien mergea) | Suriel mergeo PR de Jesus | VERIFICADO |
| Comentario de revision antes del merge | No hubo | NO_VERIFICADO |
| Pruebas del patron | `VentasFacadeTest` (2), `ConfiguracionGlobalSingletonTest` (2) | VERIFICADO |
| Comprobacion negativa | Corrida con 1 fallo intencional | VERIFICADO |
| Comparacion de alternativa y costo | `docs/analisis-singleton.md` | VERIFICADO |
| Suite completa en main | 53/53 | VERIFICADO |
| Etiqueta | `v1.0-p06` | VERIFICADO |

## Identificador final
- ID (commit): `d538a4a8000cb4772d934ae8924da1221dc4a125`
