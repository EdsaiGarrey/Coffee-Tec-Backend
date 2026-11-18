package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Pedido;

/** DAO JDBC de public.pedido. Todos los cambios SQL quedan en la capa DAO. */
public class PedidoDAO extends CrudDAO<Pedido> {

    public PedidoDAO() {
        super(Pedido.class, "pedido", Arrays.asList("idPedido"),
                c("idPedido", "id_pedido", false, false, false),
                c("folio", "folio", true, true, true),
                c("idCliente", "id_cliente", true, true, false),
                c("idEmpleado", "id_empleado", true, true, false),
                c("idUsuario", "id_usuario", true, true, false),
                c("tipoServicio", "tipo_servicio", true, true, false),
                c("estado", "estado", true, true, false),
                c("fechaHora", "fecha_hora", false, false, false),
                c("notas", "notas", true, true, false));
    }
}
