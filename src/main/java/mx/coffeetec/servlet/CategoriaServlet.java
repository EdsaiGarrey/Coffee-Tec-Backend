package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.CategoriaDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Categoria;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/categorias y /api/categorias/{id}. Desarrollo local. */
@WebServlet("/api/categorias/*")
public class CategoriaServlet extends CrudServlet<Categoria> {
    private final CategoriaDAO acceso = new CategoriaDAO();
    @Override
    protected CrudDAO<Categoria> dao() { return acceso; }
}
