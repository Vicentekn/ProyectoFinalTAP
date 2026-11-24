package dao;

import model.Ticket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementación concreta del patrón DAO para la tabla 'tickets'.
 *
 * PATRÓN DE DISEÑO IMPLEMENTADO: DAO (Data Access Object).
 * Gestiona la persistencia de entradas (CU-01), salidas y cobros (CU-02, CU-03),
 * así como el control de disponibilidad de cajones en tiempo real (RF-04).
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class TicketDAOImpl extends GenericDAO<Ticket> implements TicketDAO {

    public TicketDAOImpl(Connection connection) {
        super(connection);
    }

    /**
     * Genera un código de folio único basado en la fecha/hora actual y un sufijo aleatorio/secuencial.
     *
     * @return Cadena con formato TCK-yyyyMMdd-HHmmss.
     */
    public String generarNuevoFolio() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return "TCK-" + LocalDateTime.now().format(fmt);
    }

    @Override
    public boolean save(Ticket t) {
        String sql = "INSERT INTO tickets (folio, placa, tipo_vehiculo, fecha_ingreso, cajon_asignado, monto_total, estado, usuario_ingreso_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (t.getFolio() == null || t.getFolio().isEmpty()) {
                t.setFolio(generarNuevoFolio());
            }
            if (t.getFechaIngreso() == null) {
                t.setFechaIngreso(LocalDateTime.now());
            }

            stmt.setString(1, t.getFolio());
            stmt.setString(2, t.getPlaca().toUpperCase());
            stmt.setString(3, t.getTipoVehiculo());
            stmt.setTimestamp(4, Timestamp.valueOf(t.getFechaIngreso()));
            stmt.setInt(5, t.getCajonAsignado());
            stmt.setDouble(6, 0.0);
            stmt.setString(7, "EN_ESTACIONAMIENTO");

            if (t.getUsuarioIngresoId() != null && t.getUsuarioIngresoId() > 0) {
                stmt.setLong(8, t.getUsuarioIngresoId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        t.setId(keys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar entrada de ticket (CU-01): " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean registrarSalida(Ticket t) {
        String sql = "UPDATE tickets SET fecha_salida = ?, monto_total = ?, estado = 'PAGADO', usuario_salida_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (t.getFechaSalida() == null) {
                t.setFechaSalida(LocalDateTime.now());
            }
            stmt.setTimestamp(1, Timestamp.valueOf(t.getFechaSalida()));
            stmt.setDouble(2, t.getMontoTotal());

            if (t.getUsuarioSalidaId() != null && t.getUsuarioSalidaId() > 0) {
                stmt.setLong(3, t.getUsuarioSalidaId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }

            stmt.setLong(4, t.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar salida de ticket (CU-02/03): " + e.getMessage());
            return false;
        }
    }

    @Override
    public Ticket buscarActivoPorFolioOPlaca(String folioOPlaca) {
        String sql = "SELECT * FROM tickets WHERE (folio = ? OR placa = ?) AND estado = 'EN_ESTACIONAMIENTO' LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String criterio = folioOPlaca.trim().toUpperCase();
            stmt.setString(1, criterio);
            stmt.setString(2, criterio);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al buscar ticket activo: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Ticket> listarEnEstacionamiento() {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE estado = 'EN_ESTACIONAMIENTO' ORDER BY fecha_ingreso DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar vehículos en estacionamiento: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public int contarOcupados() {
        String sql = "SELECT COUNT(*) AS total FROM tickets WHERE estado = 'EN_ESTACIONAMIENTO'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al contar cajones ocupados: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int obtenerSiguienteCajonDisponible(int capacidadMaxima) {
        Set<Integer> ocupados = new HashSet<>();
        String sql = "SELECT cajon_asignado FROM tickets WHERE estado = 'EN_ESTACIONAMIENTO'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ocupados.add(rs.getInt("cajon_asignado"));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al consultar cajones ocupados: " + e.getMessage());
            return -1;
        }

        // Busca el primer número de cajón libre del 1 hasta capacidadMaxima
        for (int cajon = 1; cajon <= capacidadMaxima; cajon++) {
            if (!ocupados.contains(cajon)) {
                return cajon;
            }
        }
        return -1; // Estacionamiento lleno (RF-04)
    }

    @Override
    public boolean update(Ticket t) {
        return registrarSalida(t);
    }

    @Override
    public boolean delete(Long id) {
        String sql = "UPDATE tickets SET estado = 'CANCELADO' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al cancelar ticket: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Ticket findById(Long id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al buscar ticket por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM tickets ORDER BY fecha_ingreso DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar historial de tickets: " + e.getMessage());
        }
        return lista;
    }

    @Override
    protected Ticket mapResultSetToEntity(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getLong("id"));
        t.setFolio(rs.getString("folio"));
        t.setPlaca(rs.getString("placa"));
        t.setTipoVehiculo(rs.getString("tipo_vehiculo"));

        Timestamp ingreso = rs.getTimestamp("fecha_ingreso");
        if (ingreso != null) {
            t.setFechaIngreso(ingreso.toLocalDateTime());
        }

        Timestamp salida = rs.getTimestamp("fecha_salida");
        if (salida != null) {
            t.setFechaSalida(salida.toLocalDateTime());
        }

        t.setCajonAsignado(rs.getInt("cajon_asignado"));
        t.setMontoTotal(rs.getDouble("monto_total"));
        t.setEstado(rs.getString("estado"));

        long usrIngreso = rs.getLong("usuario_ingreso_id");
        if (!rs.wasNull()) t.setUsuarioIngresoId(usrIngreso);

        long usrSalida = rs.getLong("usuario_salida_id");
        if (!rs.wasNull()) t.setUsuarioSalidaId(usrSalida);

        return t;
    }

    @Override
    public List<Ticket> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin, String estado) {
        List<Ticket> lista = new ArrayList<>();
        boolean filtrarEstado = (estado != null && !estado.equalsIgnoreCase("TODOS"));

        String sql = "SELECT * FROM tickets WHERE fecha_ingreso BETWEEN ? AND ? " +
                (filtrarEstado ? "AND estado = ? " : "") +
                "ORDER BY fecha_ingreso DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            if (filtrarEstado) {
                stmt.setString(3, estado.toUpperCase());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al consultar reporte financiero (CU-06): " + e.getMessage());
        }
        return lista;
    }
}