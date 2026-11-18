package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Venta;

/** DAO JDBC de public.venta. Todos los cambios SQL quedan en la capa DAO. */
public class VentaDAO extends CrudDAO<Venta> {

    public VentaDAO() {
        super(Venta.class, "venta", Arrays.asList("idVenta"),
                c("idVenta", "id_venta", false, false, false),
                c("idPedido", "id_pedido", true, true, true),
                c("idUsuario", "id_usuario", true, true, false),
                c("fechaHora", "fecha_hora", false, false, false),
                c("estado", "estado", true, true, false),
                c("descuento", "descuento", true, true, false),
                c("impuesto", "impuesto", true, true, false),
                c("total", "total", true, true, false));
    }
}
