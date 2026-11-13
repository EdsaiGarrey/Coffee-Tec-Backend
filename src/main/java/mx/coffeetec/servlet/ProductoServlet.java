
package mx.coffeetec.servlet;

// Clases de nuestro proyecto Coffee Tec
import mx.coffeetec.conexion.ConexionDB;
import mx.coffeetec.mvc.Producto;

// Librerias para trabajar con JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.JsonNode;

// Jakarta EE - Servlets
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

// Java
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

@WebServlet("/api/productos/*")
public class ProductoServlet extends HttpServlet {

    // Convierte objetos Java a JSON y viceversa.
    // Tambien permite mostrar las fechas de forma legible.
    private final ObjectMapper objectMapper =
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                );

    // =====================================================
    // 1. GET - CONSULTAR PRODUCTOS
    // =====================================================

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Consulta SQL adaptada a nuestra base coffee_tec
        String sql =
                "SELECT id_producto, id_categoria, nombre, "
              + "descripcion, precio, url_imagen, "
              + "stock, activo, fecha_creacion "
              + "FROM producto "
              + "ORDER BY id_producto";

        List<Producto> listaProductos = new ArrayList<>();

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Recorrer los registros obtenidos
            while (rs.next()) {

                Producto producto = new Producto();

                producto.setIdProducto(
                        rs.getLong("id_producto")
                );

                producto.setIdCategoria(
                        rs.getLong("id_categoria")
                );

                producto.setNombre(
                        rs.getString("nombre")
                );

                producto.setDescripcion(
                        rs.getString("descripcion")
                );

                producto.setPrecio(
                        rs.getBigDecimal("precio")
                );

                producto.setUrlImagen(
                        rs.getString("url_imagen")
                );

                producto.setStock(
                        rs.getInt("stock")
                );

                producto.setActivo(
                        rs.getBoolean("activo")
                );

                producto.setFechaCreacion(
                        rs.getObject(
                            "fecha_creacion",
                            OffsetDateTime.class
                        )
                );

                listaProductos.add(producto);
            }

            // Respuesta HTTP 200 - OK
            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            // Convertir lista de productos a JSON
            objectMapper.writeValue(
                    response.getWriter(),
                    listaProductos
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al consultar productos", e
            );



            enviarError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudieron consultar los productos"

            );
        }
    }

    // =====================================================
    // 2. POST - REGISTRAR UN PRODUCTO
    // =====================================================

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Producto nuevoProducto;

        // Leer el JSON enviado desde Angular o Postman
        try {

            nuevoProducto = objectMapper.readValue(
                    request.getReader(),
                    Producto.class
            );

        } catch (JsonProcessingException e) {

            enviarError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El JSON enviado no es valido"
            );

            return;
        }

        // Validar los campos obligatorios
        if (nuevoProducto == null
                || nuevoProducto.getIdCategoria() == null
                || nuevoProducto.getIdCategoria() <= 0
                || nuevoProducto.getNombre() == null
                || nuevoProducto.getNombre().trim().isEmpty()
                || nuevoProducto.getNombre().length() > 120
                || nuevoProducto.getPrecio() == null
                || nuevoProducto.getPrecio().signum() < 0
                || nuevoProducto.getStock() == null
                || nuevoProducto.getStock() < 0) {

            enviarError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Datos del producto invalidos"
            );

            return;
        }

        // Consulta SQL para insertar un producto
        String sql =
                "INSERT INTO producto "
              + "(id_categoria, nombre, descripcion, "
              + "precio, url_imagen, stock) "
              + "VALUES (?, ?, ?, ?, ?, ?) "
              + "RETURNING id_producto, activo, fecha_creacion";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Asignar los valores usando PreparedStatement
            ps.setLong(
                    1, nuevoProducto.getIdCategoria()
            );

            ps.setString(
                    2, nuevoProducto.getNombre().trim()
            );

            ps.setString(
                    3, nuevoProducto.getDescripcion()
            );

            ps.setBigDecimal(
                    4, nuevoProducto.getPrecio()
            );

            ps.setString(
                    5, nuevoProducto.getUrlImagen()
            );

            ps.setInt(
                    6, nuevoProducto.getStock()
            );

            // Ejecutar INSERT y recuperar datos generados
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    nuevoProducto.setIdProducto(
                            rs.getLong("id_producto")
                    );

                    nuevoProducto.setActivo(
                            rs.getBoolean("activo")
                    );

                    nuevoProducto.setFechaCreacion(
                            rs.getObject(
                                "fecha_creacion",
                                OffsetDateTime.class
                            )
                    );
                }
            }

            // Respuesta HTTP 201 - CREATED
            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            // Devolver el producto registrado como JSON
            objectMapper.writeValue(
                    response.getWriter(),
                    nuevoProducto
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al registrar producto", e
            );

            String estadoSQL = e.getSQLState();

            // Producto duplicado en la misma categoria
            if ("23505".equals(estadoSQL)) {

                enviarError(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        "Ya existe ese producto en la categoria"
                );

            // Categoria inexistente
            } else if ("23503".equals(estadoSQL)) {

                enviarError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "La categoria indicada no existe"
                );

            // Datos que incumplen restricciones de la BD
            } else if ("23514".equals(estadoSQL)
                    || "23502".equals(estadoSQL)
                    || "22003".equals(estadoSQL)
                    || "22001".equals(estadoSQL)) {

                enviarError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Los datos no cumplen las reglas de la BD"
                );

            } else {

                enviarError(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error al guardar el producto"
                );
            }
        }
    }

    // =====================================================
    // 3. PUT - ACTUALIZAR COMPLETAMENTE UN PRODUCTO
    // =====================================================

    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Obtener el ID desde /api/productos/2
        String ruta = request.getPathInfo();

        if (ruta == null || !ruta.matches("/[1-9][0-9]*")) {
            enviarError(response, 400, "ID de producto invalido");
            return;
        }

        long idProducto;

        try {
            idProducto = Long.parseLong(ruta.substring(1));
        } catch (NumberFormatException e) {
            enviarError(response, 400, "ID fuera de rango");
            return;
        }

        // Convertir JSON a objeto Java
        Producto producto;

        try {
            producto = objectMapper.readValue(
                    request.getReader(), Producto.class);
        } catch (JsonProcessingException e) {
            enviarError(response, 400, "JSON invalido");
            return;
        }

        // Validar todos los campos editables obligatorios
        if (producto == null
                || producto.getIdCategoria() == null
                || producto.getIdCategoria() <= 0
                || producto.getNombre() == null
                || producto.getNombre().trim().isEmpty()
                || producto.getNombre().length() > 120
                || producto.getPrecio() == null
                || producto.getPrecio().signum() < 0
                || producto.getStock() == null
                || producto.getStock() < 0
                || producto.getActivo() == null) {

            enviarError(response, 400, "Datos invalidos");
            return;
        }

        String sql = "UPDATE producto SET "
                + "id_categoria = ?, nombre = ?, "
                + "descripcion = ?, precio = ?, "
                + "url_imagen = ?, stock = ?, activo = ? "
                + "WHERE id_producto = ? "
                + "RETURNING fecha_creacion";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, producto.getIdCategoria());
            ps.setString(2, producto.getNombre().trim());
            ps.setString(3, producto.getDescripcion());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setString(5, producto.getUrlImagen());
            ps.setInt(6, producto.getStock());
            ps.setBoolean(7, producto.getActivo());
            ps.setLong(8, idProducto);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    enviarError(response, 404, "Producto no encontrado");
                    return;
                }

                producto.setIdProducto(idProducto);
                producto.setNombre(producto.getNombre().trim());
                producto.setFechaCreacion(
                        rs.getObject(
                            "fecha_creacion",
                            OffsetDateTime.class
                        )
                );
            }

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    response.getWriter(), producto);

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al actualizar producto", e
            );

            if ("23505".equals(e.getSQLState())) {
                enviarError(response, 409, "Producto duplicado");
            } else if ("23503".equals(e.getSQLState())) {
                enviarError(response, 400, "Categoria inexistente");
            } else if ("23514".equals(e.getSQLState())
                    || "23502".equals(e.getSQLState())
                    || "22001".equals(e.getSQLState())
                    || "22003".equals(e.getSQLState())) {
                enviarError(response, 400, "Datos fuera de rango");
            } else {
                enviarError(response, 500,
                        "No se pudo actualizar el producto");
            }
        }
    }


    // =====================================================
    // 4. PATCH - ACTUALIZAR CAMPOS ESPECIFICOS
    // =====================================================

    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    protected void doPatch(HttpServletRequest request,
                           HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String ruta = request.getPathInfo();

        if (ruta == null || !ruta.matches("/[1-9][0-9]*")) {
            enviarError(response, 400, "ID invalido");
            return;
        }

        long idProducto;

        try {
            idProducto = Long.parseLong(ruta.substring(1));
        } catch (NumberFormatException e) {
            enviarError(response, 400, "ID fuera de rango");
            return;
        }

        JsonNode datos;

        try {
            datos = objectMapper.readTree(request.getReader());
        } catch (JsonProcessingException e) {
            enviarError(response, 400, "JSON invalido");
            return;
        }

        if (datos == null || !datos.isObject()
                || datos.size() == 0) {
            enviarError(response, 400, "No hay cambios validos");
            return;
        }

        List<String> campos = new ArrayList<>();
        List<Object> valores = new ArrayList<>();

        Iterator<String> nombres = datos.fieldNames();

        while (nombres.hasNext()) {
            String campo = nombres.next();
            JsonNode valor = datos.get(campo);

            switch (campo) {

                case "nombre":
                    if (!valor.isTextual()
                            || valor.asText().trim().isEmpty()
                            || valor.asText().length() > 120) {
                        enviarError(response, 400, "Nombre invalido");
                        return;
                    }
                    campos.add("nombre = ?");
                    valores.add(valor.asText().trim());
                    break;

                case "precio":
                    if (!valor.isNumber()
                            || valor.decimalValue().signum() < 0) {
                        enviarError(response, 400, "Precio invalido");
                        return;
                    }
                    campos.add("precio = ?");
                    valores.add(valor.decimalValue());
                    break;

                case "stock":
                    if (!valor.isIntegralNumber()
                            || !valor.canConvertToInt()
                            || valor.asInt() < 0) {
                        enviarError(response, 400, "Stock invalido");
                        return;
                    }
                    campos.add("stock = ?");
                    valores.add(valor.asInt());
                    break;

                default:
                    enviarError(response, 400,
                            "Campo no permitido: " + campo);
                    return;
            }
        }

        String sql = "UPDATE producto SET "
                + String.join(", ", campos)
                + " WHERE id_producto = ? "
                + "RETURNING id_producto, nombre, precio, stock";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }

            ps.setLong(valores.size() + 1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    enviarError(response, 404,
                            "Producto no encontrado");
                    return;
                }

                response.setStatus(200);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of(
                            "idProducto", rs.getLong("id_producto"),
                            "nombre", rs.getString("nombre"),
                            "precio", rs.getBigDecimal("precio"),
                            "stock", rs.getInt("stock")
                        )
                );
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al aplicar PATCH", e);

            if ("23505".equals(e.getSQLState())) {
                enviarError(response, 409,
                        "Producto duplicado");
            } else if ("23514".equals(e.getSQLState())
                    || "22003".equals(e.getSQLState())) {
                enviarError(response, 400,
                        "Valor fuera de rango");
            } else {
                enviarError(response, 500,
                        "Error al actualizar parcialmente");
            }
        }
    }

    // =====================================================
    // 5. DELETE - ELIMINAR UN PRODUCTO
    // =====================================================

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Obtener ID desde /api/productos/2
        String ruta = request.getPathInfo();

        if (ruta == null || !ruta.matches("/[1-9][0-9]*")) {
            enviarError(response, 400, "ID invalido");
            return;
        }

        long idProducto;

        try {
            idProducto = Long.parseLong(ruta.substring(1));
        } catch (NumberFormatException e) {
            enviarError(response, 400, "ID fuera de rango");
            return;
        }

        // El trigger de PostgreSQL realizara el respaldo.
        String sql = "DELETE FROM producto "
                   + "WHERE id_producto = ? "
                   + "RETURNING id_producto, nombre";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    enviarError(response, 404,
                            "Producto no encontrado");
                    return;
                }

                String nombreProducto = rs.getString("nombre");

                response.setStatus(HttpServletResponse.SC_OK);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of(
                            "mensaje", "Producto eliminado correctamente",
                            "idProducto", idProducto,
                            "nombre", nombreProducto
                        )
                );
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al eliminar producto", e
            );

            if ("23503".equals(e.getSQLState())) {

                enviarError(response, 409,
                        "El producto esta relacionado con otros registros");

            } else {

                enviarError(response, 500,
                        "No se pudo eliminar el producto");
            }
        }
    }

    // =====================================================
    // METODO AUXILIAR PARA RESPUESTAS DE ERROR
    // =====================================================

    private void enviarError(HttpServletResponse response,
                             int estado,
                             String mensaje)
            throws IOException {

        response.setStatus(estado);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
                response.getWriter(),
                Map.of("error", mensaje)
        );
    }
}
