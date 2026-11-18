package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetallePago;

/** DAO JDBC de public.detalle_pago. Todos los cambios SQL quedan en la capa DAO. */
public class DetallePagoDAO extends CrudDAO<DetallePago> {

    public DetallePagoDAO() {
        super(DetallePago.class, "detalle_pago", Arrays.asList("idDetallePago"),
                c("idDetallePago", "id_detalle_pago", false, false, false),
                c("idPago", "id_pago", true, true, true),
                c("metodo", "metodo", true, true, true),
                c("monto", "monto", true, true, true),
                c("referencia", "referencia", true, true, false));
    }
}
