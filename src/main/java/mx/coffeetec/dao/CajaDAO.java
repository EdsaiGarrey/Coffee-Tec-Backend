package mx.coffeetec.dao;

import java.util.Arrays;
import mx.coffeetec.dao.base.CrudDAO;
import mx.coffeetec.pojo.Caja;

/** DAO JDBC de public.caja. Todos los cambios SQL quedan en la capa DAO. */
public class CajaDAO extends CrudDAO<Caja> {

    public CajaDAO() {
        super(Caja.class, "caja", Arrays.asList("idCaja"),
                c("idCaja", "id_caja", false, false, false),
                c("nombre", "nombre", true, true, true),
                c("estado", "estado", true, true, false),
                c("fechaApertura", "fecha_apertura", true, true, false),
                c("fechaCierre", "fecha_cierre", true, true, false),
                c("saldoInicial", "saldo_inicial", true, true, false),
                c("saldoFinal", "saldo_final", true, true, false),
                c("idUsuarioApertura", "id_usuario_apertura", true, true, false),
                c("idUsuarioCierre", "id_usuario_cierre", true, true, false));
    }
}
