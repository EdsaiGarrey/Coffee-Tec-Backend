package mx.coffeetec.servlet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.coffeetec.dao.base.CrudDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Control HTTP reutilizable. Solo permite conexiones locales para pruebas de clase.
 * NO constituye autenticacion, autorizacion ni una proteccion de produccion.
 */
public abstract class CrudServlet<T> extends HttpServlet {

    private final ObjectMapper json = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected abstract CrudDAO<T> dao();

    /** UsuarioServlet puede bloquearse hasta implementar login y control de roles. */
    protected boolean habilitado() { return true; }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (!habilitado()) {
            error(resp, 403, "Modulo deshabilitado hasta implementar autenticacion y autorizacion");
            return;
        }
        String ip = req.getRemoteAddr();
        if (!("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip))) {
            error(resp, 403, "Solo disponible para pruebas locales");
            return;
        }
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long[] claves = clavesDeRuta(req, false);
            if (claves.length == 0) {
                escribir(resp, 200, dao().listar());
            } else {
                T objeto = dao().buscarPorId(claves);
                if (objeto == null) error(resp, 404, "Registro no encontrado");
                else escribir(resp, 200, objeto);
            }
        } catch (IllegalArgumentException e) { error(resp, 400, e.getMessage());
        } catch (SQLException e) { errorSQL(resp, e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (clavesDeRuta(req, false).length != 0) {
                throw new IllegalArgumentException("POST debe utilizar la ruta sin identificadores");
            }
            Map<String,Object> datos = datosJson(req, dao().camposInsertables());
            escribir(resp, 201, dao().insertar(datos));
        } catch (JsonProcessingException e) { error(resp, 400, "JSON invalido");
        } catch (IllegalArgumentException e) { error(resp, 400, e.getMessage());
        } catch (SQLException e) { errorSQL(resp, e); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        guardarCambios(req, resp, false);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        guardarCambios(req, resp, true);
    }

    private void guardarCambios(HttpServletRequest req, HttpServletResponse resp, boolean parcial)
            throws IOException {
        try {
            long[] claves = clavesDeRuta(req, true);
            Map<String,Object> datos = datosJson(req, dao().camposActualizables());
            if (!parcial && !datos.keySet().containsAll(dao().camposActualizables())) {
                throw new IllegalArgumentException("PUT requiere todos los campos editables; usa PATCH para cambios parciales");
            }
            T objeto = dao().actualizar(datos, claves);
            if (objeto == null) error(resp, 404, "Registro no encontrado");
            else escribir(resp, 200, objeto);
        } catch (JsonProcessingException e) { error(resp, 400, "JSON invalido");
        } catch (IllegalArgumentException e) { error(resp, 400, e.getMessage());
        } catch (SQLException e) { errorSQL(resp, e); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long[] claves = clavesDeRuta(req, true);
            T eliminado = dao().eliminar(claves);
            if (eliminado == null) error(resp, 404, "Registro no encontrado");
            else {
                Map<String,Object> resultado = new LinkedHashMap<>();
                resultado.put("mensaje", "Registro eliminado");
                resultado.put("registro", eliminado);
                escribir(resp, 200, resultado);
            }
        } catch (IllegalArgumentException e) { error(resp, 400, e.getMessage());
        } catch (SQLException e) { errorSQL(resp, e); }
    }

    private long[] clavesDeRuta(HttpServletRequest req, boolean obligatorias) {
        String ruta = req.getPathInfo();
        if (ruta == null || "/".equals(ruta)) {
            if (obligatorias) throw new IllegalArgumentException("Falta identificador en la ruta");
            return new long[0];
        }
        String[] partes = ruta.split("/", -1);
        if (partes.length != dao().numeroClaves() + 1 || !partes[0].isEmpty()) {
            throw new IllegalArgumentException("Numero incorrecto de identificadores en la ruta");
        }
        long[] ids = new long[partes.length - 1];
        try {
            for (int i = 1; i < partes.length; i++) {
                ids[i - 1] = Long.parseLong(partes[i]);
                if (ids[i - 1] <= 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identificadores invalidos");
        }
        return ids;
    }

    private Map<String,Object> datosJson(HttpServletRequest req, Set<String> permitidos)
            throws IOException {
        JsonNode cuerpo = json.readTree(req.getReader());
        if (cuerpo == null || !cuerpo.isObject()) {
            throw new IllegalArgumentException("Se requiere un objeto JSON");
        }
        Map<String,Object> datos = new LinkedHashMap<>();
        java.util.Iterator<Map.Entry<String,JsonNode>> it = cuerpo.fields();
        while (it.hasNext()) {
            Map.Entry<String,JsonNode> entrada = it.next();
            String campo = entrada.getKey();
            if (!permitidos.contains(campo)) {
                throw new IllegalArgumentException("Campo no permitido: " + campo);
            }
            JsonNode valor = entrada.getValue();
            Class<?> tipo = dao().tipoCampo(campo);
            if (valor.isNull()) {
                datos.put(campo, null);
            } else {
                // Evita convertir automaticamente cadenas arbitrarias a numeros o booleanos.
                if (tipo == String.class && !valor.isTextual())
                    throw new IllegalArgumentException("Se requiere texto: " + campo);
                if (tipo == Boolean.class && !valor.isBoolean())
                    throw new IllegalArgumentException("Se requiere booleano: " + campo);
                if (Number.class.isAssignableFrom(tipo) && !valor.isNumber())
                    throw new IllegalArgumentException("Se requiere numero: " + campo);
                if ((tipo == java.time.LocalDate.class || tipo == java.time.OffsetDateTime.class)
                    && !valor.isTextual())
                    throw new IllegalArgumentException("Se requiere fecha ISO: " + campo);
                try {
                    datos.put(campo, json.convertValue(valor, tipo));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Formato incorrecto en: " + campo);
                }
            }
        }
        return datos;
    }

    protected void escribir(HttpServletResponse resp, int status, Object valor) throws IOException {
        resp.setStatus(status);
        json.writeValue(resp.getWriter(), valor);
    }

    protected void error(HttpServletResponse resp, int status, String mensaje) throws IOException {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("error", mensaje);
        escribir(resp, status, m);
    }

    private void errorSQL(HttpServletResponse resp, SQLException e) throws IOException {
        getServletContext().log("Error SQL en " + getClass().getSimpleName(), e);
        String estado = e.getSQLState();
        if ("23505".equals(estado)) error(resp, 409, "El registro ya existe o incumple una restriccion UNIQUE");
        else if ("23503".equals(estado)) error(resp, 409, "Operacion incompatible con relaciones existentes");
        else if ("23502".equals(estado) || "23514".equals(estado) ||
                 (estado != null && estado.startsWith("22")))
            error(resp, 400, "Datos invalidos segun las reglas de PostgreSQL");
        else error(resp, 500, "Error interno al acceder a la base de datos");
    }
}
