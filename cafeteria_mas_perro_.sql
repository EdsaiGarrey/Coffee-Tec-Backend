
-- ============================================================
-- ENTIDADES FUERTES 
-- ============================================================

CREATE TABLE rol (
    id_rol          SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(25) NOT NULL UNIQUE,
    descripcion     VARCHAR(200),
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_rol_nombre
        CHECK (nombre IN ('administrador', 'gerente', 'empleado'))
);

CREATE TABLE empleado (
    id_empleado     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    telefono        VARCHAR(15),
    puesto          VARCHAR(80) NOT NULL DEFAULT 'Empleado',
    fecha_ingreso   DATE NOT NULL DEFAULT CURRENT_DATE,
    activo          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario (
    id_usuario         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_empleado        BIGINT UNIQUE REFERENCES empleado(id_empleado)
                       ON UPDATE CASCADE ON DELETE RESTRICT,
    id_rol             SMALLINT NOT NULL REFERENCES rol(id_rol)
                       ON UPDATE CASCADE ON DELETE RESTRICT,
    nombre_usuario     VARCHAR(50) NOT NULL UNIQUE,
    correo             VARCHAR(50) NOT NULL UNIQUE,
    contrasena_hash    TEXT NOT NULL,
    activo             BOOLEAN NOT NULL DEFAULT TRUE,
    debe_cambiar_clave BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso      TIMESTAMPTZ,
    CONSTRAINT ck_usuario_correo
        CHECK (POSITION('@' IN correo) > 1),
    CONSTRAINT ck_usuario_hash_no_vacio
        CHECK (LENGTH(TRIM(contrasena_hash)) > 0)
);

CREATE TABLE cliente (
    id_cliente      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    telefono        VARCHAR(25),
    correo          VARCHAR(254),
    fecha_registro  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_cliente_correo
        CHECK (correo IS NULL OR POSITION('@' IN correo) > 1)
);

CREATE TABLE proveedor (
    id_proveedor    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    direccion       VARCHAR(250),
    telefono        VARCHAR(25),
    correo          VARCHAR(254),
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_proveedor_correo
        CHECK (correo IS NULL OR POSITION('@' IN correo) > 1)
);

CREATE TABLE categoria (
    id_categoria   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre         VARCHAR(80) NOT NULL UNIQUE,
    tipo           VARCHAR(60) NOT NULL DEFAULT 'General',
    descripcion    TEXT,
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE producto (
    id_producto    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_categoria   BIGINT NOT NULL REFERENCES categoria(id_categoria)
                   ON UPDATE CASCADE ON DELETE RESTRICT,
    nombre         VARCHAR(120) NOT NULL,
    descripcion    TEXT,
    precio         NUMERIC(12,2) NOT NULL,
    url_imagen     TEXT,
    stock          INTEGER NOT NULL DEFAULT 0,
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_producto_precio CHECK (precio >= 0),
    CONSTRAINT ck_producto_stock CHECK (stock >= 0),
    CONSTRAINT uq_producto_nombre_categoria UNIQUE (id_categoria, nombre)
);

CREATE TABLE insumo (
    id_insumo       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_proveedor    BIGINT REFERENCES proveedor(id_proveedor)
                    ON UPDATE CASCADE ON DELETE SET NULL,
    nombre          VARCHAR(120) NOT NULL UNIQUE,
    unidad_medida   VARCHAR(30) NOT NULL,
    stock_actual    NUMERIC(14,3) NOT NULL DEFAULT 0,
    stock_minimo    NUMERIC(14,3) NOT NULL DEFAULT 0,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_insumo_stock CHECK (stock_actual >= 0),
    CONSTRAINT ck_insumo_minimo CHECK (stock_minimo >= 0)
);

CREATE TABLE pedido (
    id_pedido       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    folio           VARCHAR(30) NOT NULL UNIQUE,
    id_cliente      BIGINT REFERENCES cliente(id_cliente)
                    ON UPDATE CASCADE ON DELETE SET NULL,
    id_empleado     BIGINT REFERENCES empleado(id_empleado)
                    ON UPDATE CASCADE ON DELETE SET NULL,
    id_usuario      BIGINT REFERENCES usuario(id_usuario)
                    ON UPDATE CASCADE ON DELETE SET NULL,
    tipo_servicio   VARCHAR(20) NOT NULL DEFAULT 'para_llevar',
    estado          VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    fecha_hora      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notas           TEXT,
    CONSTRAINT ck_pedido_servicio
        CHECK (tipo_servicio IN ('en_mesa', 'para_llevar', 'entrega')),
    CONSTRAINT ck_pedido_estado
        CHECK (estado IN ('pendiente', 'en_preparacion', 'listo', 'entregado', 'cancelado'))
);

CREATE TABLE venta (
    id_venta       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pedido      BIGINT NOT NULL UNIQUE REFERENCES pedido(id_pedido)
                   ON UPDATE CASCADE ON DELETE RESTRICT,
    id_usuario     BIGINT REFERENCES usuario(id_usuario)
                   ON UPDATE CASCADE ON DELETE SET NULL,
    fecha_hora     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado         VARCHAR(15) NOT NULL DEFAULT 'pendiente',
    descuento      NUMERIC(12,2) NOT NULL DEFAULT 0,
    impuesto       NUMERIC(12,2) NOT NULL DEFAULT 0,
    total          NUMERIC(14,2) NOT NULL DEFAULT 0,
    CONSTRAINT ck_venta_estado CHECK (estado IN ('pendiente', 'pagada', 'cancelada', 'reembolsada')),
    CONSTRAINT ck_venta_descuento CHECK (descuento >= 0),
    CONSTRAINT ck_venta_impuesto CHECK (impuesto >= 0),
    CONSTRAINT ck_venta_total CHECK (total >= 0)
);

CREATE TABLE pago (
    id_pago       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta      BIGINT NOT NULL UNIQUE REFERENCES venta(id_venta)
                  ON UPDATE CASCADE ON DELETE RESTRICT,
    fecha_hora    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    monto_total   NUMERIC(14,2) NOT NULL DEFAULT 0,
    estado        VARCHAR(15) NOT NULL DEFAULT 'pendiente',
    referencia    VARCHAR(120),
    CONSTRAINT ck_pago_monto CHECK (monto_total >= 0),
    CONSTRAINT ck_pago_estado CHECK (estado IN ('pendiente', 'completado', 'fallido', 'reembolsada'))
);

CREATE TABLE caja (
    id_caja          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre           VARCHAR(80) NOT NULL UNIQUE,
    estado           VARCHAR(12) NOT NULL DEFAULT 'cerrada',
    fecha_apertura   TIMESTAMPTZ,
    fecha_cierre     TIMESTAMPTZ,
    saldo_inicial    NUMERIC(14,2) NOT NULL DEFAULT 0,
    saldo_final      NUMERIC(14,2),
    id_usuario_apertura BIGINT REFERENCES usuario(id_usuario)
                       ON UPDATE CASCADE ON DELETE SET NULL,
    id_usuario_cierre BIGINT REFERENCES usuario(id_usuario)
                      ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT ck_caja_estado CHECK (estado IN ('abierta', 'cerrada')),
    CONSTRAINT ck_caja_saldo_inicial CHECK (saldo_inicial >= 0),
    CONSTRAINT ck_caja_saldo_final CHECK (saldo_final IS NULL OR saldo_final >= 0),
    CONSTRAINT ck_caja_fechas CHECK (
        fecha_cierre IS NULL OR fecha_apertura IS NULL OR fecha_cierre >= fecha_apertura
    )
);


-- ============================================================
-- ENTIDADES DÉBILES COMO EL AMOR DE ELLA JAJAJ 
-- ============================================================

CREATE TABLE detalle_insumo_producto (
    id_producto        BIGINT NOT NULL REFERENCES producto(id_producto)
                       ON UPDATE CASCADE ON DELETE CASCADE,
    id_insumo          BIGINT NOT NULL REFERENCES insumo(id_insumo)
                       ON UPDATE CASCADE ON DELETE RESTRICT,
    cantidad_necesaria NUMERIC(14,3) NOT NULL,
    PRIMARY KEY (id_producto, id_insumo),
    CONSTRAINT ck_receta_cantidad CHECK (cantidad_necesaria > 0)
);

CREATE TABLE movimiento_inventario (
    id_movimiento    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_insumo        BIGINT NOT NULL REFERENCES insumo(id_insumo)
                     ON UPDATE CASCADE ON DELETE RESTRICT,
    id_usuario       BIGINT REFERENCES usuario(id_usuario)
                     ON UPDATE CASCADE ON DELETE SET NULL,
    tipo             VARCHAR(10) NOT NULL,
    cantidad         NUMERIC(14,3) NOT NULL,
    motivo           VARCHAR(250) NOT NULL,
    fecha_hora       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_movimiento_tipo CHECK (tipo IN ('entrada', 'salida', 'ajuste')),
    CONSTRAINT ck_movimiento_cantidad CHECK (cantidad > 0)
);

CREATE TABLE detalle_pedido (
    id_detalle_pedido BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pedido         BIGINT NOT NULL REFERENCES pedido(id_pedido)
                      ON UPDATE CASCADE ON DELETE CASCADE,
    id_producto       BIGINT NOT NULL REFERENCES producto(id_producto)
                      ON UPDATE CASCADE ON DELETE RESTRICT,
    cantidad          INTEGER NOT NULL,
    precio_unitario   NUMERIC(12,2) NOT NULL,
    subtotal          NUMERIC(14,2) GENERATED ALWAYS AS
                      (cantidad * precio_unitario) STORED,
    notas             VARCHAR(250),
    CONSTRAINT ck_detalle_pedido_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_pedido_precio CHECK (precio_unitario >= 0)
);

CREATE TABLE detalle_venta (
    id_detalle_venta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta         BIGINT NOT NULL REFERENCES venta(id_venta)
                     ON UPDATE CASCADE ON DELETE CASCADE,
    id_detalle_pedido BIGINT NOT NULL UNIQUE REFERENCES detalle_pedido(id_detalle_pedido)
                      ON UPDATE CASCADE ON DELETE RESTRICT,
    nombre_producto  VARCHAR(120) NOT NULL,
    cantidad         INTEGER NOT NULL,
    precio_unitario  NUMERIC(12,2) NOT NULL,
    subtotal         NUMERIC(14,2) GENERATED ALWAYS AS
                     (cantidad * precio_unitario) STORED,
    CONSTRAINT ck_detalle_venta_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_venta_precio CHECK (precio_unitario >= 0)
);

CREATE TABLE detalle_pago (
    id_detalle_pago BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pago         BIGINT NOT NULL REFERENCES pago(id_pago)
                    ON UPDATE CASCADE ON DELETE CASCADE,
    metodo          VARCHAR(20) NOT NULL,
    monto           NUMERIC(14,2) NOT NULL,
    referencia      VARCHAR(120),
    CONSTRAINT ck_detalle_pago_metodo
        CHECK (metodo IN ('efectivo', 'tarjeta', 'transferencia', 'otro')),
    CONSTRAINT ck_detalle_pago_monto CHECK (monto > 0)
);

CREATE TABLE movimiento_caja (
    id_movimiento_caja BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_caja            BIGINT NOT NULL REFERENCES caja(id_caja)
                       ON UPDATE CASCADE ON DELETE RESTRICT,
    id_usuario         BIGINT REFERENCES usuario(id_usuario)
                       ON UPDATE CASCADE ON DELETE SET NULL,
    id_detalle_pago    BIGINT REFERENCES detalle_pago(id_detalle_pago)
                       ON UPDATE CASCADE ON DELETE RESTRICT,
    tipo               VARCHAR(12) NOT NULL,
    concepto           VARCHAR(250) NOT NULL,
    monto              NUMERIC(14,2) NOT NULL,
    fecha_hora         TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_movimiento_caja_tipo CHECK (tipo IN ('ingreso', 'egreso')),
    CONSTRAINT ck_movimiento_caja_monto CHECK (monto > 0)
);

-- ============================================================
-- TABLAS DE RESPALDO / AUDITORÍA
-- Se conserva la fila anterior en formato JSONB.
-- Cada respaldo tiene su propio ID, por lo que se pueden guardar
-- múltiples cambios del mismo registro sin conflictos de PK.
-- ============================================================

CREATE TABLE usuario_backup (
    id_respaldo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('UPDATE', 'DELETE')),
    fecha_respaldo TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos JSONB NOT NULL
);

CREATE TABLE producto_backup (
    id_respaldo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('UPDATE', 'DELETE')),
    fecha_respaldo TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos JSONB NOT NULL
);

