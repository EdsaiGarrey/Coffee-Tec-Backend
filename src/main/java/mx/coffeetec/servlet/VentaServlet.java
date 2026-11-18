package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.VentaDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Venta;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/ventas y /api/ventas/{id}. Desarrollo local. */
@WebServlet("/api/ventas/*")
public class VentaServlet extends CrudServlet<Venta> {
    private final VentaDAO acceso = new VentaDAO();
    @Override
    protected CrudDAO<Venta> dao() { return acceso; }
}
