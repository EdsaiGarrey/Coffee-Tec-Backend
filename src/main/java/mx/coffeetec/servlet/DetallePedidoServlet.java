package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.DetallePedidoDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetallePedido;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/detalles-pedido y /api/detalles-pedido/{id}. Desarrollo local. */
@WebServlet("/api/detalles-pedido/*")
public class DetallePedidoServlet extends CrudServlet<DetallePedido> {
    private final DetallePedidoDAO acceso = new DetallePedidoDAO();
    @Override
    protected CrudDAO<DetallePedido> dao() { return acceso; }
}
