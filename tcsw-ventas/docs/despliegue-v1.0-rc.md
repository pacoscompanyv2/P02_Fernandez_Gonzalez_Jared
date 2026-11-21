# Despliegue local de v1.0-rc (C04)

Todo se ejecuta desde `tcsw-ventas/`. Requisitos: Docker con Compose, `curl`. Para reconstruir y probar fuera de Docker (opcional): JDK 17, Maven y Node 22.

## 1. Configurar (una vez)
```bash
cp .env.example .env     # (si el 5432 de tu maquina esta ocupado, pon POSTGRES_PORT=5433) cambia POSTGRES_PASSWORD y TCSW_DB_PASSWORD (la misma clave); .env nunca se sube
```
Puertos locales: API 8080 y cliente 4200 (cambian con `API_PORT` y `FRONTEND_PORT` en `.env`). Solo escuchan en 127.0.0.1.

## 2. Construir, levantar y verificar (un comando)
```bash
./scripts/verify-module.sh M14        # script PROPIO; construye las imagenes, levanta db + api + frontend y prueba
```
Equivale a `docker compose --profile stack up -d --build --wait` y luego `./scripts/verify-stack.sh --probar`.
Resultado esperado (obtenido en la maquina del integrante 1, VERIFICADO): `STACK_VERIFICADO` y `MODULO_M14_VERIFICADO (script propio, no el oficial)`. Salida en `docs/evidencia/verify-stack-salida.txt`.

## 3. Usar
- Cliente: http://127.0.0.1:4200   - API: http://127.0.0.1:8080/api/v1/productos   - Contrato: http://127.0.0.1:8080/openapi.yaml

## 4. Apagar
```bash
docker compose --profile stack down        # conserva los datos de PostgreSQL
docker compose --profile stack down -v     # BORRA tambien los datos locales (sinteticos)
```

## 5. Artefactos
- API: `tcsw-ventas-1.0.0.jar` ejecutable (`mvn clean verify -Pintegracion spring-boot:repackage`, o el artefacto `api-reportes-y-jar` del CI).
- Cliente: carpeta `dist` (`npx ng build --configuration production`, o el artefacto `cliente-dist` del CI).
- Imagenes: `tcsw-ventas-api` y `tcsw-ventas-frontend` construidas con `Dockerfile.api` y `Dockerfile.frontend`.

## 6. Versiones fijas ("congela versiones")
Spring Boot 3.2.4 (pom), Java 17, Hibernate 6.4.4.Final, PostgreSQL 16, Angular segun `package-lock.json` (el CI instala con `npm ci`; la imagen del cliente usa `npm install` con npm 10.9.8 fijo, ver notas de version),
imagenes `maven:3.9.9-eclipse-temurin-17`, `eclipse-temurin:17.0.13_11-jre`, `node:22.12-bookworm-slim`, `nginx:1.27-alpine`.
No se actualizo Spring ni Angular en este corte.

## 7. Limites declarados
Es un despliegue LOCAL; no hay produccion, HTTPS ni autenticacion. La API no tiene un endpoint de salud propio (no se agrego Actuator para no tocar dependencias): "salud" se comprueba con `GET /api/v1/productos`.
