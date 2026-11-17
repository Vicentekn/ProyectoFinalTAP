package model;
import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase de entidad que representa un Ticket de acceso y cobro vehicular.
 *
 * Pertenece a la capa Modelo (MVC). Utiliza propiedades observables de JavaFX
 * para permitir su enlace directo con las tablas (TableView) del módulo de Caseta
 * y de Reportes en tiempo real.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class Ticket {

    private final LongProperty id;
    private final StringProperty folio;
    private final StringProperty placa;
    private final StringProperty tipoVehiculo;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private final IntegerProperty cajonAsignado;
    private final DoubleProperty montoTotal;
    private final StringProperty estado;
    private Long usuarioIngresoId;
    private Long usuarioSalidaId;

    // Formateador para mostrar fechas legibles en las columnas de TableView
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Constructor por defecto. Inicializa las propiedades con valores base
     * y establece el estado inicial en 'EN_ESTACIONAMIENTO'.
     */
    public Ticket() {
        this(0L, "", "", "Automóvil", LocalDateTime.now(), 0, 0.0, "EN_ESTACIONAMIENTO");
    }

    /**
     * Constructor parametrizado para instanciar un ticket completo.
     *
     * @param id            Identificador único en base de datos.
     * @param folio         Código de seguimiento único del ticket (ej. TCK-20261117-001).
     * @param placa         Matrícula del vehículo ingresado.
     * @param tipoVehiculo  Categoría del vehículo (Automóvil, Motocicleta, Camioneta).
     * @param fechaIngreso  Marca de tiempo exacta de entrada.
     * @param cajonAsignado Número de espacio asignado dentro del estacionamiento.
     * @param montoTotal    Importe calculado al momento de la salida.
     * @param estado        Estado operativo (EN_ESTACIONAMIENTO, PAGADO, CANCELADO).
     */
    public Ticket(Long id, String folio, String placa, String tipoVehiculo,
                  LocalDateTime fechaIngreso, int cajonAsignado, double montoTotal, String estado) {
        this.id = new SimpleLongProperty(id);
        this.folio = new SimpleStringProperty(folio);
        this.placa = new SimpleStringProperty(placa);
        this.tipoVehiculo = new SimpleStringProperty(tipoVehiculo);
        this.fechaIngreso = fechaIngreso;
        this.cajonAsignado = new SimpleIntegerProperty(cajonAsignado);
        this.montoTotal = new SimpleDoubleProperty(montoTotal);
        this.estado = new SimpleStringProperty(estado);
    }

    // --- Propiedad calculada de ayuda para visualización en TableView ---

    /**
     * Devuelve la fecha de ingreso formateada como texto observable para columnas de JavaFX.
     * @return {@link StringProperty} con la fecha y hora formateadas.
     */
    public StringProperty fechaIngresoFormateadaProperty() {
        String texto = (fechaIngreso != null) ? fechaIngreso.format(FORMATTER) : "";
        return new SimpleStringProperty(texto);
    }

    // --- Getters, Setters y Propiedades Observables ---

    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getFolio() { return folio.get(); }
    public void setFolio(String folio) { this.folio.set(folio); }
    public StringProperty folioProperty() { return folio; }

    public String getPlaca() { return placa.get(); }
    public void setPlaca(String placa) { this.placa.set(placa); }
    public StringProperty placaProperty() { return placa; }

    public String getTipoVehiculo() { return tipoVehiculo.get(); }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo.set(tipoVehiculo); }
    public StringProperty tipoVehiculoProperty() { return tipoVehiculo; }

    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }

    public int getCajonAsignado() { return cajonAsignado.get(); }
    public void setCajonAsignado(int cajonAsignado) { this.cajonAsignado.set(cajonAsignado); }
    public IntegerProperty cajonAsignadoProperty() { return cajonAsignado; }

    public double getMontoTotal() { return montoTotal.get(); }
    public void setMontoTotal(double montoTotal) { this.montoTotal.set(montoTotal); }
    public DoubleProperty montoTotalProperty() { return montoTotal; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public StringProperty estadoProperty() { return estado; }

    public Long getUsuarioIngresoId() { return usuarioIngresoId; }
    public void setUsuarioIngresoId(Long usuarioIngresoId) { this.usuarioIngresoId = usuarioIngresoId; }

    public Long getUsuarioSalidaId() { return usuarioSalidaId; }
    public void setUsuarioSalidaId(Long usuarioSalidaId) { this.usuarioSalidaId = usuarioSalidaId; }
}