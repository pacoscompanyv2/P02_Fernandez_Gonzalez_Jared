# Notas de version: v1.0-rc (candidato de liberacion, C04)

Base: `v0.3-web` (C03). Equipo TCSW-E01. Estado de cada afirmacion segun `indice-evidencias-C04.md`.

## Que incluye
- **API REST** (Spring Boot 3.2.4): `GET /api/v1/productos`, `GET /api/v1/productos/{codigo}`, `POST /api/v1/ventas`, `GET /api/v1/ventas/{folio}`; errores con codigo (`VALIDACION`, `PRODUCTO_NO_ENCONTRADO`, `VENTA_NO_ENCONTRADA`, `EXISTENCIA_INSUFICIENTE`, `FOLIO_DUPLICADO`, ...). Contrato en `openapi.yaml`.
- **Cliente Angular 21**: catalogo, carrito, venta, consulta por folio; estados de carga, vacio y error (incluido `SIN_CONEXION`).
- **Nucleo compartido** (arquitectura hexagonal): la API y la app Swing usan la misma fachada; el descuento vive en el dominio.
- **Persistencia**: PostgreSQL 16 con Hibernate 6.4.4.
- **Nuevo en el rc**: imagenes Docker de API y cliente, compose con perfil `stack`, `verify-stack.sh`, `verify-module.sh` (propio), CI en GitHub Actions, plan de reversa.

## Cambios desde v0.3-web
Empaquetado en contenedores, CI, documentacion de despliegue y reversa. La logica de dominio y de la API no cambio salvo lo que registre `bitacora-c04.md`.

## Pendientes y limites conocidos
- Sin autenticacion ni HTTPS; solo despliegue local.
- Cobertura segun Sonar: API 73.6 %, cliente 97.2 % (analisis de C03; se repite sobre el commit rc: ver `tratamiento-sonar-C04.md`).
- Issues de mantenibilidad de Sonar: se registra el tratamiento en `tratamiento-sonar-C04.md`.
- `verify-module.sh` oficial de la materia: no disponible; se uso un script propio.
- Reproduccion en otra maquina: PENDIENTE (clon limpio documentado en la bitacora).
