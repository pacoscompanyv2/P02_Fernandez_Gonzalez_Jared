# # Reproduccion - C01 Corte de Arquitectura

## Herramientas y versiones
Java 21 (Eclipse Adoptium/OpenJDK), Maven, JaCoCo 0.8.11, ArchUnit 1.3.0,
SonarQube Community Build, Git.

## Clonar y verificar el corte etiquetado

git clone https://github.com/pacoscompanyv2/P02_Fernandez_Gonzalez_Jared
cd P02_Fernandez_Gonzalez_Jared/tcsw-ventas
git checkout v0.1-arquitectura

## Ejecutar pruebas

mvn clean test

Resultado esperado: Tests run: 53, Failures: 0, Errors: 0, BUILD SUCCESS

## Ejecutar analisis de calidad (opcional, requiere SonarQube corriendo)

mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=TCSW-VENTAS-C01 -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<tu-token>

Resultado esperado: Quality Gate Passed.

## Estructura del nucleo

- domain/ - Venta, Producto, Dinero, DetalleVenta (sin dependencias externas)
- domain/descuento/ - Strategy + Factory del descuento
- application/port/ - puertos de entrada y salida (interfaces)
- application/service/ - RegistrarVentaService (caso de uso)
- application/observer/ - Observer de notificacion
- application/facade/ - VentasFacade (punto de entrada simplificado)
- adapter/out/memory/ - adaptadores en memoria
- antipatron/ - demo de Singleton, no se usa en produccion (ver docs/adr/ADR-004)

## Documentos de la entrega

- docs/adr/ - decisiones de arquitectura (ADR-000 a ADR-004)
- docs/diagramas/arquitectura-c01.png - diagrama actualizado
- docs/trazabilidad.md - matriz de trazabilidad
- docs/contribuciones/ - evidencia individual por integrante

---

# Reproduccion - P07 Cliente Swing

## Herramientas y versiones
Java 21, Maven, JUnit 5.10.2, ArchUnit 1.3.0, JaCoCo 0.8.11, Git. Interfaz en Swing (sin JavaFX).
El `pom.xml` compila con `maven.compiler.source/target = 11`.

## Clonar y ejecutar pruebas

    git clone https://github.com/pacoscompanyv2/P02_Fernandez_Gonzalez_Jared
    cd P02_Fernandez_Gonzalez_Jared/tcsw-ventas
    git checkout v0.2-swing
    mvn clean test

Resultado esperado: Tests run: 85, Failures: 0, Errors: 0, BUILD SUCCESS.

## Ejecutar la aplicacion

    mvn clean compile
    java -cp target/classes adapter.in.swing.Main

Abre la ventana "tcsw-ventas - Cliente Swing" con 3 productos de ejemplo. Requiere entorno grafico.

## Analisis de calidad (opcional, requiere SonarQube corriendo)

    mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
      -Dsonar.projectKey=TCSW-VENTAS-p07 -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=<tu-token>

Resultado de referencia: Quality Gate Passed (17 issues en el primer analisis, 10 despues de atender `java:S1948`).

## Estructura Swing
- adapter/in/swing/VentasController.java - logica de interfaz; valida entradas y delega en la fachada, sin reglas de negocio
- adapter/in/swing/ProductoTableModel.java, CatalogoPanel.java - catalogo
- adapter/in/swing/CarritoTableModel.java, CarritoPanel.java, RegistrarVentaWorker.java, MainFrame.java - carrito, registro con SwingWorker y ventana
- adapter/in/swing/Main.java - raiz de composicion (repositorios en memoria, fachada, controlador, ventana)
- application/facade/VentasFacade.java - punto de entrada; delega el registro en el caso de uso
- application/service/RegistrarVentaService.java - caso de uso: valida existencias antes de descontar; descuento inyectable

## Documentos de P07
- docs/adr/ADR-005-facade-delega-en-caso-de-uso.md - decision de arquitectura
- docs/contribuciones/integrante-1-P07.md, integrante-2-P07.md, integrante-3-P07.md - contribucion individual
- docs/indice-evidencias-P07.md - evidencia por criterio de R01
- docs/bitacora-p07.md - bitacora de ejecucion y errores
- docs/protocolo-visual-P07.md - recorrido visual
- docs/capturas/ - capturas del recorrido y de Sonar

## Limites conocidos
- Las vistas Swing no tienen pruebas automaticas (cobertura 66.1% en Sonar).
- Repositorios en memoria: sin persistencia ni concurrencia.
- El carrito del controlador no es seguro ante hilos.
