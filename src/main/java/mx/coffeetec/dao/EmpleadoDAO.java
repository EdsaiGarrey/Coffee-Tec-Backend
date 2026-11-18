package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Empleado;

/** DAO JDBC de public.empleado. Todos los cambios SQL quedan en la capa DAO. */
public class EmpleadoDAO extends CrudDAO<Empleado> {

    public EmpleadoDAO() {
        super(Empleado.class, "empleado", Arrays.asList("idEmpleado"),
                c("idEmpleado", "id_empleado", false, false, false),
                c("nombre", "nombre", true, true, true),
                c("telefono", "telefono", true, true, false),
                c("puesto", "puesto", true, true, false),
                c("fechaIngreso", "fecha_ingreso", true, true, false),
                c("activo", "activo", true, true, false));
    }
}
