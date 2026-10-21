-- schema.sql - P08 Persistencia (PostgreSQL 16)
-- Datos sinteticos de la practica: no hay informacion real de personas ni de negocio.
-- Es idempotente (IF NOT EXISTS): se puede ejecutar mas de una vez.

CREATE TABLE IF NOT EXISTS producto (
    codigo     VARCHAR(20)   PRIMARY KEY,
    nombre     VARCHAR(100)  NOT NULL,
    precio     NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    existencia INTEGER       NOT NULL CHECK (existencia >= 0),
    CONSTRAINT ck_producto_codigo_no_vacio CHECK (length(trim(codigo)) > 0),
    CONSTRAINT ck_producto_nombre_no_vacio CHECK (length(trim(nombre)) > 0)
);

CREATE TABLE IF NOT EXISTS venta (
    folio     VARCHAR(30)   PRIMARY KEY,
    total     NUMERIC(12,2) NOT NULL CHECK (total >= 0),
    cerrada   BOOLEAN       NOT NULL DEFAULT FALSE,
    creada_en TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_venta_folio_no_vacio CHECK (length(trim(folio)) > 0)
);

-- una fila por partida; precio_unitario guarda el precio AL MOMENTO de la venta
CREATE TABLE IF NOT EXISTS detalle_venta (
    folio           VARCHAR(30)   NOT NULL REFERENCES venta (folio) ON DELETE CASCADE,
    linea           INTEGER       NOT NULL CHECK (linea > 0),
    codigo          VARCHAR(20)   NOT NULL REFERENCES producto (codigo),
    cantidad        INTEGER       NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    PRIMARY KEY (folio, linea)
);

CREATE INDEX IF NOT EXISTS ix_detalle_venta_codigo ON detalle_venta (codigo);
