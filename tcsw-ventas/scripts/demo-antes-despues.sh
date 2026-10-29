#!/usr/bin/env bash
# Demostracion "antes/despues" de persistencia: registra una venta en una JVM, REINICIA PostgreSQL y la lee en otra JVM.
# Uso (desde tcsw-ventas/):  ./scripts/demo-antes-despues.sh [jdbc|hibernate]      (por defecto: hibernate)
# Escribe docs/evidencia/demo-antes-despues-<adaptador>.txt (sin URL, usuario ni clave).
set -euo pipefail
cd "$(dirname "$0")/.."
ADAPTADOR="${1:-hibernate}"
[ -f .env ] || { echo "ERROR: falta .env (cp .env.example .env)"; exit 1; }
set -a; . ./.env; set +a
if [ "$ADAPTADOR" = "hibernate" ]; then export TCSW_PERSISTENCIA=hibernate; else unset TCSW_PERSISTENCIA || true; fi
SALIDA="docs/evidencia/demo-antes-despues-$ADAPTADOR.txt"
mkdir -p docs/evidencia

docker compose up -d --wait
mvn -B -q -DskipTests test-compile
mvn -B -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt
CP="target/classes:target/test-classes:$(cat target/cp.txt)"
SQL="SELECT folio, total, cerrada FROM venta WHERE folio='DEMO-C02-1'; SELECT codigo, existencia FROM producto WHERE codigo='P1';"

{
  echo "== Demo antes/despues - adaptador: $ADAPTADOR - $(date '+%Y-%m-%d %H:%M') =="
  docker compose exec -T db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -qc "DELETE FROM venta WHERE folio='DEMO-C02-1';" >/dev/null
  echo "-- JVM 1: registrar la venta"
  java -cp "$CP" demo.DemoPersistencia sembrar 2>>target/demo-stderr.log
  echo "-- PostgreSQL directo (tras la JVM 1):"
  docker compose exec -T db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "$SQL"
  echo "-- Reiniciando el contenedor de PostgreSQL..."
  docker compose restart db >/dev/null
  docker compose up -d --wait >/dev/null
  echo "-- JVM 2 (proceso nuevo): consultar"
  java -cp "$CP" demo.DemoPersistencia consultar 2>>target/demo-stderr.log
} | tee "$SALIDA"
echo
echo "Evidencia guardada en $SALIDA"
