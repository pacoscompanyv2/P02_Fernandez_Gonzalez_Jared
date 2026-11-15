#!/usr/bin/env bash
# Reproduce C03 (integracion web) con UN comando: pruebas de la API, recorrido de la API (P09), pruebas del cliente
# Angular y recorrido cliente + API a traves del proxy (P10).
# Uso (desde tcsw-ventas/):
#   ./scripts/verificar-c03.sh               -> con Docker: las pruebas de la API incluyen integracion con PostgreSQL
#   ./scripts/verificar-c03.sh --sin-docker  -> solo pruebas unitarias de la API (integracion queda NO_VERIFICADO)
# Requiere JDK 17, Maven, Node 22 con npm y curl. Los puertos 8080, 18080 y 4200 deben estar libres.
# No borra datos de PostgreSQL. La salida (solo resumen, sin claves ni URLs de base) queda en docs/evidencia/verificar-c03-salida.txt;
# el detalle de cada paso queda en target/c03-*.log. Sale con codigo 1 si algun paso falla.
set -uo pipefail
cd "$(dirname "$0")/.."
RAIZ="$(cd .. && pwd)"
SALIDA="docs/evidencia/verificar-c03-salida.txt"
CON_DOCKER=1
[ "${1:-}" = "--sin-docker" ] && CON_DOCKER=0
mkdir -p "$(dirname "$SALIDA")" target
: > "$SALIDA"

log() { echo "$*" | tee -a "$SALIDA"; }
sin_color() { sed 's/\x1b\[[0-9;]*[A-Za-z]//g'; }
FALLAS=0

# paso "descripcion" archivo-de-log comando...   (ejecuta, anota OK/FALLA y deja el detalle en el log)
paso() {
  local desc="$1" arch="$2"; shift 2
  log ""
  log "== $desc =="
  if "$@" >"$arch" 2>&1; then
    log "OK"
    return 0
  fi
  log "FALLA (detalle en ${arch#"$PWD"/})"
  sin_color <"$arch" | tail -12 | cut -c1-160 | sed 's/^/    /' | tee -a "$SALIDA"
  FALLAS=$((FALLAS + 1))
  return 1
}

# 0. Puertos libres (los recorridos levantan sus propios procesos)
for url in "http://127.0.0.1:8080/api/v1/productos" "http://127.0.0.1:18080/api/v1/productos" "http://127.0.0.1:4200/"; do
  if [ "$(curl -s -m 3 -o /dev/null -w '%{http_code}' "$url")" != "000" ]; then
    echo "Ya hay algo escuchando en $url (los puertos 8080, 18080 y 4200 deben estar libres). Detenlo y repite." >&2
    exit 2
  fi
done
[ -d "$RAIZ/cliente-angular" ] || { echo "No encuentro $RAIZ/cliente-angular" >&2; exit 2; }

log "== C03: integracion web (API + cliente Angular + nucleo) =="
log "fecha: $(date '+%Y-%m-%d %H:%M')  commit: $(git -C "$RAIZ" rev-parse --short HEAD 2>/dev/null || echo desconocido)"

# 1. API: pruebas unitarias (y de integracion con PostgreSQL si hay Docker)
if [ "$CON_DOCKER" -eq 1 ]; then
  [ -f .env ] || { echo "ERROR: falta .env (cp .env.example .env y cambia la clave), o usa --sin-docker" >&2; exit 2; }
  set -a; . ./.env; set +a
  paso "1/5 PostgreSQL (docker compose up --wait)" target/c03-1-docker.log docker compose up -d --wait
  paso "2/5 API: mvn clean verify -Pintegracion (unitarias + integracion + reglas ArchUnit)" target/c03-2-mvn.log mvn -B clean verify -Pintegracion
else
  log ""; log "== 1/5 PostgreSQL: OMITIDO (--sin-docker) =="
  paso "2/5 API: mvn clean verify (solo unitarias + reglas ArchUnit; integracion NO_VERIFICADO)" target/c03-2-mvn.log mvn -B clean verify
fi
./scripts/resumen-pruebas.sh | tee -a "$SALIDA"

# 3. API: recorrido por HTTP (solo memoria, no necesita Docker)
paso "3/5 API: recorrido por HTTP (verificar-p09.sh --solo-memoria)" target/c03-3-p09.log ./scripts/verificar-p09.sh --solo-memoria
grep -iE "RESULTADO|fallas" target/c03-3-p09.log | sin_color | tail -2 | tee -a "$SALIDA"

# 4. Cliente: instalar, compilar y probar
paso "4/5 Cliente: npm install, ng build y ng test" target/c03-4-cliente.log \
  bash -c 'cd "$1/cliente-angular" && npm install --no-audit --no-fund && npx ng build && npx ng test --watch=false' _ "$RAIZ"
sin_color <target/c03-4-cliente.log | grep -E "Test Files|Tests " | tee -a "$SALIDA"

# 5. Cliente + API por el proxy de Angular
paso "5/5 Cliente + API por el proxy (verificar-p10.sh)" target/c03-5-p10.log "$RAIZ/cliente-angular/scripts/verificar-p10.sh"
grep -E "RESULTADO" target/c03-5-p10.log | tail -1 | tee -a "$SALIDA"

log ""
if [ "$FALLAS" -eq 0 ]; then
  log "== RESULTADO C03: 5 pasos, 0 fallas =="
  [ "$CON_DOCKER" -eq 1 ] || log "(sin Docker: las pruebas de integracion con PostgreSQL NO se ejecutaron: NO_VERIFICADO)"
  [ "$CON_DOCKER" -eq 1 ] && log "(PostgreSQL sigue arriba; para apagarlo:  docker compose down)"
  exit 0
fi
log "== RESULTADO C03: $FALLAS paso(s) con falla =="
exit 1
