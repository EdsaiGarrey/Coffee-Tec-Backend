package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetalleInsumoProducto;

/** DAO JDBC de public.detalle_insumo_producto. Todos los cambios SQL quedan en la capa DAO. */
public class DetalleInsumoProductoDAO extends CrudDAO<DetalleInsumoProducto> {

    public DetalleInsumoProductoDAO() {
        super(DetalleInsumoProducto.class, "detalle_insumo_producto", Arrays.asList("idProducto", "idInsumo"),
                c("idProducto", "id_producto", true, false, true),
                c("idInsumo", "id_insumo", true, false, true),
                c("cantidadNecesaria", "cantidad_necesaria", true, true, true));
    }
}
