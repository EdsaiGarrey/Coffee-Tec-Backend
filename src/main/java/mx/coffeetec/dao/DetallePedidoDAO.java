package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetallePedido;

/** DAO JDBC de public.detalle_pedido. Todos los cambios SQL quedan en la capa DAO. */
public class DetallePedidoDAO extends CrudDAO<DetallePedido> {

    public DetallePedidoDAO() {
        super(DetallePedido.class, "detalle_pedido", Arrays.asList("idDetallePedido"),
                c("idDetallePedido", "id_detalle_pedido", false, false, false),
                c("idPedido", "id_pedido", true, true, true),
                c("idProducto", "id_producto", true, true, true),
                c("cantidad", "cantidad", true, true, true),
                c("precioUnitario", "precio_unitario", true, true, true),
                c("subtotal", "subtotal", false, false, false),
                c("notas", "notas", true, true, false));
    }
}
