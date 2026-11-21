# Bitacora C04 (candidato de liberacion)

Equipo TCSW-E01. Trabaja el integrante 1; los integrantes 2 y 3 no participaron en este corte (se declara, no se inventa).

- **2026-11-20** Lectura de la guia C04 (R05, 8 pts) y de LE-M14. Se decide trabajar sin los integrantes 2 y 3 y declararlo.
- **2026-11-20** Hallazgo: la guia pide `./scripts/verify-module.sh M14` (del paquete starter de la materia). No esta en el repo ni en la maquina del integrante 1. Decision: escribir un `verify-module.sh` y un `verify-stack.sh` PROPIOS a partir de la descripcion de LE-M14 (salud, consulta, alta, frontend) y declararlo asi; el resultado oficial queda NO_VERIFICADO.
- **2026-11-20** "Congela versiones": se mantienen Spring Boot 3.2.4 y Angular segun `package-lock.json`; imagenes Docker con version fija. No se actualiza nada.
- **2026-11-20** Se agregan `Dockerfile.api`, `Dockerfile.frontend`, `docker/nginx.conf`, servicios `api` y `frontend` (perfil `stack`) al compose, `.github/workflows/ci.yml` y `probar-reversa.sh`. Todo PENDIENTE de ejecucion.

(Las corridas, errores y retests se anotan abajo con fecha, commit, comando y primer error significativo.)

## 2026-11-20 - primeras corridas de verify-module.sh M14 (base 26deb76, cambios sin commit)
- **Corrida 1: FALLA al construir el cliente.** `npm ci` dentro de `node:22.12-alpine` termina con `EUSAGE ... Missing: @emnapi/core@1.11.3, @emnapi/runtime@1.11.3, @emnapi/wasi-threads@1.2.3 from lock file`.
  - Hipotesis 1: la imagen trae npm 10.9.0 y el proyecto declara npm 10.9.8. Prueba: fijar `npm@10.9.8` en la imagen. Resultado: el mismo error. Descartada.
  - Hipotesis 2: Alpine (musl) resuelve distinto que Ubuntu (glibc). Prueba: `node:22.12-bookworm-slim`. Resultado: el mismo error. Descartada.
  - Hecho en la maquina del autor: `npm install --package-lock-only` deja `package-lock.json` sin cambios y `npm ci --dry-run` pasa.
  - **Decision:** el Dockerfile usa `npm install` (respeta el lock y completa lo que falta). **Causa de fondo: NO determinada.** Riesgo declarado: el CI de GitHub usa `npm ci` y podria fallar igual; se vera en su primera corrida.
- **Corrida 2: 7 fallas de 11, error del script (no de la API).** `verify-stack.sh` asumia una base nueva con P1, P2 y P3; el volumen local trae solo P1 (existencia 16), asi que las pruebas con P3 recibieron 404 `PRODUCTO_NO_ENCONTRADO`, respuesta correcta de la API. Correccion: el script elige el primer producto del catalogo con existencia.
- **Corrida 3 (retest): 11 pruebas, 0 fallas, `STACK_VERIFICADO`.** Existencia de P1 de 16 a 15 tras el alta; 409 `FOLIO_DUPLICADO`, 400 `VALIDACION`, 404 `VENTA_NO_ENCONTRADA`; cliente por nginx llega a la API. Esta corrida fue con cambios sin commit; se repite tras el commit para que la evidencia lleve el hash.

## Corridas, errores y retests (cierre del corte)
Las fechas exactas de cada paso estan en `git log`; aqui va el hecho, la causa y la correccion.

- **Imagen del cliente: `npm ci` falla dentro de Docker** (EUSAGE, faltan paquetes `@emnapi/*` en el lock). Hipotesis probadas: version de npm y base Debian; ninguna lo arreglo. Correccion: `npm install` en `Dockerfile.frontend`, con npm 10.9.8 fijo. Causa exacta: NO determinada. `npm ci` si pasa en el CI de GitHub.
- **`verify-stack.sh`: 7 de 11 comprobaciones fallaron.** Hecho: el script suponia el producto P3 y el volumen local solo tenia P1 (la API siembra P1-P3 solo con catalogo vacio). Correccion: el script elige el primer producto con existencia mayor a 0. Retest: 11 comprobaciones, 0 fallas, `STACK_VERIFICADO` (commit `c29d45c`; evidencia en `426f16a`).
- **Puerto 5432 ocupado en la maquina:** el compose usa `POSTGRES_PORT=5433` desde `.env`.
- **Primer push del CI rechazado:** el token no tenia el permiso `workflow`; se genero otro con ese permiso. Ningun token se guarda en el repositorio.
- **Prueba de reversa** (`probar-reversa.sh`): `REVERSA_VERIFICADA`, ventas 3 antes y 3 despues (commit `875ccf9`).
- **Sonar:** analisis de `apicc4` y `frontcc4`; 34 hallazgos tratados (32 de la API y 2 del cliente): los corregidos salieron del parche y 20 de la API quedan abiertos y justificados uno por uno (ver `tratamiento-sonar-C04.md`) (commit `f5d6d32`).
- **Error propio:** la evidencia "antes del parche" se habia atribuido a una corrida posterior al parche; se elimino y se documento solo como "antes" la evidencia ya subida de C03 (`9170d28` / `v0.3-web`).
- **Error propio:** el tratamiento dejo un `import` sin usar en `VentaResponse` (S1128) y el Quality Gate de codigo nuevo quedo en Failed; se quito el import y el gate paso a OK (commit `e096c73`).
- **Cobertura:** la API queda en 73.6 % (Sonar) / 74.3 % (JaCoCo); 176 de las 255 lineas sin cubrir son de las ventanas Swing. Se decide no perseguir el porcentaje y declarar el hueco (`riesgos-y-pruebas-C04.md`, commit `a11a998`).
- **Prueba desde clon limpio:** `git clone` de `main` (`1850ac1`) en una carpeta nueva, `.env` creado desde `.env.example` con clave nueva, base vacia (`down -v` previo) y `./scripts/verify-module.sh M14`: `STACK_VERIFICADO`, 11 comprobaciones, 0 fallas, codigo de salida 0 (`docs/evidencia/clon-limpio-salida.txt`). Limite: las capas de Docker ya estaban en cache, asi que el build fue rapido; no se probo en una maquina sin cache.
