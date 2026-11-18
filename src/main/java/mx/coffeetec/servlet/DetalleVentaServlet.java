package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.DetalleVentaDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetalleVenta;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/detalles-venta y /api/detalles-venta/{id}. Desarrollo local. */
@WebServlet("/api/detalles-venta/*")
public class DetalleVentaServlet extends CrudServlet<DetalleVenta> {
    private final DetalleVentaDAO acceso = new DetalleVentaDAO();
    @Override
    protected CrudDAO<DetalleVenta> dao() { return acceso; }
}
