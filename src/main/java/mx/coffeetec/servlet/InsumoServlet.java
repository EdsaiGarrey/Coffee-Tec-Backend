package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.InsumoDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Insumo;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/insumos y /api/insumos/{id}. Desarrollo local. */
@WebServlet("/api/insumos/*")
public class InsumoServlet extends CrudServlet<Insumo> {
    private final InsumoDAO acceso = new InsumoDAO();
    @Override
    protected CrudDAO<Insumo> dao() { return acceso; }
}
