package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Cliente;

/** DAO JDBC de public.cliente. Todos los cambios SQL quedan en la capa DAO. */
public class ClienteDAO extends CrudDAO<Cliente> {

    public ClienteDAO() {
        super(Cliente.class, "cliente", Arrays.asList("idCliente"),
                c("idCliente", "id_cliente", false, false, false),
                c("nombre", "nombre", true, true, true),
                c("telefono", "telefono", true, true, false),
                c("correo", "correo", true, true, false),
                c("fechaRegistro", "fecha_registro", false, false, false),
                c("activo", "activo", true, true, false));
    }
}
