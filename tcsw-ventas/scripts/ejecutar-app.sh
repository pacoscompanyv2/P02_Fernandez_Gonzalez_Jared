#!/usr/bin/env bash
# Abre la aplicacion Swing. Uso (desde tcsw-ventas/):
#   ./scripts/ejecutar-app.sh              -> JDBC (o memoria si no hay .env)
#   ./scripts/ejecutar-app.sh hibernate    -> Hibernate
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] && { set -a; . ./.env; set +a; }
if [ "${1:-}" = "hibernate" ]; then export TCSW_PERSISTENCIA=hibernate; else unset TCSW_PERSISTENCIA || true; fi
mvn -B -q -DskipTests compile
mvn -B -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt
exec java -cp "target/classes:$(cat target/cp.txt)" adapter.in.swing.Main
