package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Insumo;

/** DAO JDBC de public.insumo. Todos los cambios SQL quedan en la capa DAO. */
public class InsumoDAO extends CrudDAO<Insumo> {

    public InsumoDAO() {
        super(Insumo.class, "insumo", Arrays.asList("idInsumo"),
                c("idInsumo", "id_insumo", false, false, false),
                c("idProveedor", "id_proveedor", true, true, false),
                c("nombre", "nombre", true, true, true),
                c("unidadMedida", "unidad_medida", true, true, true),
                c("stockActual", "stock_actual", true, true, false),
                c("stockMinimo", "stock_minimo", true, true, false),
                c("activo", "activo", true, true, false));
    }
}
