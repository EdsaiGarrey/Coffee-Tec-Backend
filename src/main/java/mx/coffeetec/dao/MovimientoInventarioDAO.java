package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.MovimientoInventario;

/** DAO JDBC de public.movimiento_inventario. Todos los cambios SQL quedan en la capa DAO. */
public class MovimientoInventarioDAO extends CrudDAO<MovimientoInventario> {

    public MovimientoInventarioDAO() {
        super(MovimientoInventario.class, "movimiento_inventario", Arrays.asList("idMovimiento"),
                c("idMovimiento", "id_movimiento", false, false, false),
                c("idInsumo", "id_insumo", true, true, true),
                c("idUsuario", "id_usuario", true, true, false),
                c("tipo", "tipo", true, true, true),
                c("cantidad", "cantidad", true, true, true),
                c("motivo", "motivo", true, true, true),
                c("fechaHora", "fecha_hora", false, false, false));
    }
}
