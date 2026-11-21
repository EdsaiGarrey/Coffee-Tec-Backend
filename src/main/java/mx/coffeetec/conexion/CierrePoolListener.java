
package mx.coffeetec.conexion;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CierrePoolListener
        implements ServletContextListener {

    @Override
    public void contextDestroyed(
            ServletContextEvent evento) {

        ConexionDB.cerrarPool();
    }
}
