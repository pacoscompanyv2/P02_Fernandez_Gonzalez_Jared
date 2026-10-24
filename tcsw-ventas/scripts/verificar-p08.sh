#!/usr/bin/env bash
# Reproduce P08 de punta a punta con UN comando.
# Uso (desde tcsw-ventas/):  ./scripts/verificar-p08.sh
# Requisitos: Docker + Docker Compose v2, Java 11+ (el pom compila a 11), Maven 3.8+, y el archivo .env (cp .env.example .env)
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || { echo "ERROR: falta .env (cp .env.example .env y cambia la clave)"; exit 1; }
set -a; . ./.env; set +a

echo "== 1/3 PostgreSQL 16 con Docker Compose =="
docker compose up -d --wait

echo "== 2/3 Pruebas unitarias + integracion (mvn clean verify -Pintegracion) =="
mvn -B clean verify -Pintegracion

echo "== 3/3 Salida de PostgreSQL sanitizada =="
./scripts/evidencia-postgres.sh

echo
echo "OK. Para apagar PostgreSQL:  docker compose down      (agrega -v para borrar tambien los datos)"
