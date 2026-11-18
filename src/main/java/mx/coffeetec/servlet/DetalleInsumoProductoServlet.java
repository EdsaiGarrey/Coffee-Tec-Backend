package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.DetalleInsumoProductoDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.DetalleInsumoProducto;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/detalle-insumo-producto y /api/detalle-insumo-producto/{id}. Desarrollo local. */
@WebServlet("/api/detalle-insumo-producto/*")
public class DetalleInsumoProductoServlet extends CrudServlet<DetalleInsumoProducto> {
    private final DetalleInsumoProductoDAO acceso = new DetalleInsumoProductoDAO();
    @Override
    protected CrudDAO<DetalleInsumoProducto> dao() { return acceso; }
}
