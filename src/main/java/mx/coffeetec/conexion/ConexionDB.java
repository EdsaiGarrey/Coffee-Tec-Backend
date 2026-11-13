
package mx.coffeetec.conexion;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class ConexionDB {

    private static final String JNDI_NAME = "jdbc/CoffeeTecDB";

    private ConexionDB() {
        // Evita crear objetos de esta clase utilitaria.
    }

    public static Connection getConexion() throws SQLException {
        try {
            InitialContext context = new InitialContext();

            DataSource dataSource =
                    (DataSource) context.lookup(JNDI_NAME);

            return dataSource.getConnection();

        } catch (NamingException e) {
            throw new SQLException(
                    "No se pudo encontrar el recurso JDBC: "
                    + JNDI_NAME, e
            );
        }
    }
}
