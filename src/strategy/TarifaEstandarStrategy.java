package strategy;

import model.Tarifa;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Estrategia concreta que implementa el cobro estándar por tiempo transcurrido.
 *
 * Reglas de negocio aplicadas (RF-03):
 * 1. Si el tiempo total es menor o igual a los minutos de gracia, el cobro es $0.00.
 * 2. Si supera la tolerancia, se cobra como mínimo la primera hora completa.
 * 3. Después de la primera hora, cada bloque adicional de 15 minutos (o fracción)
 *    suma el costo de fracción configurado en la tarifa.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class TarifaEstandarStrategy implements CalculoTarifaStrategy {

    @Override
    public double calcularMonto(LocalDateTime fechaIngreso, LocalDateTime fechaSalida, Tarifa tarifa) {
        if (fechaIngreso == null || fechaSalida == null || tarifa == null) {
            throw new IllegalArgumentException("Los datos de entrada, salida y tarifa no pueden ser nulos.");
        }

        // Calcular minutos totales de estancia
        long minutosTotales = Duration.between(fechaIngreso, fechaSalida).toMinutes();

        // Evitar valores negativos por desfase de reloj
        if (minutosTotales < 0) {
            minutosTotales = 0;
        }

        // 1. Verificar periodo de tolerancia (minutos de gracia)
        if (minutosTotales <= tarifa.getMinutosGracia()) {
            return 0.0;
        }

        // 2. Si estuvo 60 minutos o menos (pero más que la tolerancia), se cobra 1 hora base
        if (minutosTotales <= 60) {
            return tarifa.getCostoHora();
        }

        // 3. Más de 1 hora: cobrar 1 hora base + fracciones de 15 minutos redondeadas hacia arriba
        long minutosExcedentes = minutosTotales - 60;
        long bloquesFraccion = (long) Math.ceil(minutosExcedentes / 15.0);

        return tarifa.getCostoHora() + (bloquesFraccion * tarifa.getCostoFraccion());
    }
}