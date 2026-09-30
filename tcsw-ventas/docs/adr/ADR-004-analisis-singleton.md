# ADR-004: Por que no se usa Singleton en tcsw-ventas

## Estado
Aceptado (decision de no usar el patron)

## Contexto
Se evaluo si convenia usar Singleton para manejar configuracion global u otro
estado compartido en el proyecto, en vez de inyeccion de dependencias por
constructor (ya usada en RegistrarVentaService y VentasFacade).

## Decision
No se usa Singleton en el codigo real del proyecto. Se hizo una demo intencional
(antipatron.ConfiguracionGlobalSingleton, fuera del codigo de produccion) para
evidenciar el costo con un caso real y no solo en teoria.

## Alternativas consideradas
Usar Singleton para una futura configuracion global. Se descarto tras la demo:
ConfiguracionGlobalSingletonTest mostro que el valor configurado en una prueba
se filtra a la siguiente, porque la instancia es global y vive durante toda la
ejecucion de la JVM — el orden de ejecucion de las pruebas llego a cambiar el
resultado, y hubo que forzar el orden con @TestMethodOrder para que la demo
fuera reproducible.

## Consecuencias
- Las pruebas del proyecto real se mantienen independientes entre si porque las
  dependencias se reciben por constructor y se pueden reemplazar por dobles de
  prueba (como ya ocurre en RegistrarVentaServiceTest y VentasFacadeTest).
- Se pierde el ahorro de no escribir un constructor con parametros que un
  Singleton hubiera dado.
- Cualquier clase que llame a un Singleton adquiere una dependencia oculta que
  no se ve en su firma; con inyeccion por constructor esa dependencia queda
  explicita.
- Evidencia: antipatron/ConfiguracionGlobalSingleton.java,
  antipatron/ConfiguracionGlobalSingletonTest.java (demo del problema, con orden
  de ejecucion forzado para que sea reproducible).