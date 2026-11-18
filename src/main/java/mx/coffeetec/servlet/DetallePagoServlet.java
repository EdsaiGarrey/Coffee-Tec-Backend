package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.DetallePagoDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetallePago;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/detalles-pago y /api/detalles-pago/{id}. Desarrollo local. */
@WebServlet("/api/detalles-pago/*")
public class DetallePagoServlet extends CrudServlet<DetallePago> {
    private final DetallePagoDAO acceso = new DetallePagoDAO();
    @Override
    protected CrudDAO<DetallePago> dao() { return acceso; }
}
