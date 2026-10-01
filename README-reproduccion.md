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
