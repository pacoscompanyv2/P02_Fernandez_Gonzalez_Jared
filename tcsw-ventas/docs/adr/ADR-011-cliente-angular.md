# ADR-011 - Cliente Angular para la API de ventas

**Estado:** aceptada (P10) | **Fecha:** 2026-11

## Contexto
La API REST de P09 (`adapter.in.rest`, contrato `openapi.yaml`) ya registra y consulta ventas. P10 pide un cliente web con catalogo, servicios HTTP, formularios y carrito. El cliente corre en otro origen (`ng serve`, puerto 4200) que la API (puerto 8080).

## Decision
- Carpeta `cliente-angular/` en la raiz del repo, junto a `tcsw-ventas/`; el Maven no la conoce.
- Angular 21 con componentes independientes (standalone), signals y carga diferida de rutas.
- **Proxy de desarrollo** (`proxy.conf.json`: `/api` -> `http://127.0.0.1:8080`) en lugar de abrir CORS en la API: la API no cambia y sigue escuchando solo en 127.0.0.1.
- Servicios HTTP (`ProductosService`, `VentasService`) delgados, sin logica de negocio: la API decide existencias, descuento y folios.
- Los modelos TypeScript (`core/models.ts`) son un espejo a mano del `openapi.yaml`.
- Todo error se convierte con `describirError` a una sola forma (`codigo`, `mensaje`, `detalles`), incluido "sin conexion" (status 0 o 502/503/504 del proxy).
- El carrito vive en un servicio con signals (`CarritoService`); el total mostrado es una estimacion, el total final lo calcula la API (incluye el descuento del dominio).
- Pruebas con `HttpTestingController` y `TestBed`, sin depender de Jasmine ni de Vitest.

## Consecuencias
- El proxy solo existe con `ng serve`; para otro despliegue habria que servir el cliente detras de un proxy inverso o habilitar CORS (no se hizo).
- Si cambia `openapi.yaml` hay que actualizar `models.ts` a mano (no hay generacion de codigo).
- La API sigue sin autenticacion (D-API-3); el cliente tampoco la tiene.
