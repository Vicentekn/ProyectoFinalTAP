package dao;

import model.Usuario;
import security.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl extends GenericDAO<Usuario> implements UsuarioDAO {

    public UsuarioDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Usuario autenticar(String username, String passwordPlano) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND activo = TRUE";
        String passwordEncriptada = HashUtil.sha1(passwordPlano);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordEncriptada);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null; // Retorna null si las credenciales son incorrectas o el usuario está inactivo
    }

    @Override
    public boolean save(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, username, password, rol, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getUsername());
            stmt.setString(3, HashUtil.sha1(u.getPassword())); // Se encripta antes de guardar
            stmt.setString(4, u.getRol());
            stmt.setBoolean(5, u.isActivo());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        u.setId(keys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Usuario u) {
        String sql = "UPDATE usuarios SET nombre = ?, username = ?, rol = ?, activo = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getUsername());
            stmt.setString(3, u.getRol());
            stmt.setBoolean(4, u.isActivo());
            stmt.setLong(5, u.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario findById(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    protected Usuario mapResultSetToEntity(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNombre(rs.getString("nombre"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            u.setFechaCreacion(ts.toLocalDateTime());
        }
        return u;
    }
}