CREATE TABLE cliente_backup (
    id_respaldo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('UPDATE', 'DELETE')),
    fecha_respaldo TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos JSONB NOT NULL
);

CREATE TABLE proveedor_backup (
    id_respaldo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('UPDATE', 'DELETE')),
    fecha_respaldo TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos JSONB NOT NULL
);

CREATE TABLE pedido_backup (
    id_respaldo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('UPDATE', 'DELETE')),
    fecha_respaldo TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos JSONB NOT NULL
);

CREATE TABLE venta_backup (
    id_respaldo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('UPDATE', 'DELETE')),
    fecha_respaldo TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos JSONB NOT NULL
);

CREATE TABLE pago_backup (
    id_respaldo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('UPDATE', 'DELETE')),
    fecha_respaldo TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datos JSONB NOT NULL
);


-- ============================================================
-- FUNCIÓN GENERAL DE RESPALDO
-- AFTER UPDATE: guarda OLD (los datos antes de modificarlos).
-- AFTER DELETE: guarda OLD (los datos antes de eliminarlos).
-- TG_TABLE_NAME permite reutilizar la función en varias tablas.
-- ============================================================

CREATE OR REPLACE FUNCTION fn_respaldo_fila()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_datos JSONB;
    v_operacion VARCHAR(10);
