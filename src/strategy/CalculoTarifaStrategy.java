package strategy;

import model.Tarifa;
import java.time.LocalDateTime;

/**
 * Interfaz base para el cálculo dinámico de tarifas de estacionamiento.
 *
 * PATRÓN DE DISEÑO IMPLEMENTADO: Strategy.
 * Justificación: Permite encapsular distintas reglas de cálculo de cobro
 * (tarifa estándar por tiempo, tarifa por boleto perdido, tarifas especiales)
 * en clases independientes e intercambiables sin modificar el controlador de caseta.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public interface CalculoTarifaStrategy {

    /**
     * Calcula el importe total a pagar por la estancia de un vehículo.
     *
     * @param fechaIngreso Marca de tiempo en que el vehículo ingresó al estacionamiento.
     * @param fechaSalida  Marca de tiempo en que el vehículo solicita su salida.
     * @param tarifa       Configuración de costos vigente para el tipo de vehículo.
     * @return Monto monetario total a cobrar en pesos.
     */
    double calcularMonto(LocalDateTime fechaIngreso, LocalDateTime fechaSalida, Tarifa tarifa);
}