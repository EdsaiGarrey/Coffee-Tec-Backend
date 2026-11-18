package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Usuario;

/** DAO JDBC de public.usuario. Todos los cambios SQL quedan en la capa DAO. */
public class UsuarioDAO extends CrudDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class, "usuario", Arrays.asList("idUsuario"),
                c("idUsuario", "id_usuario", false, false, false),
                c("idEmpleado", "id_empleado", true, true, false),
                c("idRol", "id_rol", true, true, true),
                c("nombreUsuario", "nombre_usuario", true, true, true),
                c("correo", "correo", true, true, true),
                c("contrasenaHash", "contrasena_hash", true, true, true),
                c("activo", "activo", true, true, false),
                c("debeCambiarClave", "debe_cambiar_clave", true, true, false),
                c("fechaCreacion", "fecha_creacion", false, false, false),
                c("ultimoAcceso", "ultimo_acceso", true, true, false));
    }
}
