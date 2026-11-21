
package mx.coffeetec.conexion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class ConexionDB {

    private static volatile HikariDataSource pool;

    private ConexionDB() {
    }

    private static String obtenerVariable(String nombre) {
        String valor = System.getenv(nombre);

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalStateException(
                "Falta configurar la variable: " + nombre
            );
        }

        return valor;
    }

    private static HikariDataSource obtenerPool() {

        if (pool == null) {
            synchronized (ConexionDB.class) {

                if (pool == null) {
                    HikariConfig config = new HikariConfig();

                    config.setPoolName("CoffeeTecHikari");

                    config.setJdbcUrl(
                        obtenerVariable("COFFEETEC_DB_URL")
                    );

                    config.setUsername(
                        obtenerVariable("COFFEETEC_DB_USER")
                    );

                    config.setPassword(
                        obtenerVariable("COFFEETEC_DB_PASSWORD")
                    );

                    // Configuracion del pool
                    config.setMaximumPoolSize(5);
                    config.setMinimumIdle(1);
                    config.setConnectionTimeout(10000);
                    config.setIdleTimeout(600000);
                    config.setMaxLifetime(1800000);

                    pool = new HikariDataSource(config);
                }
            }
        }

        return pool;
    }

    

public static Connection getConexion()
        throws SQLException {

    try {
        return obtenerPool().getConnection();

    } catch (RuntimeException e) {
        throw new SQLException(
            "Error al obtener conexion con HikariCP", e
        );
    }
}



    public static synchronized void cerrarPool() {
        if (pool != null) {
            pool.close();
            pool = null;
        }
    }
}
