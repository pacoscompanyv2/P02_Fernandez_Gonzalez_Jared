#!/usr/bin/env bash
# Genera docs/evidencia/postgres-salida-sanitizada.txt con la salida REAL de PostgreSQL (sin claves ni rutas privadas).
# Uso (desde tcsw-ventas/):  ./scripts/evidencia-postgres.sh      [requiere: docker compose up -d --wait y .env]
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || { echo "ERROR: falta .env (cp .env.example .env)"; exit 1; }
set -a; . ./.env; set +a

SALIDA="docs/evidencia/postgres-salida-sanitizada.txt"
mkdir -p docs/evidencia

psql_app() {
  docker compose exec -T db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=0 -X "$@"
}

{
  echo "# Salida PostgreSQL sanitizada - P08 (datos sinteticos)"
  echo "# Generado (UTC): $(date -u +%Y-%m-%dT%H:%M:%SZ)"
  echo
  echo "## 1. Version del servidor"
  psql_app -c "SELECT version();"
  echo "## 2. Tablas del esquema"
  psql_app -c "\dt"
  echo "## 3. Estructura y restricciones"
  psql_app -c "\d producto" -c "\d venta" -c "\d detalle_venta"
  echo "## 4. Datos persistidos por la aplicacion"
  psql_app -c "SELECT codigo, nombre, precio, existencia FROM producto ORDER BY codigo;"
  psql_app -c "SELECT folio, total, cerrada FROM venta ORDER BY folio;"
  psql_app -c "SELECT folio, linea, codigo, cantidad, precio_unitario FROM detalle_venta ORDER BY folio, linea;"
  echo "## 5. Rechazo de estado invalido (cada intento va en una transaccion que se revierte; no cambia datos)"
  psql_app <<'SQL'
\echo -- 5.1 existencia negativa (CHECK)
BEGIN;
INSERT INTO producto (codigo, nombre, precio, existencia) VALUES ('NEG1', 'Prueba', 10, -1);
ROLLBACK;
\echo -- 5.2 codigo mas largo que la columna
BEGIN;
INSERT INTO producto (codigo, nombre, precio, existencia) VALUES ('CODIGO-DEMASIADO-LARGO-123', 'Prueba', 10, 1);
ROLLBACK;
\echo -- 5.3 partida de un producto que no existe (llave foranea)
BEGIN;
INSERT INTO venta (folio, total) VALUES ('DEMO-FK', 10);
INSERT INTO detalle_venta (folio, linea, codigo, cantidad, precio_unitario) VALUES ('DEMO-FK', 1, 'NO-EXISTE', 1, 10);
ROLLBACK;
\echo -- 5.4 despues del ROLLBACK no quedo nada
SELECT count(*) AS ventas_demo FROM venta WHERE folio = 'DEMO-FK';
SQL
} 2>&1 | sed -e "s/${POSTGRES_PASSWORD}/********/g" -e "s#${HOME}#~#g" > "$SALIDA"

echo "Listo: $SALIDA ($(wc -l < "$SALIDA") lineas)"
