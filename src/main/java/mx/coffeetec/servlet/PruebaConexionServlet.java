package mx.coffeetec.servlet;

import mx.coffeetec.conexion.ConexionDB;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/prueba-conexion")
public class PruebaConexionServlet extends HttpServlet {

    private final ObjectMapper json = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String sql = "SELECT current_database()";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();

            response.setStatus(HttpServletResponse.SC_OK);

            json.writeValue(response.getWriter(), Map.of(
                "estado", "OK",
                "mensaje", "Java conectado con PostgreSQL",
                "baseDeDatos", rs.getString(1)
            ));

        } catch (SQLException e) {

            getServletContext().log(
                "Error de conexion con PostgreSQL", e
            );

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            json.writeValue(response.getWriter(), Map.of(
                "estado", "ERROR",
                "mensaje", "No se pudo conectar con PostgreSQL"
            ));
        }
    }
}
