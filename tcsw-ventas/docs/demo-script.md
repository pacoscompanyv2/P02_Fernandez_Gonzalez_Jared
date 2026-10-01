# Guion de demostracion - C01 (2-3 min)

1. Mostrar el repositorio y el tag:
   git checkout v0.1-arquitectura
   git log -1 --oneline

2. Correr las pruebas completas:
   mvn clean test
   -> mostrar "Tests run: 53, Failures: 0, BUILD SUCCESS"

3. Señalar en el codigo (sin ejecutar nada extra):
   - domain/Venta.java: recibe DescuentoStrategy por constructor (Strategy)
   - application/service/RegistrarVentaService.java: notifica observers tras guardar (Observer)
   - application/facade/VentasFacade.java: punto de entrada simple (Facade)
   - antipatron/ConfiguracionGlobalSingletonTest.java: explicar en 1 frase por que
     el Singleton no se uso en el proyecto real (ADR-004)

4. Mostrar el diagrama actualizado (docs/diagramas/arquitectura-c01.png) y señalar
   que domain no tiene flechas saliendo hacia application ni adapter.

5. Cerrar con el resultado de Sonar (Quality Gate Passed) y el tag v0.1-arquitectura
   como evidencia de que el corte es reconstruible desde cero.