BEGIN
    v_datos := to_jsonb(OLD);
    v_operacion := TG_OP;

    CASE TG_TABLE_NAME
        WHEN 'usuario' THEN
            INSERT INTO usuario_backup (operacion, datos)
            VALUES (v_operacion, v_datos);

        WHEN 'producto' THEN
            INSERT INTO producto_backup (operacion, datos)
            VALUES (v_operacion, v_datos);

        WHEN 'cliente' THEN
            INSERT INTO cliente_backup (operacion, datos)
            VALUES (v_operacion, v_datos);

        WHEN 'proveedor' THEN
            INSERT INTO proveedor_backup (operacion, datos)
            VALUES (v_operacion, v_datos);

        WHEN 'pedido' THEN
            INSERT INTO pedido_backup (operacion, datos)
            VALUES (v_operacion, v_datos);

        WHEN 'venta' THEN
            INSERT INTO venta_backup (operacion, datos)
            VALUES (v_operacion, v_datos);

        WHEN 'pago' THEN
            INSERT INTO pago_backup (operacion, datos)
            VALUES (v_operacion, v_datos);

        ELSE
            RAISE EXCEPTION 'No existe tabla de respaldo para %', TG_TABLE_NAME;
    END CASE;

    -- En un trigger AFTER el valor retornado se ignora, pero OLD es
    -- una devolución válida para esta función.
    RETURN OLD;
