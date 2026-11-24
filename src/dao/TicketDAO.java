package dao;

import model.Ticket;
import java.util.List;

/**
 * Interfaz que define las operaciones operativas y de consulta para los tickets
 * de acceso vehicular en el estacionamiento.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public interface TicketDAO {

    /**
     * Busca un ticket específico mediante su folio único o número de placa activa.
     *
     * @param folioOPlaca Folio del ticket (ej. "TCK-...") o placa del vehículo.
     * @return Objeto {@link Ticket} encontrado, o {@code null} si no existe.
     */
    Ticket buscarActivoPorFolioOPlaca(String folioOPlaca);

    /**
     * Registra el pago y la salida definitiva de un vehículo en base de datos.
     *
     * @param ticket Objeto {@link Ticket} con fechaSalida, montoTotal y usuarioSalidaId asignados.
     * @return {@code true} si la actualización fue exitosa.
     */
    boolean registrarSalida(Ticket ticket);

    /**
     * Obtiene todos los vehículos que se encuentran actualmente dentro del estacionamiento.
     *
     * @return Lista de tickets con estado 'EN_ESTACIONAMIENTO'.
     */
    List<Ticket> listarEnEstacionamiento();

    /**
     * Calcula el número total de cajones ocupados en este instante (RF-04 / CU-07).
     *
     * @return Cantidad entera de vehículos activos dentro del recinto.
     */
    int contarOcupados();

    /**
     * Determina el primer número de cajón disponible entre 1 y la capacidad máxima.
     *
     * @param capacidadMaxima Total de cajones físicos del estacionamiento.
     * @return Número del cajón libre asignable, o -1 si el estacionamiento está lleno.
     */
    int obtenerSiguienteCajonDisponible(int capacidadMaxima);

    /**
     * Consulta los tickets registrados dentro de un rango de fechas y estado específico (CU-06).
     *
     * @param inicio Fecha y hora inicial del corte.
     * @param fin    Fecha y hora final del corte.
     * @param estado Filtro de estado ("TODOS", "PAGADO", "EN_ESTACIONAMIENTO", "CANCELADO").
     * @return Lista de tickets que cumplen con los criterios de auditoría.
     */
    List<Ticket> buscarPorRangoFechas(java.time.LocalDateTime inicio, java.time.LocalDateTime fin, String estado);
}