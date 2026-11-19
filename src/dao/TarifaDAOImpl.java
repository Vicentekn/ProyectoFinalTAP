package dao;

import model.Tarifa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta del patrón DAO para la tabla 'tarifas'.
 *
 * PATRÓN DE DISEÑO IMPLEMENTADO: DAO (Data Access Object).
 * Hereda de {@link GenericDAO} aplicando programación genérica {@code <Tarifa>}.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class TarifaDAOImpl extends GenericDAO<Tarifa> implements TarifaDAO {

    public TarifaDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Tarifa buscarPorTipoVehiculo(String tipoVehiculo) {
        String sql = "SELECT * FROM tarifas WHERE tipo_vehiculo = ? AND activo = TRUE LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipoVehiculo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al buscar tarifa por tipo de vehículo: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Tarifa> listarActivas() {
        List<Tarifa> lista = new ArrayList<>();
        String sql = "SELECT * FROM tarifas WHERE activo = TRUE ORDER BY id ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar tarifas activas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean save(Tarifa t) {
        String sql = "INSERT INTO tarifas (tipo_vehiculo, costo_hora, costo_fraccion, minutos_gracia, costo_boleto_perdido, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, t.getTipoVehiculo());
            stmt.setDouble(2, t.getCostoHora());
            stmt.setDouble(3, t.getCostoFraccion());
            stmt.setInt(4, t.getMinutosGracia());
            stmt.setDouble(5, t.getCostoBoletoPerdido());
            stmt.setBoolean(6, t.isActivo());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        t.setId(keys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al guardar tarifa: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Tarifa t) {
        String sql = "UPDATE tarifas SET tipo_vehiculo = ?, costo_hora = ?, costo_fraccion = ?, minutos_gracia = ?, costo_boleto_perdido = ?, activo = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, t.getTipoVehiculo());
            stmt.setDouble(2, t.getCostoHora());
            stmt.setDouble(3, t.getCostoFraccion());
            stmt.setInt(4, t.getMinutosGracia());
            stmt.setDouble(5, t.getCostoBoletoPerdido());
            stmt.setBoolean(6, t.isActivo());
            stmt.setLong(7, t.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar tarifa: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM tarifas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar tarifa: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Tarifa findById(Long id) {
        String sql = "SELECT * FROM tarifas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al buscar tarifa por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Tarifa> findAll() {
        List<Tarifa> lista = new ArrayList<>();
        String sql = "SELECT * FROM tarifas ORDER BY id ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar todas las tarifas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    protected Tarifa mapResultSetToEntity(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();
        t.setId(rs.getLong("id"));
        t.setTipoVehiculo(rs.getString("tipo_vehiculo"));
        t.setCostoHora(rs.getDouble("costo_hora"));
        t.setCostoFraccion(rs.getDouble("costo_fraccion"));
        t.setMinutosGracia(rs.getInt("minutos_gracia"));
        t.setCostoBoletoPerdido(rs.getDouble("costo_boleto_perdido"));
        t.setActivo(rs.getBoolean("activo"));
        return t;
    }
}