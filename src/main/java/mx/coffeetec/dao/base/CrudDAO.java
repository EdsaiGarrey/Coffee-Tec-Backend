package mx.coffeetec.dao.base;

import mx.coffeetec.conexion.ConexionDB;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/** Base JDBC reutilizable: las sentencias SQL nunca se construyen con nombres del cliente HTTP. */
public abstract class CrudDAO<T> {

    public static final class Columna {
        public final String propiedad;
        public final String sql;
        public final boolean insertable;
        public final boolean actualizable;
        public final boolean obligatoria;

        public Columna(String propiedad, String sql, boolean insertable,
                       boolean actualizable, boolean obligatoria) {
            this.propiedad = propiedad;
            this.sql = sql;
            this.insertable = insertable;
            this.actualizable = actualizable;
            this.obligatoria = obligatoria;
        }
    }

    private final Class<T> tipo;
    private final String tabla;
    private final List<String> claves;
    private final LinkedHashMap<String, Columna> columnas = new LinkedHashMap<>();
    private final Map<String, PropertyDescriptor> atributos = new LinkedHashMap<>();

    protected CrudDAO(Class<T> tipo, String tabla, List<String> claves, Columna... columnas) {
        this.tipo = tipo;
        this.tabla = validarIdentificador(tabla);
        this.claves = Collections.unmodifiableList(new ArrayList<>(claves));
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(tipo).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                    atributos.put(pd.getName(), pd);
                }
            }
        } catch (java.beans.IntrospectionException e) {
            throw new IllegalStateException("Error leyendo propiedades de " + tipo.getName(), e);
        }
        for (Columna c : columnas) {
            validarIdentificador(c.sql);
            if (!atributos.containsKey(c.propiedad) || this.columnas.put(c.propiedad, c) != null) {
                throw new IllegalArgumentException("Propiedad duplicada o inexistente: " + c.propiedad);
            }
        }
        if (claves.isEmpty() || !this.columnas.keySet().containsAll(claves)) {
            throw new IllegalArgumentException("Clave primaria invalida en " + tabla);
        }
    }

    protected static Columna c(String propiedad, String sql,
                               boolean insertable, boolean actualizable,
                               boolean obligatoria) {
        return new Columna(propiedad, sql, insertable, actualizable, obligatoria);
    }

    private static String validarIdentificador(String v) {
        if (!v.matches("[a-z][a-z0-9_]*")) {
            throw new IllegalArgumentException("Identificador SQL invalido");
        }
        return v;
    }

    public int numeroClaves() { return claves.size(); }
    public Set<String> camposInsertables() {
        return columnas.values().stream().filter(c -> c.insertable)
                .map(c -> c.propiedad).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    public Set<String> camposActualizables() {
        return columnas.values().stream().filter(c -> c.actualizable)
                .map(c -> c.propiedad).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    public Set<String> camposObligatorios() {
        return columnas.values().stream().filter(c -> c.obligatoria)
                .map(c -> c.propiedad).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    public Class<?> tipoCampo(String propiedad) {
        PropertyDescriptor pd = atributos.get(propiedad);
        if (pd == null) throw new IllegalArgumentException("Campo desconocido: " + propiedad);
        return pd.getPropertyType();
    }

    private String condicionPk() {
        return claves.stream().map(k -> columnas.get(k).sql + " = ?")
                .collect(Collectors.joining(" AND "));
    }

    private T mapear(ResultSet rs) throws SQLException {
        try {
            T objeto = tipo.getDeclaredConstructor().newInstance();
            for (Columna c : columnas.values()) {
                PropertyDescriptor pd = atributos.get(c.propiedad);
                Class<?> t = pd.getPropertyType();
                Object valor;
                if (t == OffsetDateTime.class) {
                    valor = rs.getObject(c.sql, OffsetDateTime.class);
                } else if (t == LocalDate.class) {
                    valor = rs.getObject(c.sql, LocalDate.class);
                } else if (t == BigDecimal.class) {
                    valor = rs.getBigDecimal(c.sql);
                } else {
                    valor = rs.getObject(c.sql);
                }
                pd.getWriteMethod().invoke(objeto, valor);
            }
            return objeto;
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            throw new SQLException("Error mapeando " + tabla, e);
        }
    }

    private void parametros(PreparedStatement ps, List<Object> valores, int inicio)
            throws SQLException {
        for (int i = 0; i < valores.size(); i++) {
            ps.setObject(inicio + i, valores.get(i));
        }
    }

    public List<T> listar() throws SQLException {
        String orden = claves.stream().map(k -> columnas.get(k).sql)
                .collect(Collectors.joining(", "));
        String sql = "SELECT * FROM " + tabla + " ORDER BY " + orden + " LIMIT 200";
        List<T> resultado = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
        }
        return resultado;
    }

    public T buscarPorId(long... ids) throws SQLException {
        comprobarClaves(ids);
        String sql = "SELECT * FROM " + tabla + " WHERE " + condicionPk();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < ids.length; i++) ps.setLong(i + 1, ids[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public T insertar(Map<String, Object> cambios) throws SQLException {
        comprobarCampos(cambios, true);
        for (String k : camposObligatorios()) {
            if (!cambios.containsKey(k) || cambios.get(k) == null) {
                throw new IllegalArgumentException("Falta campo obligatorio: " + k);
            }
        }
        List<Columna> incluidas = columnas.values().stream()
                .filter(c -> c.insertable && cambios.containsKey(c.propiedad))
                .collect(Collectors.toList());
        String sql;
        if (incluidas.isEmpty()) {
            sql = "INSERT INTO " + tabla + " DEFAULT VALUES RETURNING *";
        } else {
            String nombres = incluidas.stream().map(c -> c.sql).collect(Collectors.joining(", "));
            String signos = String.join(", ", Collections.nCopies(incluidas.size(), "?"));
            sql = "INSERT INTO " + tabla + " (" + nombres + ") VALUES (" + signos + ") RETURNING *";
        }
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            List<Object> valores = incluidas.stream().map(c -> cambios.get(c.propiedad)).collect(Collectors.toList());
            parametros(ps, valores, 1);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("INSERT no devolvio registro");
                return mapear(rs);
            }
        }
    }

    public T actualizar(Map<String, Object> cambios, long... ids) throws SQLException {
        comprobarClaves(ids);
        comprobarCampos(cambios, false);
        if (cambios.isEmpty()) throw new IllegalArgumentException("No se enviaron campos para actualizar");
        List<Columna> incluidas = columnas.values().stream()
                .filter(c -> c.actualizable && cambios.containsKey(c.propiedad))
                .collect(Collectors.toList());
        String set = incluidas.stream().map(c -> c.sql + " = ?").collect(Collectors.joining(", "));
        String sql = "UPDATE " + tabla + " SET " + set + " WHERE " + condicionPk() + " RETURNING *";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            List<Object> valores = incluidas.stream().map(c -> cambios.get(c.propiedad)).collect(Collectors.toList());
            parametros(ps, valores, 1);
            for (int i = 0; i < ids.length; i++) ps.setLong(valores.size() + i + 1, ids[i]);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapear(rs) : null; }
        }
    }

    public T eliminar(long... ids) throws SQLException {
        comprobarClaves(ids);
        String sql = "DELETE FROM " + tabla + " WHERE " + condicionPk() + " RETURNING *";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < ids.length; i++) ps.setLong(i + 1, ids[i]);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapear(rs) : null; }
        }
    }

    private void comprobarClaves(long[] ids) {
        if (ids == null || ids.length != claves.size()) {
            throw new IllegalArgumentException("Se esperan " + claves.size() + " identificadores");
        }
        for (long id : ids) if (id <= 0) throw new IllegalArgumentException("ID debe ser positivo");
    }

    private void comprobarCampos(Map<String, Object> cambios, boolean insertar) {
        for (String campo : cambios.keySet()) {
            Columna c = columnas.get(campo);
            if (c == null || !(insertar ? c.insertable : c.actualizable)) {
                throw new IllegalArgumentException("Campo no permitido: " + campo);
            }
            if (c.obligatoria && cambios.get(campo) == null) {
                throw new IllegalArgumentException("El campo no puede ser nulo: " + campo);
            }
        }
    }
}
