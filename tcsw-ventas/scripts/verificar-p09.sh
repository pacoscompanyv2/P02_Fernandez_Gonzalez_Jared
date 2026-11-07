#!/usr/bin/env bash
# Recorrido reproducible de la API de ventas (P09). Uso (desde tcsw-ventas/):
#   ./scripts/verificar-p09.sh                 -> fase A (memoria) y, si hay Docker + .env, fase B (PostgreSQL + Hibernate)
#   ./scripts/verificar-p09.sh --solo-memoria  -> solo la fase A (no necesita Docker)
# Arranca la API en el puerto 18080 (P09_PUERTO para cambiarlo), prueba con curl y la detiene. Sale con codigo 1 si algo falla.
# La salida queda en docs/evidencia/verificar-p09-salida.txt (sin URLs de base ni claves). Los logs de la API van a target/.
set -uo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] && { set -a; . ./.env; set +a; }

PUERTO="${P09_PUERTO:-18080}"
BASE="http://127.0.0.1:$PUERTO"
SALIDA="docs/evidencia/verificar-p09-salida.txt"
CUERPO="target/p09-cuerpo.json"
mkdir -p docs/evidencia target
: > "$SALIDA"
PRUEBAS=0
FALLAS=0
PID=""

log() { echo "$*" | tee -a "$SALIDA"; }

# chk "descripcion" estado_esperado "texto que debe aparecer en el cuerpo (o vacio)" -- argumentos de curl
chk() {
  local desc="$1" esperado="$2" texto="$3"
  shift 3
  PRUEBAS=$((PRUEBAS + 1))
  local codigo ok=1
  codigo=$(curl -s -o "$CUERPO" -w '%{http_code}' "$@")
  [ "$codigo" = "$esperado" ] || ok=0
  if [ -n "$texto" ] && ! grep -qF -- "$texto" "$CUERPO"; then ok=0; fi
  if [ "$ok" = 1 ]; then
    log "OK    [$codigo] $desc"
  else
    log "FALLA [$codigo, esperado $esperado] $desc"
    FALLAS=$((FALLAS + 1))
  fi
  log "        $(head -c 170 "$CUERPO" | tr '\n' ' ')"
}

detener() {
  if [ -n "$PID" ] && kill -0 "$PID" 2>/dev/null; then
    kill "$PID" 2>/dev/null
    wait "$PID" 2>/dev/null
  fi
  PID=""
}
trap detener EXIT

# arrancar nombre_del_log [opciones java...]  (las variables de entorno ya estan exportadas)
arrancar() {
  local nombre="$1"; shift
  SERVER_PORT="$PUERTO" java "$@" -cp "target/classes:$(cat target/cp.txt)" adapter.in.rest.ApiApplication > "target/api-$nombre.log" 2>&1 &
  PID=$!
  local i
  for i in $(seq 1 60); do
    if curl -s -o /dev/null "$BASE/api/v1/productos"; then return 0; fi
    if ! kill -0 "$PID" 2>/dev/null; then log "FALLA: la API se cerro al arrancar (ver target/api-$nombre.log)"; FALLAS=$((FALLAS + 1)); return 1; fi
    sleep 1
  done
  log "FALLA: la API no respondio en 60 s (ver target/api-$nombre.log)"
  FALLAS=$((FALLAS + 1))
  return 1
}

JSON='Content-Type: application/json'

log "== Verificacion P09 - API de ventas  ($(date '+%Y-%m-%d %H:%M')) =="
log "commit: $(git rev-parse --short HEAD 2>/dev/null || echo desconocido)"
log "java:   $(java -version 2>&1 | grep -v 'Picked up' | head -1)"

mvn -B -q -DskipTests compile || { log "FALLA: no compila"; exit 1; }
mvn -B -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt || { log "FALLA: no pude armar el classpath"; exit 1; }

# ---------------------------------------------------------------- FASE A: memoria
log ""
log "== FASE A: almacenamiento en memoria =="
if arrancar memoria -Dtcsw.almacenamiento=memoria; then
  FOLIO="P09-A-$(date +%s)"
  chk "GET  /api/v1/productos -> lista el catalogo"               200 '"P1"'                         "$BASE/api/v1/productos"
  chk "GET  /openapi.yaml -> sirve el contrato"                   200 'openapi: 3.0.3'               "$BASE/openapi.yaml"
  chk "POST /api/v1/ventas -> 201 venta CERRADA"                  201 '"estado":"CERRADA"'          -X POST "$BASE/api/v1/ventas" -H "$JSON" -d "{\"folio\":\"$FOLIO\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":2}]}"
  chk "GET  /api/v1/ventas/{folio} -> 200"                        200 "\"folio\":\"$FOLIO\""         "$BASE/api/v1/ventas/$FOLIO"
  chk "GET  /api/v1/productos/P1 -> existencia 20 menos 2"        200 '"existencia":18'              "$BASE/api/v1/productos/P1"
  chk "POST mismo folio -> 409 FOLIO_DUPLICADO"                   409 'FOLIO_DUPLICADO'              -X POST "$BASE/api/v1/ventas" -H "$JSON" -d "{\"folio\":\"$FOLIO\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":1}]}"
  chk "POST datos invalidos -> 400 VALIDACION con detalles"       400 'items[0].cantidad'            -X POST "$BASE/api/v1/ventas" -H "$JSON" -d '{"folio":" ","items":[{"codigoProducto":"P1","cantidad":0}]}'
  chk "POST JSON roto -> 400 JSON_INVALIDO"                       400 'JSON_INVALIDO'                -X POST "$BASE/api/v1/ventas" -H "$JSON" -d '{"folio":'
  chk "POST producto inexistente -> 404 PRODUCTO_NO_ENCONTRADO"   404 'PRODUCTO_NO_ENCONTRADO'       -X POST "$BASE/api/v1/ventas" -H "$JSON" -d "{\"folio\":\"$FOLIO-2\",\"items\":[{\"codigoProducto\":\"NOPE\",\"cantidad\":1}]}"
  chk "POST sin existencia -> 409 EXISTENCIA_INSUFICIENTE"        409 'EXISTENCIA_INSUFICIENTE'     -X POST "$BASE/api/v1/ventas" -H "$JSON" -d "{\"folio\":\"$FOLIO-3\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":999}]}"
  chk "GET  producto tras el rechazo -> sigue en 18"              200 '"existencia":18'              "$BASE/api/v1/productos/P1"
  chk "GET  venta inexistente -> 404 VENTA_NO_ENCONTRADA"         404 'VENTA_NO_ENCONTRADA'         "$BASE/api/v1/ventas/NO-EXISTE"
  chk "DELETE venta -> 405 METODO_NO_PERMITIDO"                   405 'METODO_NO_PERMITIDO'         -X DELETE "$BASE/api/v1/ventas/$FOLIO"
  chk "GET  ruta inexistente -> 404 RECURSO_NO_ENCONTRADO"        404 'RECURSO_NO_ENCONTRADO'       "$BASE/api/v1/nada"
