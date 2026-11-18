package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.PedidoDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Pedido;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/pedidos y /api/pedidos/{id}. Desarrollo local. */
@WebServlet("/api/pedidos/*")
public class PedidoServlet extends CrudServlet<Pedido> {
    private final PedidoDAO acceso = new PedidoDAO();
    @Override
    protected CrudDAO<Pedido> dao() { return acceso; }
}
