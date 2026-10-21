#!/usr/bin/env bash
# Suma los reportes REALES de Maven (no hay que teclear numeros a mano).
# Uso (desde tcsw-ventas/, despues de mvn):
#   ./scripts/resumen-pruebas.sh          -> imprime el resumen
#   source <(./scripts/resumen-pruebas.sh --env)   -> deja variables TU FU EU TI FI EI para usar en sed
cd "$(dirname "$0")/.."

suma() {
  local dir="$1"
  if ls "$dir"/*.txt >/dev/null 2>&1; then
    grep -h "^Tests run:" "$dir"/*.txt | awk -F'[:,]' '{t+=$2; f+=$4; e+=$6} END{printf "%d %d %d\n", t, f, e}'
  else
    echo "0 0 0"
  fi
}

read -r TU FU EU <<< "$(suma target/surefire-reports)"
read -r TI FI EI <<< "$(suma target/failsafe-reports)"

if [ "${1:-}" = "--env" ]; then
  echo "TU=$TU; FU=$FU; EU=$EU; TI=$TI; FI=$FI; EI=$EI"
else
  echo "unitarias   (surefire):  total=$TU  fallos=$FU  errores=$EU"
  echo "integracion (failsafe):  total=$TI  fallos=$FI  errores=$EI"
fi
