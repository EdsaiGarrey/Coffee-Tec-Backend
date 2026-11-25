
-- Coffee Tec
-- Migracion 001: preparar columnas para AES-GCM
-- No cifra ni modifica el contenido de los registros.
-- Aplicar despues de crear las tablas originales.

BEGIN;

ALTER TABLE empleado
    ALTER COLUMN telefono TYPE TEXT;

ALTER TABLE cliente
    ALTER COLUMN telefono TYPE TEXT;

ALTER TABLE proveedor
    ALTER COLUMN direccion TYPE TEXT;

ALTER TABLE proveedor
    ALTER COLUMN telefono TYPE TEXT;

COMMIT;
