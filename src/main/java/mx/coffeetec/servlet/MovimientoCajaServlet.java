package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.MovimientoCajaDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.MovimientoCaja;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/movimientos-caja y /api/movimientos-caja/{id}. Desarrollo local. */
@WebServlet("/api/movimientos-caja/*")
public class MovimientoCajaServlet extends CrudServlet<MovimientoCaja> {
    private final MovimientoCajaDAO acceso = new MovimientoCajaDAO();
    @Override
    protected CrudDAO<MovimientoCaja> dao() { return acceso; }
}
