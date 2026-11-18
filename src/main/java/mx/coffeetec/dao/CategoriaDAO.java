package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Categoria;

/** DAO JDBC de public.categoria. Todos los cambios SQL quedan en la capa DAO. */
public class CategoriaDAO extends CrudDAO<Categoria> {

    public CategoriaDAO() {
        super(Categoria.class, "categoria", Arrays.asList("idCategoria"),
                c("idCategoria", "id_categoria", false, false, false),
                c("nombre", "nombre", true, true, true),
                c("tipo", "tipo", true, true, false),
                c("descripcion", "descripcion", true, true, false),
                c("activo", "activo", true, true, false),
                c("fechaCreacion", "fecha_creacion", false, false, false));
    }
}
