#!/usr/bin/env bash
# Recorrido reproducible del cliente Angular (P10). Uso (desde la raiz del repo):
#   ./cliente-angular/scripts/verificar-p10.sh
# Levanta la API (memoria, puerto 8080) y `ng serve` (puerto 4200) y prueba con curl A TRAVES DEL PROXY de Angular
# (http://127.0.0.1:4200/api/...): el cliente sirve su pagina y el proxy llega a la API, incluidos los casos de error.
# Requiere `npm install` hecho en cliente-angular/ y los puertos 8080 y 4200 libres. Sale con codigo 1 si algo falla.
# La salida queda en tcsw-ventas/docs/evidencia/verificar-p10-salida.txt. Los logs de API y cliente van a /tmp.
set -uo pipefail
RAIZ="$(cd "$(dirname "$0")/../.." && pwd)"
SALIDA="$RAIZ/tcsw-ventas/docs/evidencia/verificar-p10-salida.txt"
WEB="http://127.0.0.1:4200"
API="http://127.0.0.1:8080"
LOG_API=/tmp/p10-api.log
LOG_WEB=/tmp/p10-web.log
mkdir -p "$(dirname "$SALIDA")"
: > "$SALIDA"
OK=0; FALLAS=0; PID_API=""; PID_WEB=""

log() { echo "$*" | tee -a "$SALIDA"; }
limpiar() {
  # Mata por puerto: "kill -- -PID" falla en silencio porque $! no es lider de grupo con setsid en segundo plano.
  for p in 4200 8080; do
    for pid in $(ss -ltnp 2>/dev/null | grep -E ":$p\b" | grep -o "pid=[0-9]*" | cut -d= -f2 | sort -u); do kill "$pid" 2>/dev/null; done
  done
}
trap limpiar EXIT

# chk "descripcion" estado_esperado "texto en el cuerpo" METODO ruta [cuerpo-json]
chk() {
  local desc="$1" esperado="$2" texto="$3" metodo="$4" ruta="$5" cuerpo="${6:-}" salida codigo resp
  if [ -n "$cuerpo" ]; then
    salida=$(curl -s -w '\n%{http_code}' -X "$metodo" "$WEB$ruta" -H 'Content-Type: application/json' -d "$cuerpo")
  else
    salida=$(curl -s -w '\n%{http_code}' -X "$metodo" "$WEB$ruta")
  fi
  codigo="${salida##*$'\n'}"; resp="${salida%$'\n'*}"
  if [ "$codigo" = "$esperado" ] && grep -qF -- "$texto" <<<"$resp"; then
    log "OK    [$codigo] $desc"; OK=$((OK + 1))
  else
    log "FALLA [$codigo, esperado $esperado] $desc"; FALLAS=$((FALLAS + 1))
  fi
  log "        $(head -c 170 <<<"$resp" | tr '\n' ' ')"
}

esperar() { # url segundos
  local n=0
  while [ "$n" -lt "$2" ]; do
    [ "$(curl -s -o /dev/null -w '%{http_code}' "$1")" = "200" ] && return 0
    sleep 1; n=$((n + 1))
  done
  return 1
}

for url in "$API/api/v1/productos" "$WEB/"; do
  if [ "$(curl -s -o /dev/null -w '%{http_code}' "$url")" != "000" ]; then
    echo "Ya hay algo escuchando en $url (los puertos 8080 y 4200 deben estar libres). Deten esos procesos y repite." >&2
    exit 2
  fi
done
[ -d "$RAIZ/cliente-angular/node_modules" ] || { echo "Falta npm install en cliente-angular/" >&2; exit 2; }

log "== P10: cliente Angular + API a traves del proxy =="
log "Levantando la API (memoria)..."
(cd "$RAIZ/tcsw-ventas" && setsid ./scripts/ejecutar-api.sh memoria >"$LOG_API" 2>&1 & echo $! >/tmp/p10-api.pid)
PID_API=$(cat /tmp/p10-api.pid)
esperar "$API/api/v1/productos" 120 || { log "FALLA: la API no arranco (ver $LOG_API)"; exit 1; }
log "Levantando el cliente (ng serve)..."
(cd "$RAIZ/cliente-angular" && setsid npx ng serve --host 127.0.0.1 --port 4200 >"$LOG_WEB" 2>&1 & echo $! >/tmp/p10-web.pid)
PID_WEB=$(cat /tmp/p10-web.pid)
esperar "$WEB/" 240 || { log "FALLA: el cliente no arranco (ver $LOG_WEB)"; exit 1; }

FOLIO="P10-$(date +%s)"
chk "el cliente sirve su pagina (app-root)" 200 "app-root" GET "/"
chk "catalogo por el proxy: GET /api/v1/productos" 200 '"codigo":"P1"' GET "/api/v1/productos"
chk "producto inexistente: 404 PRODUCTO_NO_ENCONTRADO" 404 "PRODUCTO_NO_ENCONTRADO" GET "/api/v1/productos/NOPE"
chk "registrar venta: 201 CERRADA" 201 "CERRADA" POST "/api/v1/ventas" "{\"folio\":\"$FOLIO\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":2}]}"
chk "consultar la venta registrada: 200" 200 "$FOLIO" GET "/api/v1/ventas/$FOLIO"
chk "mismo folio otra vez: 409 FOLIO_DUPLICADO" 409 "FOLIO_DUPLICADO" POST "/api/v1/ventas" "{\"folio\":\"$FOLIO\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":1}]}"
chk "mas de la existencia: 409 EXISTENCIA_INSUFICIENTE" 409 "EXISTENCIA_INSUFICIENTE" POST "/api/v1/ventas" "{\"folio\":\"$FOLIO-B\",\"items\":[{\"codigoProducto\":\"P1\",\"cantidad\":9999}]}"
chk "venta sin partidas: 400 VALIDACION" 400 "VALIDACION" POST "/api/v1/ventas" "{\"folio\":\"$FOLIO-C\",\"items\":[]}"
chk "folio inexistente: 404 VENTA_NO_ENCONTRADA" 404 "VENTA_NO_ENCONTRADA" GET "/api/v1/ventas/NO-EXISTE"

log ""
log "== RESULTADO: $((OK + FALLAS)) comprobaciones, $FALLAS fallas =="
[ "$FALLAS" -eq 0 ]