END;
$$;


-- ============================================================
-- TRIGGERS: RESPALDAN TANTO UPDATE COMO DELETE
-- ============================================================

CREATE TRIGGER trg_backup_usuario
AFTER UPDATE OR DELETE ON usuario
FOR EACH ROW
EXECUTE FUNCTION fn_respaldo_fila();

CREATE TRIGGER trg_backup_producto
AFTER UPDATE OR DELETE ON producto
FOR EACH ROW
EXECUTE FUNCTION fn_respaldo_fila();

CREATE TRIGGER trg_backup_cliente
AFTER UPDATE OR DELETE ON cliente
FOR EACH ROW
EXECUTE FUNCTION fn_respaldo_fila();

CREATE TRIGGER trg_backup_proveedor
AFTER UPDATE OR DELETE ON proveedor
FOR EACH ROW
EXECUTE FUNCTION fn_respaldo_fila();

CREATE TRIGGER trg_backup_pedido
AFTER UPDATE OR DELETE ON pedido
FOR EACH ROW
EXECUTE FUNCTION fn_respaldo_fila();

CREATE TRIGGER trg_backup_venta
AFTER UPDATE OR DELETE ON venta
FOR EACH ROW
EXECUTE FUNCTION fn_respaldo_fila();

CREATE TRIGGER trg_backup_pago
AFTER UPDATE OR DELETE ON pago
FOR EACH ROW
EXECUTE FUNCTION fn_respaldo_fila();



