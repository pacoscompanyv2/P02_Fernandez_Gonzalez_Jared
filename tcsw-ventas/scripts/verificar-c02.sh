#!/usr/bin/env bash
# Reproduce C02 desde CERO con UN comando (esquema nuevo, pruebas unitarias + integracion, demos antes/despues).
# Uso (desde tcsw-ventas/):  ./scripts/verificar-c02.sh
# ATENCION: 'docker compose down -v' BORRA los datos locales de PostgreSQL (son datos sinteticos de la practica).
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || { echo "ERROR: falta .env (cp .env.example .env y cambia la clave)"; exit 1; }
set -a; . ./.env; set +a

echo "== 1/5 Esquema desde cero (down -v + up --wait) =="
docker compose down -v
docker compose up -d --wait

echo "== 2/5 Pruebas unitarias + integracion (mvn clean verify -Pintegracion) =="
mvn -B clean verify -Pintegracion

echo "== 3/5 Resumen de pruebas (de los reportes de Maven) =="
./scripts/resumen-pruebas.sh

echo "== 4/5 Demostracion antes/despues: JDBC y Hibernate =="
./scripts/demo-antes-despues.sh jdbc
./scripts/demo-antes-despues.sh hibernate

echo "== 5/5 Salida de PostgreSQL sanitizada =="
./scripts/evidencia-postgres.sh

echo
echo "OK. Evidencia en docs/evidencia/. Para apagar PostgreSQL:  docker compose down"
