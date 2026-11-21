# P02_Fernandez_Gonzalez_Jared

Sistema de ventas de la experiencia educativa Tecnologias para la Construccion de Software (equipo TCSW-E01): nucleo hexagonal en Java, API REST (Spring Boot), cliente Angular, persistencia en PostgreSQL y cliente de escritorio Swing.

- `tcsw-ventas/` - nucleo, API REST, adaptadores (memoria, JDBC, Hibernate), Swing, scripts, documentos y evidencia.
- `cliente-angular/` - cliente web.
- `README-reproduccion.md` - como reproducir cada corte (C01 a C04).

## Version vigente: `v1.0-rc` (C04)

```bash
cd tcsw-ventas
cp .env.example .env        # misma clave en POSTGRES_PASSWORD y TCSW_DB_PASSWORD
./scripts/verify-module.sh M14
```

Detalle, estados de verificacion y limites: seccion "C04" de `README-reproduccion.md` y `tcsw-ventas/docs/indice-evidencias-C04.md`. Notas de version: `tcsw-ventas/docs/notas-version-v1.0-rc.md`.

Etiquetas: `v0.1-arquitectura`, `v0.2-swing`, `v0.3-persistencia`, `v0.3-web`, `v1.0-rc`.
