package mx.coffeetec.dao;

import mx.coffeetec.conexion.ConexionDB;
import mx.coffeetec.pojo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/** Solo acceso a datos: no maneja peticiones ni respuestas HTTP. */
public class ProductoDAO {

    private static final String COLUMNAS =
            "id_producto, id_categoria, nombre, descripcion, "
            + "precio, url_imagen, stock, activo, fecha_creacion";

    /** GET /api/productos */
    public List<Producto> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM producto ORDER BY id_producto";
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
        return productos;
    }
    
/** GET: buscar un producto por su ID */
public Producto buscarPorId(long id) throws SQLException {
    String sql = "SELECT " + COLUMNAS
            + " FROM producto WHERE id_producto = ?";

    try (Connection conn = ConexionDB.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setLong(1, id);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapearProducto(rs) : null;
        }
    }
}


    /** POST /api/productos */
    public Producto insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto "
                + "(id_categoria, nombre, descripcion, precio, url_imagen, stock) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING " + COLUMNAS;

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, producto.getIdCategoria());
            ps.setString(2, producto.getNombre().trim());
            ps.setString(3, producto.getDescripcion());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setString(5, producto.getUrlImagen());
            ps.setInt(6, producto.getStock());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        throw new SQLException("El INSERT no devolvio el producto creado");
    }

    /** PUT /api/productos/{id}: actualizacion completa. */
    public Producto actualizar(long id, Producto producto) throws SQLException {
        String sql = "UPDATE producto SET "
                + "id_categoria = ?, nombre = ?, descripcion = ?, "
                + "precio = ?, url_imagen = ?, stock = ?, activo = ? "
                + "WHERE id_producto = ? RETURNING " + COLUMNAS;

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, producto.getIdCategoria());
            ps.setString(2, producto.getNombre().trim());
            ps.setString(3, producto.getDescripcion());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setString(5, producto.getUrlImagen());
            ps.setInt(6, producto.getStock());
            ps.setBoolean(7, producto.getActivo());
            ps.setLong(8, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearProducto(rs) : null;
            }
        }
    }

    /** PATCH /api/productos/{id}: solo nombre, precio o stock. */
    public Map<String, Object> actualizarParcial(long id, Map<String, Object> cambios)
            throws SQLException {
        if (cambios == null || cambios.isEmpty()) {
            throw new IllegalArgumentException("No se recibieron cambios");
        }

        StringJoiner asignaciones = new StringJoiner(", ");
        List<Object> valores = new ArrayList<>();

        for (Map.Entry<String, Object> cambio : cambios.entrySet()) {
            // Nunca se concatena una columna enviada por el cliente.
            switch (cambio.getKey()) {
                case "nombre":
                    asignaciones.add("nombre = ?");
                    break;
                case "precio":
                    asignaciones.add("precio = ?");
                    break;
                case "stock":
                    asignaciones.add("stock = ?");
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Campo no permitido: " + cambio.getKey());
            }
            valores.add(cambio.getValue());
        }

        String sql = "UPDATE producto SET " + asignaciones
                + " WHERE id_producto = ? "
                + "RETURNING id_producto, nombre, precio, stock";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }
            ps.setLong(valores.size() + 1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, Object> resultado = new LinkedHashMap<>();
                resultado.put("idProducto", rs.getLong("id_producto"));
                resultado.put("nombre", rs.getString("nombre"));
                resultado.put("precio", rs.getBigDecimal("precio"));
                resultado.put("stock", rs.getInt("stock"));
                return resultado;
            }
        }
    }

    /** DELETE /api/productos/{id}. El trigger de PostgreSQL guarda el respaldo. */
    public Map<String, Object> eliminar(long id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id_producto = ? "
                + "RETURNING id_producto, nombre";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, Object> resultado = new LinkedHashMap<>();
                resultado.put("mensaje", "Producto eliminado correctamente");
                resultado.put("idProducto", rs.getLong("id_producto"));
                resultado.put("nombre", rs.getString("nombre"));
                return resultado;
            }
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getLong("id_producto"));
        producto.setIdCategoria(rs.getLong("id_categoria"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setUrlImagen(rs.getString("url_imagen"));
        producto.setStock(rs.getInt("stock"));
        producto.setActivo(rs.getBoolean("activo"));
        producto.setFechaCreacion(rs.getObject("fecha_creacion", OffsetDateTime.class));
        return producto;
    }
}
