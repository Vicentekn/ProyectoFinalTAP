package controller;

import config.ConexionDB;
import dao.TicketDAOImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Ticket;
import model.Usuario;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para el módulo de Auditoría y Reportes Financieros (CU-06).
 *
 * Pertenece a la capa Controlador (MVC). Permite al rol AUDITOR consultar
 * historiales operativos por rango de fechas, calcular ingresos totales
 * y exportar reportes en formato CSV compatible con hojas de cálculo.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class AuditoriaController {

    @FXML private Label lblAuditorActivo;
    @FXML private Label lblEstado;

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<String> cmbEstadoFiltro;

    @FXML private Label lblIngresosTotales;
    @FXML private Label lblTotalPagados;
    @FXML private Label lblTotalRegistros;

    @FXML private TableView<Ticket> tblAuditoria;
    @FXML private TableColumn<Ticket, String> colFolio;
    @FXML private TableColumn<Ticket, String> colPlaca;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, Number> colCajon;
    @FXML private TableColumn<Ticket, String> colIngreso;
    @FXML private TableColumn<Ticket, String> colSalida;
    @FXML private TableColumn<Ticket, String> colMonto;
    @FXML private TableColumn<Ticket, String> colEstado;

    private TicketDAOImpl ticketDAO;
    private final ObservableList<Ticket> listaResultados = FXCollections.observableArrayList();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        try {
            ticketDAO = new TicketDAOImpl(ConexionDB.getConnection());
            configurarTabla();

            // Por defecto consulta los últimos 30 días hasta hoy
            dpFechaInicio.setValue(LocalDate.now().minusDays(30));
            dpFechaFin.setValue(LocalDate.now());

            cmbEstadoFiltro.setItems(FXCollections.observableArrayList(
                    "TODOS", "PAGADO", "EN_ESTACIONAMIENTO", "CANCELADO"));
            cmbEstadoFiltro.getSelectionModel().select("TODOS");

            handleConsultarReporte();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", e.getMessage());
        }
    }

    public void setUsuarioActual(Usuario usuario) {
        if (usuario != null) {
            lblAuditorActivo.setText("Auditor en sesión: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        }
    }

    private void configurarTabla() {
        colFolio.setCellValueFactory(c -> c.getValue().folioProperty());
        colPlaca.setCellValueFactory(c -> c.getValue().placaProperty());
        colTipo.setCellValueFactory(c -> c.getValue().tipoVehiculoProperty());
        colCajon.setCellValueFactory(c -> c.getValue().cajonAsignadoProperty());
        colIngreso.setCellValueFactory(c -> c.getValue().fechaIngresoFormateadaProperty());

        colSalida.setCellValueFactory(c -> {
            LocalDateTime sal = c.getValue().getFechaSalida();
            return new SimpleStringProperty(sal != null ? sal.format(FMT) : "En curso");
        });

        colMonto.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("$%.2f", c.getValue().getMontoTotal())));

        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        tblAuditoria.setItems(listaResultados);
    }

    /**
     * Ejecuta la consulta filtrada por fechas y recalcula los indicadores financieros (CU-06).
     */
    @FXML
    private void handleConsultarReporte() {
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();

        if (inicio == null || fin == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fechas Requeridas", "Seleccione ambas fechas para realizar el corte.");
            return;
        }
        if (inicio.isAfter(fin)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Rango Inválido", "La fecha inicial no puede ser posterior a la fecha final.");
            return;
        }

        LocalDateTime fechaHoraInicio = inicio.atStartOfDay();
        LocalDateTime fechaHoraFin = fin.atTime(LocalTime.MAX);
        String estadoSeleccionado = cmbEstadoFiltro.getValue();

        List<Ticket> resultados = ticketDAO.buscarPorRangoFechas(fechaHoraInicio, fechaHoraFin, estadoSeleccionado);
        listaResultados.setAll(resultados);

        // Calcular KPIs financieros
        double ingresosTotales = 0.0;
        int ticketsPagados = 0;

        for (Ticket t : resultados) {
            if ("PAGADO".equalsIgnoreCase(t.getEstado())) {
                ingresosTotales += t.getMontoTotal();
                ticketsPagados++;
            }
        }

        lblIngresosTotales.setText(String.format("$%.2f MXN", ingresosTotales));
        lblTotalPagados.setText(String.valueOf(ticketsPagados));
        lblTotalRegistros.setText(String.valueOf(resultados.size()));
        lblEstado.setText("Consulta completada: " + resultados.size() + " registros encontrados.");
    }

    /**
     * Exporta los resultados visibles en la tabla a un archivo CSV (Excel) mediante FileChooser.
     */
    @FXML
    private void handleExportarCSV() {
        if (listaResultados.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Datos", "No hay registros en la tabla para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Auditoría");
        fileChooser.setInitialFileName("Reporte_Estacionamiento_" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV (*.csv)", "*.csv"));

        File archivo = fileChooser.showSaveDialog(tblAuditoria.getScene().getWindow());
        if (archivo != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                writer.println("FOLIO,PLACA,TIPO_VEHICULO,CAJON,FECHA_INGRESO,FECHA_SALIDA,MONTO_MXN,ESTADO");
                for (Ticket t : listaResultados) {
                    String fIng = (t.getFechaIngreso() != null) ? t.getFechaIngreso().format(FMT) : "";
                    String fSal = (t.getFechaSalida() != null) ? t.getFechaSalida().format(FMT) : "EN_CURSO";
                    writer.printf("%s,%s,%s,%d,%s,%s,%.2f,%s%n",
                            t.getFolio(), t.getPlaca(), t.getTipoVehiculo(),
                            t.getCajonAsignado(), fIng, fSal, t.getMontoTotal(), t.getEstado());
                }
                lblEstado.setText("Reporte exportado exitosamente en: " + archivo.getAbsolutePath());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación Exitosa",
                        "El reporte financiero se guardó en:\n" + archivo.getAbsolutePath());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Exportar", "No se pudo escribir el archivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblAuditoria.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Administración de Estacionamiento - Login");
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
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