package factory;

import model.Ticket;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Fábrica encargada de construir y formatear los distintos tipos de comprobantes
 * emitidos en la caseta del estacionamiento (CU-04).
 *
 * PATRÓN DE DISEÑO IMPLEMENTADO: Factory Method.
 * Justificación: Centraliza y encapsula la lógica de instanciación y estructuración
 * visual de los documentos de entrada y salida, permitiendo agregar nuevos formatos
 * (ej. comprobante fiscal o pase de cortesía) sin modificar los controladores.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class ComprobanteFactory {

    // Constantes para definir los productos que puede fabricar la clase
    public static final String TIPO_ENTRADA = "ENTRADA";
    public static final String TIPO_SALIDA = "SALIDA";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Método fábrica (Factory Method) que produce el texto estructurado del comprobante
     * según la operación solicitada.
     *
     * @param ticket          Entidad {@link Ticket} con los datos del vehículo y tiempos.
     * @param tipoComprobante Tipo de documento a fabricar ({@code TIPO_ENTRADA} o {@code TIPO_SALIDA}).
     * @return Cadena formateada lista para visualizarse en pantalla o enviarse a impresora térmica.
     */
    public static String crearComprobante(Ticket ticket, String tipoComprobante) {
        if (ticket == null || tipoComprobante == null) {
            throw new IllegalArgumentException("El ticket y el tipo de comprobante no pueden ser nulos.");
        }

        switch (tipoComprobante.toUpperCase()) {
            case TIPO_ENTRADA:
                return construirTicketEntrada(ticket);

            case TIPO_SALIDA:
                return construirReciboSalida(ticket);

            default:
                throw new IllegalArgumentException("Tipo de comprobante no soportado: " + tipoComprobante);
        }
    }

    /**
     * Construye el formato específico del boleto entregado al conductor al ingresar (CU-01 / CU-04).
     */
    private static String construirTicketEntrada(Ticket ticket) {
        String fechaIng = (ticket.getFechaIngreso() != null) ? ticket.getFechaIngreso().format(FMT) : "N/A";

        return "==========================================\n" +
                "     ESTACIONAMIENTO CENTRAL CELAYA       \n" +
                "           TICKET DE INGRESO              \n" +
                "==========================================\n" +
                "FOLIO:         " + ticket.getFolio() + "\n" +
                "PLACA:         " + ticket.getPlaca() + "\n" +
                "VEHÍCULO:      " + ticket.getTipoVehiculo() + "\n" +
                "CAJÓN ASIG.:   #" + ticket.getCajonAsignado() + "\n" +
                "HORA INGRESO:  " + fechaIng + "\n" +
                "------------------------------------------\n" +
                "AVISO IMPORTANTE:\n" +
                "- Conserve este boleto para su salida.\n" +
                "- Tolerancia máxima de 15 minutos.\n" +
                "- El extravío genera multa reglamentaria.\n" +
                "==========================================";
    }

    /**
     * Construye el formato específico del recibo de pago emitido al liquidar la estancia (CU-03 / CU-04).
     */
    private static String construirReciboSalida(Ticket ticket) {
        String fechaIng = (ticket.getFechaIngreso() != null) ? ticket.getFechaIngreso().format(FMT) : "N/A";
        String fechaSal = (ticket.getFechaSalida() != null) ? ticket.getFechaSalida().format(FMT) : "N/A";

        long minutosTotales = 0;
        if (ticket.getFechaIngreso() != null && ticket.getFechaSalida() != null) {
            minutosTotales = Math.max(0, Duration.between(ticket.getFechaIngreso(), ticket.getFechaSalida()).toMinutes());
        }
        long horas = minutosTotales / 60;
        long minutos = minutosTotales % 60;

        return "==========================================\n" +
                "     ESTACIONAMIENTO CENTRAL CELAYA       \n" +
                "         COMPROBANTE DE PAGO              \n" +
                "==========================================\n" +
                "FOLIO:         " + ticket.getFolio() + "\n" +
                "PLACA:         " + ticket.getPlaca() + "\n" +
                "VEHÍCULO:      " + ticket.getTipoVehiculo() + "\n" +
                "CAJÓN LIBERADO:#" + ticket.getCajonAsignado() + "\n" +
                "------------------------------------------\n" +
                "INGRESO:       " + fechaIng + "\n" +
                "SALIDA:        " + fechaSal + "\n" +
                "ESTANCIA:      " + horas + " hrs " + minutos + " min\n" +
                "------------------------------------------\n" +
                "TOTAL PAGADO:  $" + String.format("%.2f", ticket.getMontoTotal()) + " MXN\n" +
                "ESTADO:        " + ticket.getEstado() + "\n" +
                "==========================================\n" +
                "       ¡GRACIAS POR SU PREFERENCIA!       \n" +
                "==========================================";
    }
}