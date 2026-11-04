#!/usr/bin/env bash
# Levanta la API REST (P09). Uso (desde tcsw-ventas/):
#   ./scripts/ejecutar-api.sh              -> segun el entorno (.env): JDBC si hay TCSW_DB_*, memoria si no
#   ./scripts/ejecutar-api.sh hibernate    -> PostgreSQL con Hibernate
#   ./scripts/ejecutar-api.sh jdbc         -> PostgreSQL con JDBC
#   ./scripts/ejecutar-api.sh memoria      -> sin base de datos
# Escucha en http://127.0.0.1:8080  (otro puerto:  SERVER_PORT=9090 ./scripts/ejecutar-api.sh)
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] && { set -a; . ./.env; set +a; }
case "${1:-}" in
  hibernate) export TCSW_PERSISTENCIA=hibernate ;;
  jdbc)      unset TCSW_PERSISTENCIA || true ;;
  memoria)   unset TCSW_PERSISTENCIA TCSW_DB_URL TCSW_DB_USER TCSW_DB_PASSWORD || true ;;
  "")        ;;
  *)         echo "Uso: $0 [hibernate|jdbc|memoria]" >&2; exit 2 ;;
esac
exec mvn -B spring-boot:run
