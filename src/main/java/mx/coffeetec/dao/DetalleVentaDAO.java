package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetalleVenta;

/** DAO JDBC de public.detalle_venta. Todos los cambios SQL quedan en la capa DAO. */
public class DetalleVentaDAO extends CrudDAO<DetalleVenta> {

    public DetalleVentaDAO() {
        super(DetalleVenta.class, "detalle_venta", Arrays.asList("idDetalleVenta"),
                c("idDetalleVenta", "id_detalle_venta", false, false, false),
                c("idVenta", "id_venta", true, true, true),
                c("idDetallePedido", "id_detalle_pedido", true, true, true),
                c("nombreProducto", "nombre_producto", true, true, true),
                c("cantidad", "cantidad", true, true, true),
                c("precioUnitario", "precio_unitario", true, true, true),
                c("subtotal", "subtotal", false, false, false));
    }
}
