package model;

import javafx.beans.property.*;

/**
 * Clase de entidad que representa la configuración de tarifas por tipo de vehículo.
 *
 * Pertenece a la capa Modelo (MVC). Implementa JavaFX Properties para facilitar
 * su edición en tablas y formularios del Panel de Administración (CU-05) y sirve
 * como entrada de datos para las estrategias de cobro (Patrón Strategy).
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class Tarifa {

    private final LongProperty id;
    private final StringProperty tipoVehiculo;
    private final DoubleProperty costoHora;
    private final DoubleProperty costoFraccion;
    private final IntegerProperty minutosGracia;
    private final DoubleProperty costoBoletoPerdido;
    private final BooleanProperty activo;

    /**
     * Constructor por defecto con valores estándar de inicialización.
     */
    public Tarifa() {
        this(0L, "", 0.0, 0.0, 15, 150.0, true);
    }

    /**
     * Constructor parametrizado para instanciar una tarifa completa.
     *
     * @param id                 Identificador único en base de datos.
     * @param tipoVehiculo       Categoría de vehículo (Automóvil, Motocicleta, Camioneta).
     * @param costoHora          Tarifa base por cada hora completa de estancia.
     * @param costoFraccion      Tarifa aplicada por cada fracción adicional (ej. 15 min).
     * @param minutosGracia      Tiempo de tolerancia sin costo al ingresar.
     * @param costoBoletoPerdido Multa fija aplicable en caso de extravío del comprobante.
     * @param activo             Indica si la tarifa está vigente en el sistema.
     */
    public Tarifa(Long id, String tipoVehiculo, double costoHora, double costoFraccion,
                  int minutosGracia, double costoBoletoPerdido, boolean activo) {
        this.id = new SimpleLongProperty(id);
        this.tipoVehiculo = new SimpleStringProperty(tipoVehiculo);
        this.costoHora = new SimpleDoubleProperty(costoHora);
        this.costoFraccion = new SimpleDoubleProperty(costoFraccion);
        this.minutosGracia = new SimpleIntegerProperty(minutosGracia);
        this.costoBoletoPerdido = new SimpleDoubleProperty(costoBoletoPerdido);
        this.activo = new SimpleBooleanProperty(activo);
    }

    // --- Getters, Setters y Propiedades Observables ---

    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getTipoVehiculo() { return tipoVehiculo.get(); }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo.set(tipoVehiculo); }
    public StringProperty tipoVehiculoProperty() { return tipoVehiculo; }

    public double getCostoHora() { return costoHora.get(); }
    public void setCostoHora(double costoHora) { this.costoHora.set(costoHora); }
    public DoubleProperty costoHoraProperty() { return costoHora; }

    public double getCostoFraccion() { return costoFraccion.get(); }
    public void setCostoFraccion(double costoFraccion) { this.costoFraccion.set(costoFraccion); }
    public DoubleProperty costoFraccionProperty() { return costoFraccion; }

    public int getMinutosGracia() { return minutosGracia.get(); }
    public void setMinutosGracia(int minutosGracia) { this.minutosGracia.set(minutosGracia); }
    public IntegerProperty minutosGraciaProperty() { return minutosGracia; }

    public double getCostoBoletoPerdido() { return costoBoletoPerdido.get(); }
    public void setCostoBoletoPerdido(double costoBoletoPerdido) { this.costoBoletoPerdido.set(costoBoletoPerdido); }
    public DoubleProperty costoBoletoPerdidoProperty() { return costoBoletoPerdido; }

    public boolean isActivo() { return activo.get(); }
    public void setActivo(boolean activo) { this.activo.set(activo); }
    public BooleanProperty activoProperty() { return activo; }

    /**
     * Permite que el objeto Tarifa se muestre de forma legible dentro de un ComboBox de JavaFX.
     */
    @Override
    public String toString() {
        return getTipoVehiculo() + " ($" + String.format("%.2f", getCostoHora()) + "/hr)";
    }
}