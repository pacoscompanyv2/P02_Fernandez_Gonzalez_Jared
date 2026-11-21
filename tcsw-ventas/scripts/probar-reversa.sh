#!/usr/bin/env bash
# C04: prueba el plan de reversa (docs/plan-reversa-v1.0-rc.md) sin borrar datos.
# Uso (desde tcsw-ventas/, con el stack rc arriba y verify-stack ya ejecutado):  ./scripts/probar-reversa.sh [tag-anterior]
# 1) cuenta ventas en PostgreSQL  2) baja el stack rc (NO borra el volumen)  3) crea un worktree del tag anterior (por defecto v0.3-web)
# y corre su verificar-p09.sh (usa la base de PRUEBAS, no la de la aplicacion)  4) comprueba que las ventas no cambiaron
# 5) vuelve a levantar el rc y repite verify-stack.   Salida en docs/evidencia/probar-reversa-salida.txt
set -uo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || { echo "ERROR: falta .env"; exit 1; }
set -a; . ./.env; set +a
TAG="${1:-v0.3-web}"
SALIDA="docs/evidencia/probar-reversa-salida.txt"
mkdir -p docs/evidencia; : > "$SALIDA"
FALLAS=0
log() { echo "$*" | tee -a "$SALIDA"; }
ventas() { docker compose exec -T db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -tAc "SELECT count(*) FROM venta" 2>/dev/null | tr -d '[:space:]'; }
WT="$(mktemp -d)/previo"

log "== probar-reversa (C04) $(date '+%Y-%m-%d %H:%M') =="
log "commit rc: $(git rev-parse --short HEAD)   tag anterior: $TAG"
git rev-parse -q --verify "refs/tags/$TAG" > /dev/null || { log "FALLA: no existe el tag $TAG (git fetch --tags)"; exit 1; }

ANTES=$(ventas); log "ventas en PostgreSQL antes: ${ANTES:-?}"
log "== 1. Bajar el stack rc (sin -v: los datos se conservan) =="
docker compose --profile stack down >> "$SALIDA" 2>&1 && log "OK    stack rc detenido" || { log "FALLA al bajar el stack"; FALLAS=$((FALLAS+1)); }
docker compose up -d --wait >> "$SALIDA" 2>&1 || { log "FALLA: PostgreSQL no levanto"; exit 1; }

log "== 2. Version anterior ($TAG) en un worktree aparte =="
if git worktree add --detach "$WT" "$TAG" >> "$SALIDA" 2>&1; then
  cp .env "$WT/tcsw-ventas/.env"
  if ( cd "$WT/tcsw-ventas" && ./scripts/verificar-p09.sh ) > "$WT/p09.txt" 2>&1; then log "OK    $TAG: verificar-p09.sh sin fallas"; else log "FALLA $TAG: verificar-p09.sh (ultimas lineas abajo)"; tail -8 "$WT/p09.txt" | tee -a "$SALIDA"; FALLAS=$((FALLAS+1)); fi
  tail -3 "$WT/p09.txt" | tee -a "$SALIDA"
  git worktree remove --force "$WT" >> "$SALIDA" 2>&1
else
  log "FALLA: no pude crear el worktree de $TAG"; FALLAS=$((FALLAS+1))
fi

log "== 3. Los datos de la aplicacion no cambiaron =="
DESPUES=$(ventas); log "ventas en PostgreSQL despues: ${DESPUES:-?}"
if [ -n "$ANTES" ] && [ "$ANTES" = "$DESPUES" ]; then log "OK    mismas ventas antes y despues"; else log "FALLA las ventas cambiaron"; FALLAS=$((FALLAS+1)); fi

log "== 4. Volver al rc y verificarlo otra vez =="
docker compose --profile stack up -d --wait >> "$SALIDA" 2>&1 || { log "FALLA: el rc no volvio a levantar"; FALLAS=$((FALLAS+1)); }
./scripts/verify-stack.sh --probar >> "$SALIDA" 2>&1 && log "OK    el rc vuelve a pasar verify-stack" || { log "FALLA verify-stack tras la reversa"; FALLAS=$((FALLAS+1)); }

log ""; log "fallas: $FALLAS"
[ "$FALLAS" -eq 0 ] && { log "REVERSA_VERIFICADA"; exit 0; }
log "REVERSA_NO_VERIFICADA"; exit 1
