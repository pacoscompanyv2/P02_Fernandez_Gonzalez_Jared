#!/usr/bin/env bash
# C04: levanta PostgreSQL + API + cliente en contenedores y prueba salud, consulta, alta, errores y frontend.
# Uso (desde tcsw-ventas/):  ./scripts/verify-stack.sh            -> construye, levanta y prueba
#                            ./scripts/verify-stack.sh --probar   -> solo prueba (el stack ya esta arriba)
# Termina con STACK_VERIFICADO (codigo 0) o con codigo 1 si algo falla. Salida en docs/evidencia/verify-stack-salida.txt
# Apagar:  docker compose --profile stack down      (agrega -v para borrar tambien los datos locales de PostgreSQL)
# Cada corrida completa descuenta 1 unidad de un producto con existencia; si se agotan, "down -v" y repetir con base nueva.
set -uo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || { echo "ERROR: falta .env (cp .env.example .env y cambia la clave)"; exit 1; }
set -a; . ./.env; set +a
API="http://127.0.0.1:${API_PORT:-8080}"
WEB="http://127.0.0.1:${FRONTEND_PORT:-4200}"
SALIDA="docs/evidencia/verify-stack-salida.txt"
CUERPO="$(mktemp)"
mkdir -p docs/evidencia
: > "$SALIDA"
FALLAS=0; PRUEBAS=0
trap 'rm -f "$CUERPO"' EXIT
log() { echo "$*" | tee -a "$SALIDA"; }

# chk "descripcion" estado_esperado "texto en el cuerpo (o vacio)" -- argumentos de curl
chk() {
  local desc="$1" esperado="$2" texto="$3"; shift 3
  PRUEBAS=$((PRUEBAS + 1))
  local codigo ok=1
  codigo=$(curl -s -o "$CUERPO" -w '%{http_code}' "$@")
  [ "$codigo" = "$esperado" ] || ok=0
  if [ -n "$texto" ] && ! grep -qF -- "$texto" "$CUERPO"; then ok=0; fi
  if [ "$ok" = 1 ]; then log "OK    [$codigo] $desc"; else log "FALLA [$codigo, esperado $esperado] $desc"; FALLAS=$((FALLAS + 1)); fi
  log "        $(head -c 150 "$CUERPO" | tr '\n' ' ')"
}
existencia() { curl -s "$API/api/v1/productos/$1" | grep -o '"existencia":[0-9]*' | head -1 | cut -d: -f2; }

log "== verify-stack (C04) $(date '+%Y-%m-%d %H:%M') =="
log "commit: $(git rev-parse --short HEAD 2>/dev/null || echo desconocido)"
log "docker: $(docker --version 2>&1 | head -1)"

if [ "${1:-}" != "--probar" ]; then
  log "== 1. Construir y levantar (docker compose --profile stack up -d --build --wait) =="
  if ! docker compose --profile stack up -d --build --wait >> "$SALIDA" 2>&1; then
    log "FALLA: el stack no levanto (ver el final de $SALIDA y 'docker compose --profile stack logs')"
    exit 1
  fi
  log "OK    db, api y frontend en estado healthy"
fi

J='Content-Type: application/json'
FOLIO="C04-V-$(date +%s)"
log "== 2. Salud y consulta (API) =="
chk "GET  /api/v1/productos -> la API responde y lista el catalogo"  200 '"codigo"' "$API/api/v1/productos"
chk "GET  /openapi.yaml -> el contrato se sirve"                      200 'openapi:' "$API/openapi.yaml"
# producto de prueba: el primero del catalogo con existencia (base nueva: P1; una base vieja puede traer otros)
PROD=$(curl -s "$API/api/v1/productos" | tr '{' '\n' | grep -E '"existencia":[1-9]' | head -1 | grep -o '"codigo":"[^"]*"' | cut -d'"' -f4)
log "      producto de prueba: ${PROD:-ninguno con existencia}"
ANTES=$(existencia "$PROD"); log "      existencia de $PROD antes del alta: ${ANTES:-?}"
chk "GET  /api/v1/productos/$PROD -> consulta de un producto"        200 "\"codigo\":\"$PROD\"" "$API/api/v1/productos/$PROD"

log "== 3. Alta (la venta se guarda en PostgreSQL por Hibernate) =="
chk "POST /api/v1/ventas -> 201 venta CERRADA"   201 '"estado":"CERRADA"' -X POST "$API/api/v1/ventas" -H "$J" -d "{\"folio\":\"$FOLIO\",\"items\":[{\"codigoProducto\":\"$PROD\",\"cantidad\":1}]}"
chk "GET  /api/v1/ventas/{folio} -> la venta existe" 200 "\"folio\":\"$FOLIO\"" "$API/api/v1/ventas/$FOLIO"
DESPUES=$(existencia "$PROD"); log "      existencia de $PROD despues del alta: ${DESPUES:-?}"
PRUEBAS=$((PRUEBAS + 1))
if [ -n "${ANTES:-}" ] && [ -n "${DESPUES:-}" ] && [ "$DESPUES" -eq $((ANTES - 1)) ]; then log "OK    la existencia bajo en 1 (efecto persistido)"; else log "FALLA la existencia no bajo en 1 (antes=${ANTES:-?}, despues=${DESPUES:-?})"; FALLAS=$((FALLAS + 1)); fi

log "== 4. Errores controlados =="
chk "POST mismo folio -> 409 FOLIO_DUPLICADO"           409 'FOLIO_DUPLICADO'      -X POST "$API/api/v1/ventas" -H "$J" -d "{\"folio\":\"$FOLIO\",\"items\":[{\"codigoProducto\":\"$PROD\",\"cantidad\":1}]}"
chk "POST datos invalidos -> 400 VALIDACION"            400 'VALIDACION'           -X POST "$API/api/v1/ventas" -H "$J" -d '{"folio":" ","items":[{"codigoProducto":"P3","cantidad":0}]}'
chk "GET  venta inexistente -> 404 VENTA_NO_ENCONTRADA" 404 'VENTA_NO_ENCONTRADA'  "$API/api/v1/ventas/NO-EXISTE"

log "== 5. Cliente Angular (nginx) y su camino hacia la API =="
chk "GET  / -> el cliente sirve su pagina"              200 '<app-root'            "$WEB/"
chk "GET  /api/v1/productos por nginx -> llega a la API" 200 '"codigo"'            "$WEB/api/v1/productos"

log ""
log "pruebas: $PRUEBAS   fallas: $FALLAS"
if [ "$FALLAS" -eq 0 ]; then log "STACK_VERIFICADO"; exit 0; fi
log "STACK_NO_VERIFICADO ($FALLAS fallas)"; exit 1
