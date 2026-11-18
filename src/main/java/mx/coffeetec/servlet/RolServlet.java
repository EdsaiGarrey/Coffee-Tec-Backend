package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.RolDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Rol;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/roles y /api/roles/{id}. Desarrollo local. */
@WebServlet("/api/roles/*")
public class RolServlet extends CrudServlet<Rol> {
    private final RolDAO acceso = new RolDAO();
    @Override
    protected CrudDAO<Rol> dao() { return acceso; }
}
