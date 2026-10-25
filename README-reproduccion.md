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

---

# Reproduccion - P08 Persistencia (JDBC + PostgreSQL 16)

## Herramientas y versiones
- Java 21 (el `pom.xml` compila con `maven.compiler.source/target = 11`), Maven, Git
- Docker y Docker Compose v2 (imagen `postgres:16`)
- Driver `org.postgresql:postgresql` 42.7.4, JUnit 5.10.2, ArchUnit 1.3.0, JaCoCo 0.8.11, maven-failsafe 3.2.5

Para registrar TUS versiones reales:

    java -version
    mvn -v
    docker --version
    docker compose version

## 1. Clonar y entrar

    git clone https://github.com/pacoscompanyv2/P02_Fernandez_Gonzalez_Jared
    cd P02_Fernandez_Gonzalez_Jared/tcsw-ventas
    git checkout v0.3-persistencia

## 2. Credenciales locales (no se suben al repositorio)

    cp .env.example .env
    sed -i "s/cambia_esta_clave/$(openssl rand -hex 8)/g" .env

Datos sinteticos y locales. `.env` esta en `.gitignore`; nunca se versiona.

## 3. Pruebas unitarias (NO necesitan Docker)

    mvn clean test

Resultado esperado: `BUILD SUCCESS`, `Failures: 0, Errors: 0` (el total esta en la tabla de estado al final).

## 4. Todo en un comando (PostgreSQL + pruebas unitarias e integracion + salida sanitizada)

    ./scripts/verificar-p08.sh

## 5. Paso a paso

    docker compose up -d --wait            # PostgreSQL 16; aplica db/schema.sql y crea la base *_test
    set -a; . ./.env; set +a               # exporta TCSW_DB_* y TCSW_TEST_DB_URL
    mvn clean verify -Pintegracion         # unitarias (surefire) + integracion (failsafe) contra PostgreSQL
    ./scripts/resumen-pruebas.sh           # suma los reportes reales de Maven

## 6. Ejecutar la aplicacion Swing con PostgreSQL

    set -a; . ./.env; set +a
    mvn -q -DskipTests compile dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/libs
    java -cp "target/classes:target/libs/*" adapter.in.swing.Main

La consola imprime `Almacenamiento: PostgreSQL`. Registra una venta, cierra la ventana, vuelve a abrirla: la existencia
se conserva. Sin las variables `TCSW_DB_*` imprime `Almacenamiento: memoria` y todo funciona como en P07.

## 7. Salida de PostgreSQL sanitizada

    ./scripts/evidencia-postgres.sh        # genera docs/evidencia/postgres-salida-sanitizada.txt (sin claves)

## 8. Apagar y limpiar

    docker compose down                    # apaga (conserva los datos)
    docker compose down -v                 # apaga y BORRA los datos
    rm -rf target/libs                     # copias locales del driver

## 9. Casos de error que se prueban
- Existencia negativa y codigo mas largo que la columna: la base los rechaza (`23514`, `22001`).
- Folio de venta duplicado: se rechaza (`23505`) y la existencia no se descuenta dos veces.
- Venta con un producto que no existe: `rollback` completo (sin venta, sin partidas, existencias intactas).
- Nombre con SQL inyectado (`x'); DROP TABLE producto;--`): se guarda como texto literal.
- Existencia insuficiente por la fachada: se rechaza y la base no cambia.

## 10. Problemas comunes
- `Faltan variables de entorno de base de datos`: falta `set -a; . ./.env; set +a`.
- `password authentication failed`: cambiaste la clave despues de crear el volumen; `docker compose down -v` y vuelve a levantar.
- Puerto 5432 ocupado: cambia `POSTGRES_PORT` y el puerto en `TCSW_DB_URL` / `TCSW_TEST_DB_URL` dentro de `.env`.

## 11. Analisis de calidad (SonarQube local)

    mvn clean verify -Pintegracion org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
      -Dsonar.projectKey=P08-VENTAS -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=<tu-token>

(El token se genera en tu SonarQube y NUNCA se pega en el repositorio ni en capturas.)

## 12. Estructura nueva de P08
- `docker-compose.yml`, `.env.example`, `db/schema.sql`, `db/02-base-pruebas.sh`
- `adapter/out/jdbc/` - `ConexionJdbc`, `ConfiguracionJdbc`, `RepositorioException`, `ProductoRepositoryJdbc`, `VentaRepositoryJdbc`, `RepositoriosFactory`
- `scripts/` - `verificar-p08.sh`, `evidencia-postgres.sh`, `resumen-pruebas.sh`
- `docs/adr/ADR-006-persistencia-jdbc-en-adaptadores.md`

## Estado de verificacion (se actualiza con lo que realmente se ejecuto)

| Comprobacion | Estado | Evidencia |
|---|---|---|
| `mvn clean test` (unitarias, sin Docker) | VERIFICADO | 101 pruebas, 0 fallos, 0 errores (24-oct, main ac92607); ver docs/bitacora-p08.md |
| `docker compose up -d --wait` | VERIFICADO | contenedor tcsw-ventas-db Healthy; ver docs/bitacora-p08.md |
| `mvn clean verify -Pintegracion` (pruebas *IT en PostgreSQL 16) | VERIFICADO | 27 pruebas, 0 fallos, 0 errores (24-oct, main ac92607) |
| Aplicacion Swing con PostgreSQL (venta que sobrevive al reinicio) | VERIFICADO (equipo de Jesus); reproduccion propia NO_VERIFICADO | capturas de Jesus y docs/protocolo-visual-P08.md |
| Analisis Sonar | VERIFICADO | proyecto P08-VENTAS, Quality Gate Passed, captura Overall Code |
