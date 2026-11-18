package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.ClienteDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Cliente;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/clientes y /api/clientes/{id}. Desarrollo local. */
@WebServlet("/api/clientes/*")
public class ClienteServlet extends CrudServlet<Cliente> {
    private final ClienteDAO acceso = new ClienteDAO();
    @Override
    protected CrudDAO<Cliente> dao() { return acceso; }
}
