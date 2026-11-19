package dao;
import model.Tarifa;
import java.util.List;

/**
 * Interfaz que define las operaciones de acceso a datos para las tarifas del estacionamiento.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public interface TarifaDAO {

    /**
     * Busca la tarifa vigente correspondiente a una categoría de vehículo.
     *
     * @param tipoVehiculo Nombre de la categoría (ej. "Automóvil", "Motocicleta", "Camioneta").
     * @return Objeto {@link Tarifa} activo, o {@code null} si no existe configuración.
     */
    Tarifa buscarPorTipoVehiculo(String tipoVehiculo);

    /**
     * Obtiene únicamente las tarifas que se encuentran marcadas como activas en el sistema.
     *
     * @return Lista de tarifas vigentes.
     */
    List<Tarifa> listarActivas();
}