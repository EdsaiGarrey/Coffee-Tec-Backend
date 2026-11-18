package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.CajaDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Caja;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/cajas y /api/cajas/{id}. Desarrollo local. */
@WebServlet("/api/cajas/*")
public class CajaServlet extends CrudServlet<Caja> {
    private final CajaDAO acceso = new CajaDAO();
    @Override
    protected CrudDAO<Caja> dao() { return acceso; }
}
