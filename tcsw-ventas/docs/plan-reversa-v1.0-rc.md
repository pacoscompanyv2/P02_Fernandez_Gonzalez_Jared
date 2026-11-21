# Plan de reversa de v1.0-rc (C04)

Version anterior estable: **`v0.3-web`** (commit `26deb76`, C03). Esquema de base de datos sin cambios entre ambas versiones: la reversa no necesita migrar datos.

## Cuando aplicarla
Si tras desplegar `v1.0-rc` falla `verify-stack.sh`, o la API/cliente no levantan, o se detecta un defecto grave.

## Pasos
1. Conservar el primer error: `docker compose --profile stack logs --no-color > /tmp/fallo-rc.txt` (revisa que no tenga claves antes de compartirlo).
2. Respaldo opcional de datos: `docker compose exec -T db pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" > respaldo.sql` (fuera del repo; nunca se sube).
3. Bajar el rc SIN borrar datos: `docker compose --profile stack down` (sin `-v`).
4. Volver a la version anterior en un directorio aparte: `git worktree add --detach ../tcsw-previo v0.3-web`.
5. Levantar y comprobar la anterior desde ese directorio: `cd ../tcsw-previo/tcsw-ventas && cp ../../<repo>/tcsw-ventas/.env . && ./scripts/verificar-c03.sh`.
6. Registrar en la bitacora el motivo, la version y el comando; el historial y la evidencia NO se borran.

## Prueba (automatizada)
`./scripts/probar-reversa.sh` ejecuta los pasos 3 a 5 con `verificar-p09.sh` de `v0.3-web`, comprueba que el numero de ventas en PostgreSQL no cambia y vuelve a verificar el rc.
Salida: `docs/evidencia/probar-reversa-salida.txt`. Estado: **PENDIENTE** hasta que se ejecute y se conserve esa salida.

## Limites
La reversa es de codigo e imagenes, no de datos creados por el rc (no hay migraciones). Una reversa en produccion no existe: no hay produccion.
