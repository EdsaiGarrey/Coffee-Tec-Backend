package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.MovimientoInventarioDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.MovimientoInventario;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/movimientos-inventario y /api/movimientos-inventario/{id}. Desarrollo local. */
@WebServlet("/api/movimientos-inventario/*")
public class MovimientoInventarioServlet extends CrudServlet<MovimientoInventario> {
    private final MovimientoInventarioDAO acceso = new MovimientoInventarioDAO();
    @Override
    protected CrudDAO<MovimientoInventario> dao() { return acceso; }
}
