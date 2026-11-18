# Indice de evidencias - C03 Integracion web (equipo TCSW-E01)

Estados: VERIFICADO = hay evidencia; NO_VERIFICADO = no hay evidencia; PENDIENTE = falta hacerlo. Documento completo con capturas: `C03_EQUIPO_01.docx` (Eminus). Producto: entrega `v0.3-web` (API + cliente Angular + nucleo reutilizado).

## Mapa criterio R04 -> evidencia

| Criterio (pts) | Evidencia | Estado |
|---|---|---|
| API (1.5) | Contrato y endpoints de P09 (`GET /api/v1/productos`, `GET /api/v1/productos/{codigo}`, `POST /api/v1/ventas`, `GET /api/v1/ventas/{folio}`); `verificar-p09.sh` completo: 21 comprobaciones, 0 fallas; `mvn clean verify -Pintegracion`: 174 pruebas unitarias y 56 de integracion, 0 fallos | VERIFICADO |
| Angular (1.5) | Cliente con Catalogo, Carrito, Venta y Consulta (menu y rutas); `ng build` sin errores; `ng test`: 9 archivos, 51 pruebas, 0 fallos; `verificar-p10.sh`: 9 comprobaciones, 0 fallas por el proxy; recorrido en el navegador (capturas en el Word) | VERIFICADO |
| Reuso del nucleo (1) | `docs/reuso-nucleo-C03.md`: API y Swing entran por `VentasFacade`; el descuento por mayoreo y el redondeo viven solo en el dominio; el cliente solo estima el total (599.00 estimado contra 569.00 final de la API); 4 reglas de `ArquitecturaRestTest` | VERIFICADO (codigo leido y comportamiento visto en el navegador). Resultado individual de cada regla ArchUnit: NO_VERIFICADO (corren dentro de `mvn verify`, que termino sin fallos) |
| Errores y validacion (0.5) | Exito, vacio y error en el navegador: venta registrada, carrito vacio, busqueda sin resultados, `FOLIO_VACIO`, `VENTA_NO_ENCONTRADA` (folio 67), "Agregar" deshabilitado sin existencia, `SIN_CONEXION` con la API apagada; 409 `EXISTENCIA_INSUFICIENTE` y `FOLIO_DUPLICADO` en `verificar-p09.sh` | VERIFICADO. Folio de solo espacios en el formulario de venta: NO_VERIFICADO (no se probo) |
| Calidad (0.5) | 174 + 56 + 51 pruebas sin fallos; bitacora, indice y reproduccion en README; trazabilidad en la seccion siguiente | VERIFICADO en lo propio; ver limitaciones de trazabilidad |

## Evidencia de persistencia

`verificar-p09.sh` (fase B) usa PostgreSQL con Hibernate sobre la base de pruebas: registra una venta, reinicia la API y vuelve a consultarla. Resultado de la corrida del 18-nov-2026 (`docs/evidencia/verificar-c03-p09-salida.txt`): `POST /api/v1/ventas` 201 CERRADA; existencia de P1 en 18; tras reiniciar la API, `GET` de la venta 200 CERRADA y existencia aun en 18; 409 por folio repetido; 409 por existencia insuficiente sin cambios; consulta SQL con `cerrada = t`. Total: 21 comprobaciones, 0 fallas. Estado: VERIFICADO.

## Verificacion integral

Script: `tcsw-ventas/scripts/verificar-c03.sh` (5 pasos). Corrida final del 18-nov-2026 a las 07:31 sobre el commit `9170d28` (el script modificado se versiono despues, en `bea54f4`):

| Paso | Resultado |
|---|---|
| 1 PostgreSQL (`docker compose up --wait`) | OK |
| 2 `mvn clean verify -Pintegracion` | unitarias 174 (0 fallos, 0 errores); integracion 56 (0 fallos, 0 errores) |
| 3 `verificar-p09.sh` (memoria y PostgreSQL con reinicio) | 21 comprobaciones, 0 fallas |
| 4 `npm install`, `ng build`, `ng test` | 9 archivos, 51 pruebas, 0 fallos |
| 5 `verificar-p10.sh` | 9 comprobaciones, 0 fallas |
| Resultado | 5 pasos, 0 fallas |

Salidas guardadas: `docs/evidencia/verificar-c03-salida.txt`, `verificar-c03-p09-salida.txt` y `verificar-c03-p10-salida.txt`. Una corrida anterior (14-nov, sobre `08497b8`) uso `--solo-memoria` y no ejecuto la fase de persistencia; la del 18-nov la reemplaza.

## Trazabilidad (declaracion de autoria)

| Elemento | Referencia | Estado |
|---|---|---|
| Script `verificar-c03.sh` y su salida | `0a7390c` (14-nov) y `bea54f4` (18-nov), directo a `main`, sin issue ni PR | VERIFICADO, sin revision |
| Correccion "API apagada = sin conexion" (`api-error.ts` y su prueba) | `9170d28`, directo a `main`, sin issue ni PR; 51 pruebas en verde; `SIN_CONEXION` visto en el navegador el 18-nov | VERIFICADO, sin revision |
| Documentacion de cierre | issue #52, PR #53 | VERIFICADO; el PR #53 se fusiono el 18-nov sin comentario de revision de otro integrante |

Los integrantes 2 (Suriel) y 3 (Jesus) no registraron aportes en C03: el trabajo de este corte lo hizo el integrante 1. Su participacion previa esta en los archivos de contribucion de P09 y P10. No se generaron archivos `integrante-2-C03.md` ni `integrante-3-C03.md`.

## Hallazgo cerrado: "API apagada" se veia como `ERROR_HTTP`

El 14-nov, con la API apagada, el cliente mostraba `ERROR_HTTP: La API respondio 500` (el proxy de `ng serve` responde 500). Se corrigio en `9170d28`: un 500 sin el formato de error de la API (`codigo`) se trata como `SIN_CONEXION`; un 500 real de la API conserva su codigo. El 18-nov se vio en el navegador `SIN_CONEXION: No se pudo conectar con la API. Revisa que este en marcha en el puerto 8080.` Estado: VERIFICADO.

## Limitaciones
- Script, correccion y parte de la evidencia se subieron directo a `main`, sin issue ni PR; los mensajes de commit lo dicen
- El PR #53 se fusiono sin revision de otro integrante
- La corrida final es sobre `9170d28` con el script modificado en el directorio de trabajo; el commit de cierre que lleva el tag solo agrega documentacion
- `./scripts/verify-module.sh M13`, citado en la guia, no existe en este repositorio: NO_VERIFICADO (no disponible); `verificar-c03.sh` es el equivalente propio
- No se ejecuto Sonar en C03: NO_VERIFICADO (el ultimo resultado del cliente, de P10, fue Quality Gate aprobado con 97.1 % de cobertura)
- Sin pruebas de extremo a extremo con navegador real automatizadas; el proxy solo existe con `ng serve`
- El total del carrito del cliente es una estimacion sin descuentos; el total final lo calcula la API
- Etiqueta `v0.3-web` y entrega en Eminus: PENDIENTE hasta hacerlas
