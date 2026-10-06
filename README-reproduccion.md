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
    git checkout 1-swing-controlador-base
    mvn clean test

Resultado esperado (rama 1-swing-controlador-base, parte del integrante 1):
Tests run: 69, Failures: 0, Errors: 0, BUILD SUCCESS.

El conteo del equipo cambia al integrar las ramas 2-swing-catalogo y 3-swing-carrito-registro.
Al cerrar P07 se debe actualizar aqui el numero final y el commit o tag de entrega:
`<NUMERO_FINAL_DE_PRUEBAS>` pruebas, commit/tag `<TAG_O_COMMIT_FINAL>`.

## Ejecutar la aplicacion
Con la rama integrada (MainFrame de la rama 3):

    mvn clean compile
    java -cp target/classes adapter.in.swing.Main

En la rama del integrante 1 el `Main` solo inicializa el controlador e imprime
"Cliente Swing inicializado con 3 productos." (la ventana la aporta el integrante 3).

## Estructura Swing
- adapter/in/swing/VentasController.java - logica de interfaz; valida entradas y delega en la fachada, sin reglas de negocio
- adapter/in/swing/ProductoTableModel.java, CatalogoPanel.java - catalogo (integrante 2)
- adapter/in/swing/CarritoTableModel.java, CarritoPanel.java, RegistrarVentaWorker.java, MainFrame.java - carrito, registro con SwingWorker y ventana (integrante 3)
- application/facade/VentasFacade.java - punto de entrada; delega el registro al caso de uso
- application/service/RegistrarVentaService.java - caso de uso: valida existencias antes de descontar, descuento inyectable

## Documentos de P07
- docs/adr/ADR-005-facade-delega-en-caso-de-uso.md - decision de arquitectura
- docs/contribuciones/integrante-1-P07.md - contribucion individual del integrante 1
