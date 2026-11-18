package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.ProveedorDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Proveedor;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/proveedores y /api/proveedores/{id}. Desarrollo local. */
@WebServlet("/api/proveedores/*")
public class ProveedorServlet extends CrudServlet<Proveedor> {
    private final ProveedorDAO acceso = new ProveedorDAO();
    @Override
    protected CrudDAO<Proveedor> dao() { return acceso; }
}
