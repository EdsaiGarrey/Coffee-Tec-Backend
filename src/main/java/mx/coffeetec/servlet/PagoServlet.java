package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.PagoDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Pago;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/pagos y /api/pagos/{id}. Desarrollo local. */
@WebServlet("/api/pagos/*")
public class PagoServlet extends CrudServlet<Pago> {
    private final PagoDAO acceso = new PagoDAO();
    @Override
    protected CrudDAO<Pago> dao() { return acceso; }
}
