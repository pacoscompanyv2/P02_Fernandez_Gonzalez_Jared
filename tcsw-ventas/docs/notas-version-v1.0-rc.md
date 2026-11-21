# Notas de version: v1.0-rc (candidato de liberacion, C04)

Base: `v0.3-web` (C03). Equipo TCSW-E01. Estado de cada afirmacion segun `indice-evidencias-C04.md`.

## Que incluye
- **API REST** (Spring Boot 3.2.4): `GET /api/v1/productos`, `GET /api/v1/productos/{codigo}`, `POST /api/v1/ventas`, `GET /api/v1/ventas/{folio}`; errores con codigo (`VALIDACION`, `PRODUCTO_NO_ENCONTRADO`, `VENTA_NO_ENCONTRADA`, `EXISTENCIA_INSUFICIENTE`, `FOLIO_DUPLICADO`, ...). Contrato en `openapi.yaml`.
- **Cliente Angular 21**: catalogo, carrito, venta, consulta por folio; estados de carga, vacio y error (incluido `SIN_CONEXION`).
- **Nucleo compartido** (arquitectura hexagonal): la API y la app Swing usan la misma fachada; el descuento vive en el dominio.
- **Persistencia**: PostgreSQL 16 con Hibernate 6.4.4.
- **Nuevo en el rc**: imagenes Docker de API y cliente, compose con perfil `stack`, `verify-stack.sh`, `verify-module.sh` (propio, solo M14), CI en GitHub Actions, plan de reversa.

## Cambios desde v0.3-web
- Empaquetado en contenedores, CI, documentacion de despliegue y reversa.
- Tratamiento de Sonar: hallazgos corregidos en 12 archivos (API y cliente) sin cambio de comportamiento: despues del parche, 174 + 56 pruebas de la API y 51 del cliente con 0 fallas, y `verificar-c03.sh` con 5 pasos y 0 fallas. Detalle en `tratamiento-sonar-C04.md`.
- Se corrigio un `import` sin usar que introdujo el propio tratamiento (S1128).

## Resultados de calidad
- Sonar API (`apicc4`): Quality Gate OK, 0 hallazgos nuevos, 20 abiertos y justificados uno por uno, cobertura 73.6 %, 0.0 % de duplicacion.
- Sonar cliente (`frontcc4`): Quality Gate OK, 0 hallazgos, cobertura 97.2 %, 0.0 % de duplicacion.
- Pruebas y riesgos: `riesgos-y-pruebas-C04.md`.
- Reversa a `v0.3-web`: VERIFICADA (`plan-reversa-v1.0-rc.md`).

## Pendientes y limites conocidos
- Sin autenticacion ni HTTPS; solo despliegue local.
- La interfaz Swing (ventanas) no tiene pruebas automaticas: concentra 176 de las 255 lineas sin cubrir de la API.
- 20 hallazgos de mantenibilidad de Sonar siguen abiertos por decision documentada (tocan el nucleo compartido, la persistencia o firmas de Spring).
- `verify-module.sh` oficial de la materia: no disponible; se uso un script propio que solo cubre M14.
- Dentro de la imagen del cliente se usa `npm install` y no `npm ci` (con `npm ci` la construccion de la imagen fallo por dependencias opcionales ausentes del lock; la causa exacta no se determino). `npm ci` si funciona en el CI de GitHub.
- Reproduccion desde un clon limpio: VERIFICADA sobre `main` (`1850ac1`, `docs/evidencia/clon-limpio-salida.txt`), con la cache de Docker de la maquina del integrante 1 (no es una maquina sin cache).
- Analisis final de Sonar sobre el commit del tag: PENDIENTE hasta ejecutarse.
