package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Pago;

/** DAO JDBC de public.pago. Todos los cambios SQL quedan en la capa DAO. */
public class PagoDAO extends CrudDAO<Pago> {

    public PagoDAO() {
        super(Pago.class, "pago", Arrays.asList("idPago"),
                c("idPago", "id_pago", false, false, false),
                c("idVenta", "id_venta", true, true, true),
                c("fechaHora", "fecha_hora", false, false, false),
                c("montoTotal", "monto_total", true, true, false),
                c("estado", "estado", true, true, false),
                c("referencia", "referencia", true, true, false));
    }
}
