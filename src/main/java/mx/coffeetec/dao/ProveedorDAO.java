package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Proveedor;

/** DAO JDBC de public.proveedor. Todos los cambios SQL quedan en la capa DAO. */
public class ProveedorDAO extends CrudDAO<Proveedor> {

    public ProveedorDAO() {
        super(Proveedor.class, "proveedor", Arrays.asList("idProveedor"),
                c("idProveedor", "id_proveedor", false, false, false),
                c("nombre", "nombre", true, true, true),
                c("direccion", "direccion", true, true, false),
                c("telefono", "telefono", true, true, false),
                c("correo", "correo", true, true, false),
                c("activo", "activo", true, true, false));
    }
}
