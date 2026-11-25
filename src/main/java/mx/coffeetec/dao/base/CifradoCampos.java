
package mx.coffeetec.dao.base;

import java.sql.SQLException;
import seguridadd.CifradoAESGCM;

public final class CifradoCampos {

    private CifradoCampos() {
    }

    // Determinar qué campos deben cifrarse
    private static boolean esSensible(
            String tabla, String campo) {

        switch (tabla) {

            case "cliente":
            case "empleado":
                return "telefono".equals(campo);

            case "proveedor":
                return "telefono".equals(campo)
                        || "direccion".equals(campo);

            default:
                return false;
        }
    }

    // Cifrar antes de guardar en PostgreSQL
    public static Object cifrarSiNecesario(
            String tabla,
            String campo,
            Object valor) throws SQLException {

        if (valor == null || !esSensible(tabla, campo)) {
            return valor;
        }

        if (!(valor instanceof String)) {
            throw new SQLException(
                "Tipo invalido para un campo sensible"
            );
        }

        try {
            return CifradoAESGCM.cifrar((String) valor);

        } catch (IllegalStateException e) {
            throw new SQLException(
                "No se pudo cifrar el campo sensible", e
            );
        }
    }

    // Descifrar al consultar PostgreSQL
    public static Object descifrarSiNecesario(
            String tabla,
            String campo,
            Object valor) throws SQLException {

        if (valor == null || !esSensible(tabla, campo)) {
            return valor;
        }

        if (!(valor instanceof String)) {
            throw new SQLException(
                "Tipo invalido para un campo sensible"
            );
        }

        try {
            return CifradoAESGCM.descifrar((String) valor);

        } catch (IllegalStateException e) {
            throw new SQLException(
                "No se pudo descifrar el campo sensible", e
            );
        }
    }
}
