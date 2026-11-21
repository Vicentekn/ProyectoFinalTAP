package controller;

import config.ConexionDB;
import dao.TarifaDAOImpl;
import dao.TicketDAOImpl;
import model.Tarifa;
import model.Ticket;
import model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;

/**
 * Controlador principal para la operación diaria en Caseta.
 *
 * Pertenece a la capa Controlador (MVC). Gestiona el registro de entradas (CU-01),
 * la consulta de ocupación en tiempo real (CU-07 / RF-04) y prepara el flujo
 * de salida y cobro (CU-02 / CU-03).
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class CasetaController {

    // Capacidad física total del recinto (RF-04)
    private static final int CAPACIDAD_MAXIMA = 50;

    @FXML private Label lblOperadorActivo;
    @FXML private Label lblDisponibles;
    @FXML private Label lblOcupados;
    @FXML private Label lblPorcentaje;
    @FXML private Label lblEstado;

    @FXML private TextField txtPlacaEntrada;
    @FXML private ComboBox<Tarifa> cmbTipoVehiculo;
    @FXML private Button btnRegistrarEntrada;

    @FXML private TextField txtFolioSalida;
    @FXML private CheckBox chkBoletoPerdido;
    @FXML private Button btnProcesarSalida;

    @FXML private TableView<Ticket> tblTicketsActivos;
    @FXML private TableColumn<Ticket, String> colFolio;
    @FXML private TableColumn<Ticket, String> colPlaca;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, Number> colCajon;
    @FXML private TableColumn<Ticket, String> colIngreso;

    private Usuario usuarioActual;
    private TicketDAOImpl ticketDAO;
    private TarifaDAOImpl tarifaDAO;
    private final ObservableList<Ticket> listaTicketsActivos = FXCollections.observableArrayList();

    /**
     * Inicializa los componentes visuales, enlaza las columnas de la tabla
     * y carga los catálogos iniciales desde MySQL.
     */
    @FXML
    public void initialize() {
        try {
            ticketDAO = new TicketDAOImpl(ConexionDB.getConnection());
            tarifaDAO = new TarifaDAOImpl(ConexionDB.getConnection());

            configurarTabla();
            cargarTiposVehiculo();
            actualizarDatos();

            // Al hacer clic en una fila de la tabla, copia automáticamente el folio al campo de salida
            tblTicketsActivos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    txtFolioSalida.setText(newVal.getFolio());
                }
            });

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Inicialización",
                    "No se pudo conectar con la base de datos: " + e.getMessage());
        }
    }

    /**
     * Recibe la sesión del usuario autenticado desde el LoginController.
     * @param usuario Objeto {@link Usuario} con rol OPERADOR o ADMINISTRADOR.
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        if (usuario != null) {
            lblOperadorActivo.setText("Operador en turno: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        }
    }

    /**
     * Vincula las columnas del TableView con las JavaFX Properties del modelo {@link Ticket}.
     */
    private void configurarTabla() {
        colFolio.setCellValueFactory(cell -> cell.getValue().folioProperty());
        colPlaca.setCellValueFactory(cell -> cell.getValue().placaProperty());
        colTipo.setCellValueFactory(cell -> cell.getValue().tipoVehiculoProperty());
        colCajon.setCellValueFactory(cell -> cell.getValue().cajonAsignadoProperty());
        colIngreso.setCellValueFactory(cell -> cell.getValue().fechaIngresoFormateadaProperty());
        tblTicketsActivos.setItems(listaTicketsActivos);
    }

    /**
     * Llena el ComboBox con las tarifas vigentes configuradas en la base de datos.
     */
    private void cargarTiposVehiculo() {
        ObservableList<Tarifa> tarifas = FXCollections.observableArrayList(tarifaDAO.listarActivas());
        cmbTipoVehiculo.setItems(tarifas);
        if (!tarifas.isEmpty()) {
            cmbTipoVehiculo.getSelectionModel().selectFirst();
        }
    }

    /**
     * Ejecuta el caso de uso CU-01 (Registrar Entrada) y valida la capacidad (RF-04).
     */
    @FXML
    private void handleRegistrarEntrada() {
        String placa = txtPlacaEntrada.getText().trim().toUpperCase();
        Tarifa tarifaSeleccionada = cmbTipoVehiculo.getValue();

        if (placa.isEmpty() || tarifaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos",
                    "Ingrese la placa del vehículo y seleccione su categoría.");
            return;
        }

        // Validar que el vehículo no tenga ya un ticket abierto adentro
        if (ticketDAO.buscarActivoPorFolioOPlaca(placa) != null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Vehículo Duplicado",
                    "La placa " + placa + " ya cuenta con un ingreso activo en el estacionamiento.");
            return;
        }

        // Validar disponibilidad de cajones (RF-04)
        int cajonLibre = ticketDAO.obtenerSiguienteCajonDisponible(CAPACIDAD_MAXIMA);
        if (cajonLibre == -1) {
            mostrarAlerta(Alert.AlertType.ERROR, "Estacionamiento Lleno",
                    "No hay cajones disponibles. Se ha alcanzado la capacidad máxima (" + CAPACIDAD_MAXIMA + ").");
            return;
        }

        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setFolio(ticketDAO.generarNuevoFolio());
        nuevoTicket.setPlaca(placa);
        nuevoTicket.setTipoVehiculo(tarifaSeleccionada.getTipoVehiculo());
        nuevoTicket.setFechaIngreso(LocalDateTime.now());
        nuevoTicket.setCajonAsignado(cajonLibre);
        if (usuarioActual != null) {
            nuevoTicket.setUsuarioIngresoId(usuarioActual.getId());
        }

        if (ticketDAO.save(nuevoTicket)) {
            txtPlacaEntrada.clear();
            actualizarDatos();
            lblEstado.setText("Entrada registrada: Placa " + placa + " | Folio: " + nuevoTicket.getFolio() + " | Cajón #" + cajonLibre);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Ticket Generado (CU-01)",
                    "Folio: " + nuevoTicket.getFolio() + "\nPlaca: " + placa + "\nCajón Asignado: #" + cajonLibre);
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "No se pudo guardar el ticket en la base de datos.");
        }
    }

    /**
     * Acción de salida y cobro (se completará con Strategy y Factory Method en los Días 11 y 12).
     */
    @FXML
    private void handleProcesarSalida() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Módulo en Construcción",
                "El procesamiento completo de cobro con Strategy y Factory se integrará en los Días 11 y 12.");
    }

    /**
     * Refresca la tabla de vehículos activos y recalcula los indicadores de ocupación (CU-07).
     */
    @FXML
    public void actualizarDatos() {
        listaTicketsActivos.setAll(ticketDAO.listarEnEstacionamiento());

        int ocupados = ticketDAO.contarOcupados();
        int disponibles = Math.max(0, CAPACIDAD_MAXIMA - ocupados);
        double porcentaje = (ocupados * 100.0) / CAPACIDAD_MAXIMA;

        lblOcupados.setText(String.valueOf(ocupados));
        lblDisponibles.setText(String.valueOf(disponibles));
        lblPorcentaje.setText(String.format("%.0f%%", porcentaje));

        // Bloquear botón de entrada automáticamente si está lleno (RF-04)
        btnRegistrarEntrada.setDisable(disponibles == 0);
    }

    /**
     * Cierra la sesión actual y regresa a la pantalla de Login.
     */
    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegistrarEntrada.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Administración de Estacionamiento - Login");
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al regresar al Login: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}