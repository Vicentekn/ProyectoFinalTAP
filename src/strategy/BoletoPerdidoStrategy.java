package strategy;

import model.Tarifa;
import java.time.LocalDateTime;

/**
 * Estrategia concreta para el cobro por extravío de ticket de acceso.
 *
 * Regla de negocio aplicada (RF-06): Ignora el tiempo transcurrido y aplica
 * directamente la cuota fija de penalización configurada por el Administrador
 * para el tipo de vehículo correspondiente.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class BoletoPerdidoStrategy implements CalculoTarifaStrategy {

    @Override
    public double calcularMonto(LocalDateTime fechaIngreso, LocalDateTime fechaSalida, Tarifa tarifa) {
        if (tarifa == null) {
            throw new IllegalArgumentException("La tarifa no puede ser nula.");
        }
        return tarifa.getCostoBoletoPerdido();
    }
}