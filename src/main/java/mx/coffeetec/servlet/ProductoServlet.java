package mx.coffeetec.servlet;

import mx.coffeetec.dao.ProductoDAO;
import mx.coffeetec.pojo.Producto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Recibe HTTP, valida JSON y delega todo el acceso SQL al DAO. */
@WebServlet("/api/productos/*")
public class ProductoServlet extends HttpServlet {

    private final ProductoDAO dao = new ProductoDAO();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // GET: listar productos
   
@Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
    prepararJson(response);

    try {
        String ruta = request.getPathInfo();

        if (ruta == null || "/".equals(ruta)) {
            // Mostrar todos los productos
            response.setStatus(200);
            objectMapper.writeValue(
                    response.getWriter(), dao.listar());
        } else {
            // Buscar un producto por ID
            Long id = leerId(request);

            if (id == null) {
                enviarError(response, 400, "ID invalido");
                return;
            }

            Producto producto = dao.buscarPorId(id);

            if (producto == null) {
                enviarError(response, 404,
                        "Producto no encontrado");
                return;
            }

            response.setStatus(200);
            objectMapper.writeValue(
                    response.getWriter(), producto);
        }

    } catch (SQLException e) {
        getServletContext().log(
                "Error al consultar productos", e);
        enviarError(response, 500,
                "No se pudieron consultar los productos");
    }
}


    // POST: registrar producto
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        prepararJson(response);
        Producto nuevoProducto;
        try {
            nuevoProducto = objectMapper.readValue(request.getReader(), Producto.class);
        } catch (JsonProcessingException e) {
            enviarError(response, 400, "El JSON enviado no es valido");
            return;
        }

        if (!productoValido(nuevoProducto, false)) {
            enviarError(response, 400, "Datos del producto invalidos");
            return;
        }

        try {
            Producto creado = dao.insertar(nuevoProducto);
            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), creado);
        } catch (SQLException e) {
            getServletContext().log("Error al registrar producto", e);
            enviarErrorSql(response, e, "Error al guardar el producto");
        }
    }

    // PUT: actualizar todos los campos editables
    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        prepararJson(response);
        Long id = leerId(request);
        if (id == null) {
            enviarError(response, 400, "ID de producto invalido");
            return;
        }

        Producto producto;
        try {
            producto = objectMapper.readValue(request.getReader(), Producto.class);
        } catch (JsonProcessingException e) {
            enviarError(response, 400, "JSON invalido");
            return;
        }

        if (!productoValido(producto, true)) {
            enviarError(response, 400, "Datos del producto invalidos");
            return;
        }

        try {
            Producto actualizado = dao.actualizar(id, producto);
            if (actualizado == null) {
                enviarError(response, 404, "Producto no encontrado");
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), actualizado);
        } catch (SQLException e) {
            getServletContext().log("Error al actualizar producto", e);
            enviarErrorSql(response, e, "No se pudo actualizar el producto");
        }
    }

    // Jakarta HttpServlet no ofrece doPatch directamente.
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

    // PATCH: permite cambiar nombre, precio y stock individualmente
    protected void doPatch(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        prepararJson(response);
        Long id = leerId(request);
        if (id == null) {
            enviarError(response, 400, "ID de producto invalido");
            return;
        }

        JsonNode datos;
        try {
            datos = objectMapper.readTree(request.getReader());
        } catch (JsonProcessingException e) {
            enviarError(response, 400, "JSON invalido");
            return;
        }

        if (datos == null || !datos.isObject() || datos.size() == 0) {
            enviarError(response, 400, "No hay cambios validos");
            return;
        }

        Map<String, Object> cambios = new LinkedHashMap<>();
        Iterator<String> campos = datos.fieldNames();

        while (campos.hasNext()) {
            String campo = campos.next();
            JsonNode valor = datos.get(campo);

            switch (campo) {
                case "nombre":
                    if (!valor.isTextual()
                            || valor.asText().trim().isEmpty()
                            || valor.asText().trim().length() > 120) {
                        enviarError(response, 400, "Nombre invalido");
                        return;
                    }
                    cambios.put("nombre", valor.asText().trim());
                    break;
                case "precio":
                    if (!valor.isNumber() || valor.decimalValue().signum() < 0) {
                        enviarError(response, 400, "Precio invalido");
                        return;
                    }
                    cambios.put("precio", valor.decimalValue());
                    break;
                case "stock":
                    if (!valor.isIntegralNumber() || !valor.canConvertToInt()
                            || valor.asInt() < 0) {
                        enviarError(response, 400, "Stock invalido");
                        return;
                    }
                    cambios.put("stock", valor.asInt());
                    break;
                default:
                    enviarError(response, 400, "Campo no permitido: " + campo);
                    return;
            }
        }

        try {
            Map<String, Object> actualizado = dao.actualizarParcial(id, cambios);
            if (actualizado == null) {
                enviarError(response, 404, "Producto no encontrado");
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), actualizado);
        } catch (SQLException e) {
            getServletContext().log("Error al aplicar PATCH", e);
            enviarErrorSql(response, e, "Error al actualizar parcialmente");
        }
    }

    // DELETE: eliminar producto; PostgreSQL administra el respaldo
    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        prepararJson(response);
        Long id = leerId(request);
        if (id == null) {
            enviarError(response, 400, "ID de producto invalido");
            return;
        }

        try {
            Map<String, Object> eliminado = dao.eliminar(id);
            if (eliminado == null) {
                enviarError(response, 404, "Producto no encontrado");
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), eliminado);
        } catch (SQLException e) {
            getServletContext().log("Error al eliminar producto", e);
            if ("23503".equals(e.getSQLState())) {
                enviarError(response, 409,
                        "El producto esta relacionado con otros registros");
            } else {
                enviarError(response, 500, "No se pudo eliminar el producto");
            }
        }
    }

    private boolean productoValido(Producto p, boolean activoObligatorio) {
        return p != null
                && p.getIdCategoria() != null && p.getIdCategoria() > 0
                && p.getNombre() != null && !p.getNombre().trim().isEmpty()
                && p.getNombre().trim().length() <= 120
                && p.getPrecio() != null && p.getPrecio().signum() >= 0
                && p.getStock() != null && p.getStock() >= 0
                && (!activoObligatorio || p.getActivo() != null);
    }

    private Long leerId(HttpServletRequest request) {
        String ruta = request.getPathInfo();
        if (ruta == null || !ruta.matches("/[1-9][0-9]*")) {
            return null;
        }
        try {
            return Long.valueOf(ruta.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void prepararJson(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void enviarErrorSql(HttpServletResponse response, SQLException e,
                                String mensajeGeneral) throws IOException {
        String estado = e.getSQLState();
        if ("23505".equals(estado)) {
            enviarError(response, 409, "Ya existe ese producto en la categoria");
        } else if ("23503".equals(estado)) {
            enviarError(response, 400, "La categoria indicada no existe");
        } else if ("23514".equals(estado) || "23502".equals(estado)
                || "22003".equals(estado) || "22001".equals(estado)) {
            enviarError(response, 400, "Los datos no cumplen las reglas de la BD");
        } else {
            enviarError(response, 500, mensajeGeneral);
        }
    }

    private void enviarError(HttpServletResponse response, int estado,
                             String mensaje) throws IOException {
        prepararJson(response);
        response.setStatus(estado);
        objectMapper.writeValue(response.getWriter(), Map.of("error", mensaje));
    }
}
