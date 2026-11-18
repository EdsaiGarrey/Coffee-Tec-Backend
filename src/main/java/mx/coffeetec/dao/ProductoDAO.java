
package mx.coffeetec.dao;

import mx.coffeetec.conexion.ConexionDB;
import mx.coffeetec.pojo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // CONSULTAR TODOS LOS PRODUCTOS
    public List<Producto> listar() throws SQLException {

        List<Producto> productos = new ArrayList<>();

        String sql =
                "SELECT id_producto, id_categoria, nombre, "
                + "descripcion, precio, url_imagen, "
                + "stock, activo, fecha_creacion "
                + "FROM producto "
                + "ORDER BY id_producto";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Producto producto = new Producto();

                producto.setIdProducto(
                        rs.getLong("id_producto"));

                producto.setIdCategoria(
                        rs.getLong("id_categoria"));

                producto.setNombre(
                        rs.getString("nombre"));

                producto.setDescripcion(
                        rs.getString("descripcion"));

                producto.setPrecio(
                        rs.getBigDecimal("precio"));

                producto.setUrlImagen(
                        rs.getString("url_imagen"));

                producto.setStock(
                        rs.getInt("stock"));

                producto.setActivo(
                        rs.getBoolean("activo"));

                producto.setFechaCreacion(
                        rs.getObject(
                                "fecha_creacion",
                                OffsetDateTime.class
                        ));

                productos.add(producto);
            }
        }

        return productos;
    }
}
