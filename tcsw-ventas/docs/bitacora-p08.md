# Bitacora P08 - Persistencia (JDBC + PostgreSQL 16)

Equipo TCSW-E01 | Repositorio: https://github.com/pacoscompanyv2/P02_Fernandez_Gonzalez_Jared
Comandos de verificacion (desde `tcsw-ventas/`): `mvn clean test` y, con Docker y `.env`, `mvn clean verify -Pintegracion`

| Fecha y hora | Quien | Accion | Resultado |
|---|---|---|---|
| 2026-10-20 | Jared | Estado base antes de P08 (`main` en `5c1a5ee`, tag `v0.2-swing`) | 85 pruebas, BUILD SUCCESS |
| 2026-10-20 | Jared | Rama `jdbc-base-postgres`: PostgreSQL 16 en Docker Compose, `db/schema.sql`, `ConfiguracionJdbc`, `ProductoRepositoryJdbc`, regla ArchUnit | 96 unitarias, BUILD SUCCESS |
| 2026-10-20 | Jared | `docker compose up -d --wait` y `mvn clean verify -Pintegracion` | contenedor `tcsw-ventas-db` Healthy; 96 unitarias + 3 integracion, 0 fallos, BUILD SUCCESS |
| 2026-10-20 | Jared | Commit de codigo (mensaje corregido dos veces hasta apuntar al issue real) | `4a446fd` (#28) |
| 2026-10-20 | Jared | PR #27 abierto con `Closes #28`; commit de documentacion | `64599fe` (#28) |
| 2026-10-21 | Jesus | Merge del PR #27 (`1c4e511`); issue #28 cerrado | `main` con la base de persistencia |
| 2026-10-23 | Suriel | Issue #29, `ProductoRepositoryJdbcIT` (11), `verificar-p08.sh`, `evidencia-postgres.sh`; commit `c87cca7`; PR #30 | 96 unitarias + 14 integracion |
| 2026-10-23 | Jesus | Merge del PR #30 (`63ace2d`); issue #29 cerrado | - |
| 2026-10-23 | Suriel | Documentacion de su contribucion | `5dd4200` (subida por la web), `3a65703` (en `docs/contribuciones/`) |
| 2026-10-24 (tarde) | Jared | `mvn clean verify -Pintegracion` en `main` tras el PR #30 | 96 unitarias + 14 integracion, BUILD SUCCESS |
| 2026-10-24 | Jesus | Issue #31 abierto | `VentaRepositoryJdbc` con transacciones y `Main` a PostgreSQL |
| 2026-10-24 (capturas de codigo de las 20:19) | Jesus | `RepositoriosFactory`, `VentaRepositoryJdbc`, `Main` y 3 clases de prueba | 6 archivos |
| 2026-10-24 (captura de las 20:39) | Jesus | PR #32 abierto, commit de codigo `7fd9922` | `Closes` sin numero en la primera version |
| 2026-10-24 20:54 | Jesus | `mvn clean test` en su rama | 101 pruebas, BUILD SUCCESS |
| 2026-10-24 | Jesus | Recorrido de la aplicacion con PostgreSQL: venta F-001, reapertura, folio repetido, consultas SQL | 4 capturas (ver protocolo visual) |
| 2026-10-24 | Jesus | Descripcion del PR corregida a `Closes #31`; documentacion `b7dbfc6` (#31) | PR con 2 commits |
| 2026-10-24 | Jared | Merge del PR #32 (`ac92607`); issue #31 cerrado | `main` final |
| 2026-10-24 | Jared | `mvn clean verify -Pintegracion` en `main` final | 101 unitarias + 27 integracion, 0 fallos, 0 errores, BUILD SUCCESS |
| 2026-10-24 22:26 | Jared | Sonar, analisis 1 (proyecto `P08-VENTAS`) | Quality Gate Passed, 15 issues de mantenibilidad, cobertura 72.8%, duplicacion 0.0% |
| (al cerrar) | Jared | Correcciones de documentacion, indice, bitacora, protocolo y etiqueta `v0.3-persistencia` | pendiente de registrar fecha y hash en Eminus |

## Errores y decisiones
- **Permiso de Docker:** `docker ps` fallo con `permission denied` sobre el socket; se agrego el usuario al grupo `docker` y se abrio una sesion nueva del grupo.
- **Puerto 5432 ocupado** por otro PostgreSQL local: se uso `POSTGRES_PORT=5433` y las dos URLs en 5433, solo en el `.env` local (no versionado).
- **Variable vieja exportada:** `docker compose` da prioridad a las variables del shell sobre `.env`; hubo que volver a cargar `.env` (`set -a; . ./.env; set +a`) antes de levantar el contenedor.
- **Variables vacias en la terminal:** una asignacion con espacios (`N=   26`) dejo `N` vacio y el commit salio con un numero equivocado; se corrigio con `git commit --amend` y `git push --force-with-lease` (rama propia, sin mergear). El numero siguio equivocado una vez mas porque el issue aun no existia: se creo el issue (#28) y se corrigio por segunda vez. Aprendizaje: en GitHub issues y PR comparten numeracion, y el issue se crea primero.
- **`Main` real distinto del supuesto:** el parche inicial no coincidia con el `Main` del repositorio; se entrego el `Main` completo y se verifico contra el codigo real antes de subir.
- **Pruebas de integracion sin entorno:** correr `mvn clean verify -Pintegracion` sin cargar `.env` produce 14 errores "Faltan variables de entorno de base de datos". No es un fallo del codigo: el diseno falla con un mensaje claro en lugar de saltarse las pruebas.
- **Doble conteo de existencias al leer ventas:** `Venta.agregarPartida` descuenta stock; `buscarPorFolio` reconstruye cada partida con `existencia actual + cantidad` para no descontar dos veces (ADR-006, limitacion 2).
- **Descripcion del PR #32 con `Closes` sin numero:** el issue no se habria cerrado ni enlazado; se corrigio antes del merge.
- **Documentacion de Jesus con datos que no coincidian con GitHub** (nombre de rama sin el prefijo `3-`, `(#31)` en un titulo que no lo llevaba, `[E_REV]` sin rellenar): corregido en el cierre.
- **Archivo duplicado** `docs/integrante-2-P08.md` (subido por la web): se elimina; la ubicacion correcta es `docs/contribuciones/`.
- **Revisiones:** ningun PR recibio comentario de revision en GitHub; las tres filas quedan NO_VERIFICADO.
- **`venta.cerrada` en `false`:** el caso de uso heredado no cierra la venta. Se declara como limitacion y no se modifico el nucleo a un dia de la entrega.

## Pendiente al cerrar
- Subir esta documentacion y crear la etiqueta `v0.3-persistencia`; registrar el hash en el envio de Eminus.
- Reproducir el recorrido de la aplicacion en la maquina de Jared (hoy solo hay evidencia del equipo de Jesus).
- Regenerar `docs/evidencia/postgres-salida-sanitizada.txt` despues de una venta, para que las tablas de datos no aparezcan vacias.
- Revisar el detalle de los 15 issues de Sonar.
