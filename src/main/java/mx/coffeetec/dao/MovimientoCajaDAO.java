package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.MovimientoCaja;

/** DAO JDBC de public.movimiento_caja. Todos los cambios SQL quedan en la capa DAO. */
public class MovimientoCajaDAO extends CrudDAO<MovimientoCaja> {

    public MovimientoCajaDAO() {
        super(MovimientoCaja.class, "movimiento_caja", Arrays.asList("idMovimientoCaja"),
                c("idMovimientoCaja", "id_movimiento_caja", false, false, false),
                c("idCaja", "id_caja", true, true, true),
                c("idUsuario", "id_usuario", true, true, false),
                c("idDetallePago", "id_detalle_pago", true, true, false),
                c("tipo", "tipo", true, true, true),
                c("concepto", "concepto", true, true, true),
                c("monto", "monto", true, true, true),
                c("fechaHora", "fecha_hora", false, false, false));
    }
}
