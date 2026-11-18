package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Rol;

/** DAO JDBC de public.rol. Todos los cambios SQL quedan en la capa DAO. */
public class RolDAO extends CrudDAO<Rol> {

    public RolDAO() {
        super(Rol.class, "rol", Arrays.asList("idRol"),
                c("idRol", "id_rol", false, false, false),
                c("nombre", "nombre", true, true, true),
                c("descripcion", "descripcion", true, true, false),
                c("activo", "activo", true, true, false));
    }
}
