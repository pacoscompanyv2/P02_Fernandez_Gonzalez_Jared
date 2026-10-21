#!/bin/bash
# se ejecuta SOLO la primera vez que se crea el volumen de PostgreSQL (docker-entrypoint-initdb.d).
# crea una base aparte para las pruebas de integracion: asi sus TRUNCATE nunca tocan los datos de la aplicacion.
set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres \
    -c "CREATE DATABASE \"${POSTGRES_DB}_test\""
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "${POSTGRES_DB}_test" \
    -f /docker-entrypoint-initdb.d/01-schema.sql