fi
detener

# ---------------------------------------------------------------- FASE B: PostgreSQL + Hibernate
log ""
log "== FASE B: PostgreSQL con Hibernate (base de pruebas) y persistencia tras reiniciar la API =="
FASE_B=1
[ "${1:-}" = "--solo-memoria" ] && { FASE_B=0; log "OMITIDA (--solo-memoria): NO_VERIFICADO"; }
if [ "$FASE_B" = 1 ] && { [ -z "${TCSW_TEST_DB_URL:-}" ] || [ -z "${POSTGRES_USER:-}" ]; }; then
  FASE_B=0; log "OMITIDA: faltan las variables de .env (set -a; . ./.env; set +a): NO_VERIFICADO"
fi
if [ "$FASE_B" = 1 ] && [[ "$TCSW_TEST_DB_URL" != *_test ]]; then
  FASE_B=0; log "OMITIDA: TCSW_TEST_DB_URL debe terminar en _test (proteccion de la base real): NO_VERIFICADO"
fi
if [ "$FASE_B" = 1 ]; then
  BASE_PRUEBAS="${TCSW_TEST_DB_URL##*/}"
  if ! docker compose exec -T db psql -U "$POSTGRES_USER" -d "$BASE_PRUEBAS" -c "TRUNCATE TABLE detalle_venta, venta, producto" > /dev/null 2>&1; then
    FASE_B=0; log "OMITIDA: PostgreSQL no responde (docker compose up -d --wait): NO_VERIFICADO"
  fi
fi
if [ "$FASE_B" = 1 ]; then
  FOLIO_B="P09-B-$(date +%s)"
  export TCSW_PERSISTENCIA=hibernate
  export TCSW_DB_URL="$TCSW_TEST_DB_URL"
  if arrancar hibernate-1; then
    chk "POST /api/v1/ventas (Hibernate) -> 201 CERRADA"        201 '"estado":"CERRADA"'  -X POST "$BASE/api/v1/ventas" -H "$JSON" -d "{\"folio\":\"$FOLIO_B\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":2}]}"
    chk "GET  producto -> existencia 18 en PostgreSQL"          200 '"existencia":18'     "$BASE/api/v1/productos/P1"
  fi
  detener
  log "-- API detenida y vuelta a arrancar (reinicio) --"
  if arrancar hibernate-2; then
    chk "GET  venta tras REINICIAR la API -> 200 CERRADA"       200 '"estado":"CERRADA"'  "$BASE/api/v1/ventas/$FOLIO_B"
    chk "GET  producto tras reiniciar -> sigue en 18"           200 '"existencia":18'     "$BASE/api/v1/productos/P1"
    chk "POST mismo folio -> 409 FOLIO_DUPLICADO"               409 'FOLIO_DUPLICADO'     -X POST "$BASE/api/v1/ventas" -H "$JSON" -d "{\"folio\":\"$FOLIO_B\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":1}]}"
    chk "POST sin existencia -> 409 y sin cambios"              409 'EXISTENCIA_INSUFICIENTE' -X POST "$BASE/api/v1/ventas" -H "$JSON" -d "{\"folio\":\"$FOLIO_B-X\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":999}]}"
  fi
  detener
  PRUEBAS=$((PRUEBAS + 1))
  CERRADA=$(docker compose exec -T db psql -U "$POSTGRES_USER" -d "$BASE_PRUEBAS" -tAc "SELECT cerrada FROM venta WHERE folio='$FOLIO_B'" 2>/dev/null | tr -d '[:space:]')
  if [ "$CERRADA" = "t" ]; then log "OK    [SQL] la venta $FOLIO_B quedo con cerrada = t en PostgreSQL"; else log "FALLA [SQL] cerrada = '$CERRADA' (esperado t)"; FALLAS=$((FALLAS + 1)); fi
fi

log ""
log "== RESULTADO: $PRUEBAS comprobaciones, $FALLAS fallas =="
[ "$FALLAS" = 0 ]
