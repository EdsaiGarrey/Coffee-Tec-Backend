package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.EmpleadoDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Empleado;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/empleados y /api/empleados/{id}. Desarrollo local. */
@WebServlet("/api/empleados/*")
public class EmpleadoServlet extends CrudServlet<Empleado> {
    private final EmpleadoDAO acceso = new EmpleadoDAO();
    @Override
    protected CrudDAO<Empleado> dao() { return acceso; }
}
