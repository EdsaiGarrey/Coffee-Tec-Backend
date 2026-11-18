package mx.coffeetec.servlet;

import jakarta.servlet.annotation.WebServlet;
import mx.coffeetec.dao.UsuarioDAO;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Usuario;
import mx.coffeetec.servlet.base.CrudServlet;

/** Ruta: /api/usuarios y /api/usuarios/{id}. Desarrollo local. */
@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends CrudServlet<Usuario> {
    private final UsuarioDAO acceso = new UsuarioDAO();
    @Override
    protected CrudDAO<Usuario> dao() { return acceso; }
    // No publicar usuario ni hashes hasta tener login, roles y respuestas sin contrasenaHash.
    @Override
    protected boolean habilitado() { return false; }

}
