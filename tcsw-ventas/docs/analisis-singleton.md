# Analisis critico de Singleton (P06)

## Por que NO se usa Singleton en tcsw-ventas

Se hizo una demo (antipatron.ConfiguracionGlobalSingleton) para evidenciar el costo real:

1. *Estado compartido entre pruebas*: ConfiguracionGlobalSingletonTest demuestra que el
   valor configurado en una prueba se filtra a la siguiente, porque la instancia es global y
   vive durante toda la ejecucion de la JVM.
2. *No se puede inyectar un doble de prueba*: en RegistrarVentaService y VentasFacade
   las dependencias se reciben por constructor, lo que permite pasar implementaciones falsas
   en las pruebas. Un Singleton no se puede reemplazar asi de facil.
3. *Dependencia oculta*: cualquier clase puede llamar getInstancia() sin que se vea en su
   constructor ni en su firma, dificultando saber de que depende realmente.

## Costo declarado
Usar Singleton ahorraria escribir un constructor con parametros, pero a cambio las pruebas
dejan de ser independientes (ver demo) y se pierde la posibilidad de tener configuraciones
distintas en la misma JVM.

## Decision
El proyecto sigue usando inyeccion de dependencias por constructor en lugar de Singleton.