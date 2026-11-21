#!/usr/bin/env bash
# IMPLEMENTACION PROPIA de ./scripts/verify-module.sh <modulo>, escrita a partir de la descripcion de LE-M14.
# NO es el script oficial de la materia (el paquete starter no estuvo disponible); solo conoce M14.
# Uso (desde tcsw-ventas/):  ./scripts/verify-module.sh M14
set -uo pipefail
cd "$(dirname "$0")/.."
case "${1:-}" in
  M14)
    ./scripts/verify-stack.sh "${@:2}" || { echo "MODULO_M14_NO_VERIFICADO"; exit 1; }
    echo "MODULO_M14_VERIFICADO (script propio, no el oficial)" | tee -a docs/evidencia/verify-stack-salida.txt
    ;;
  *)
    echo "Modulo '${1:-}' no soportado por este script propio (solo M14)."; exit 2 ;;
esac
