# Indice de evidencias - C03 Integracion web (equipo TCSW-E01)

Estados: VERIFICADO = hay evidencia; NO_VERIFICADO = no hay evidencia; PENDIENTE = falta hacerlo. Documento completo con capturas: `C03_EQUIPO_01.docx` (Eminus). Producto: entrega `v0.3-web` (API + cliente Angular + nucleo reutilizado).

## Mapa criterio R04 -> evidencia

| Criterio (pts) | Evidencia | Estado |
|---|---|---|
| API (1.5) | Contrato y endpoints de P09 (`GET /api/v1/productos`, `GET /api/v1/productos/{codigo}`, `POST /api/v1/ventas`, `GET /api/v1/ventas/{folio}`); `verificar-p09.sh --solo-memoria`: 14 comprobaciones, 0 fallas; `mvn clean verify -Pintegracion`: 56 pruebas de integracion, 0 fallos | VERIFICADO |
| Angular (1.5) | Cliente con Catalogo, Carrito, Venta y Consulta (navegacion por menu y rutas); `ng build` sin errores; `ng test`: 9 archivos, 49 pruebas, 0 fallos; `verificar-p10.sh`: 9 comprobaciones, 0 fallas a traves del proxy; recorrido en el navegador del integrante 1 (capturas en el Word) | VERIFICADO. Recorrido en navegador de los integrantes 2 y 3: PENDIENTE |
| Reuso del nucleo (1) | `docs/reuso-nucleo-C03.md`: API y Swing entran por `VentasFacade`; el descuento por mayoreo y el redondeo viven solo en el dominio; el cliente solo estima el total; 4 reglas de `ArquitecturaRestTest` | VERIFICADO (codigo leido y comportamiento visto en el navegador). Desglose por clase de las reglas ArchUnit: NO_VERIFICADO (no se guardo; corren dentro de `mvn verify`, que termino sin fallos) |
| Errores y validacion (0.5) | Exito, vacio y error vistos en el navegador: venta registrada, carrito vacio, busqueda sin resultados, `FOLIO_VACIO` en consulta, `VENTA_NO_ENCONTRADA` (folio 67), boton "Agregar" deshabilitado sin existencia; 409 `EXISTENCIA_INSUFICIENTE` y `FOLIO_DUPLICADO` en `verificar-p09.sh` | VERIFICADO. "Sin conexion" en navegador: PENDIENTE (hallazgo abajo). Validacion del folio de solo espacios en el formulario de venta: PENDIENTE |
| Calidad (0.5) | 174 pruebas unitarias y 56 de integracion sin fallos; trazabilidad en la seccion siguiente; README con la reproduccion | VERIFICADO en lo propio; trazabilidad de los integrantes 2 y 3: PENDIENTE |

## Verificacion integral

Script: `tcsw-ventas/scripts/verificar-c03.sh` (5 pasos). Salida guardada en `docs/evidencia/verificar-c03-salida.txt`. Corrida del 14-nov-2026 sobre el commit `08497b8` con el script aun sin versionar (el script se agrego despues, en `0a7390c`):

| Paso | Resultado |
|---|---|
| 1 PostgreSQL (`docker compose up --wait`) | OK |
| 2 `mvn clean verify -Pintegracion` | unitarias 174 (0 fallos, 0 errores); integracion 56 (0 fallos, 0 errores) |
| 3 `verificar-p09.sh --solo-memoria` | 14 comprobaciones, 0 fallas |
| 4 `npm install`, `ng build`, `ng test` | 9 archivos, 49 pruebas, 0 fallos |
| 5 `verificar-p10.sh` | 9 comprobaciones, 0 fallas |
| Resultado | 5 pasos, 0 fallas |

## Trazabilidad por integrante

| Integrante | Aporte en C03 | Issue / PR | Estado |
|---|---|---|---|
| 1 Jared | script `verificar-c03.sh` (commit `0a7390c`, directo a `main`), documentacion de cierre, capturas, base del cliente y API (P09 y P10, ver sus archivos de contribucion) | issue #52; el PR que lo cierra enlaza `Closes #52` | script: VERIFICADO; documentacion: en revision |
| 2 Suriel | correccion "API apagada = sin conexion" en `api-error.ts` y su prueba; corrida propia del script | por abrir | PENDIENTE |
| 3 Jesus | revision del PR de Suriel; prueba propia de validacion del folio con espacios; corrida propia del script | por abrir | PENDIENTE |

## Hallazgo abierto: "API apagada" se ve como `ERROR_HTTP`

Con la API apagada, el proxy de `ng serve` responde 500 y el cliente muestra `ERROR_HTTP: La API respondio 500` en lugar del mensaje de "sin conexion" (visto en el navegador el 14-nov). Correccion propuesta: tratar como sin conexion un 500 que no trae el formato de error de la API (`codigo`). El parche existe y con el `ng test` dio 51 pruebas en verde en la maquina del integrante 1, pero **no esta integrado a `main`**: lo aplica el integrante 2 con su issue y PR. Hasta entonces: NO_VERIFICADO en `main`.

## Limitaciones
- El script `verificar-c03.sh` y su salida se subieron directo a `main`, sin issue ni PR (`0a7390c`); lo dice su mensaje
- La corrida guardada del script es del 14-nov sobre `08497b8`; no se repitio sobre el commit final
- El total del carrito del cliente es una estimacion sin descuentos; el total final lo calcula la API (ver `reuso-nucleo-C03.md`)
- No se volvio a ejecutar Sonar en C03: NO_VERIFICADO para este corte (el ultimo resultado del cliente, de P10, fue Quality Gate aprobado con 97.1 % de cobertura)
- Sin pruebas de extremo a extremo con navegador real; el proxy solo existe con `ng serve`
- Etiqueta `v0.3-web`: PENDIENTE
