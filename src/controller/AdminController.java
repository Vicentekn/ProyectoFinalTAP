package controller;

import config.ConexionDB;
import dao.TarifaDAOImpl;
import dao.UsuarioDAOImpl;
import model.Tarifa;
import model.Usuario;
import security.HashUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador para el Panel de Administración del sistema.
 *
 * Pertenece a la capa Controlador (MVC). Permite al rol ADMINISTRADOR
 * configurar y actualizar tarifas (CU-05) y gestionar las cuentas y roles
 * del personal aplicando cifrado SHA-1 (RNF-02).
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class AdminController {

    @FXML private Label lblAdminActivo;
    @FXML private Label lblEstado;

    // Controles Pestaña Tarifas
    @FXML private TextField txtTipoVehiculo;
    @FXML private TextField txtCostoHora;
    @FXML private TextField txtCostoFraccion;
    @FXML private TextField txtMinutosGracia;
    @FXML private TextField txtBoletoPerdido;
    @FXML private CheckBox chkTarifaActiva;

    @FXML private TableView<Tarifa> tblTarifas;
    @FXML private TableColumn<Tarifa, Number> colTarifaId;
    @FXML private TableColumn<Tarifa, String> colTarifaTipo;
    @FXML private TableColumn<Tarifa, Number> colTarifaHora;
    @FXML private TableColumn<Tarifa, Number> colTarifaFraccion;
    @FXML private TableColumn<Tarifa, Number> colTarifaGracia;
    @FXML private TableColumn<Tarifa, Number> colTarifaPerdido;
    @FXML private TableColumn<Tarifa, Boolean> colTarifaActivo;

    // Controles Pestaña Usuarios
    @FXML private TextField txtNombreUsuario;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPasswordUsuario;
    @FXML private ComboBox<String> cmbRolUsuario;
    @FXML private CheckBox chkUsuarioActivo;

    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Number> colUsrId;
    @FXML private TableColumn<Usuario, String> colUsrNombre;
    @FXML private TableColumn<Usuario, String> colUsrUsername;
    @FXML private TableColumn<Usuario, String> colUsrRol;
    @FXML private TableColumn<Usuario, Boolean> colUsrActivo;

    private Usuario adminActual;
    private TarifaDAOImpl tarifaDAO;
    private UsuarioDAOImpl usuarioDAO;

    private Tarifa tarifaSeleccionada;
    private Usuario usuarioSeleccionado;

    private final ObservableList<Tarifa> listaTarifas = FXCollections.observableArrayList();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            tarifaDAO = new TarifaDAOImpl(ConexionDB.getConnection());
            usuarioDAO = new UsuarioDAOImpl(ConexionDB.getConnection());

            configurarTablas();
            cmbRolUsuario.setItems(FXCollections.observableArrayList("ADMINISTRADOR", "OPERADOR", "AUDITOR"));
            cmbRolUsuario.getSelectionModel().select("OPERADOR");

            cargarDatos();

            // Listener para cargar tarifa seleccionada en el formulario
            tblTarifas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    tarifaSeleccionada = newVal;
                    txtTipoVehiculo.setText(newVal.getTipoVehiculo());
                    txtCostoHora.setText(String.valueOf(newVal.getCostoHora()));
                    txtCostoFraccion.setText(String.valueOf(newVal.getCostoFraccion()));
                    txtMinutosGracia.setText(String.valueOf(newVal.getMinutosGracia()));
                    txtBoletoPerdido.setText(String.valueOf(newVal.getCostoBoletoPerdido()));
                    chkTarifaActiva.setSelected(newVal.isActivo());
                }
            });

            // Listener para cargar usuario seleccionado en el formulario
            tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    usuarioSeleccionado = newVal;
                    txtNombreUsuario.setText(newVal.getNombre());
                    txtUsername.setText(newVal.getUsername());
                    txtPasswordUsuario.clear();
                    cmbRolUsuario.setValue(newVal.getRol());
                    chkUsuarioActivo.setSelected(newVal.isActivo());
                }
            });

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", e.getMessage());
        }
    }

    public void setUsuarioActual(Usuario usuario) {
        this.adminActual = usuario;
        if (usuario != null) {
            lblAdminActivo.setText("Administrador: " + usuario.getNombre());
        }
    }

    private void configurarTablas() {
        colTarifaId.setCellValueFactory(c -> c.getValue().idProperty());
        colTarifaTipo.setCellValueFactory(c -> c.getValue().tipoVehiculoProperty());
        colTarifaHora.setCellValueFactory(c -> c.getValue().costoHoraProperty());
        colTarifaFraccion.setCellValueFactory(c -> c.getValue().costoFraccionProperty());
        colTarifaGracia.setCellValueFactory(c -> c.getValue().minutosGraciaProperty());
        colTarifaPerdido.setCellValueFactory(c -> c.getValue().costoBoletoPerdidoProperty());
        colTarifaActivo.setCellValueFactory(c -> c.getValue().activoProperty());
        tblTarifas.setItems(listaTarifas);

        colUsrId.setCellValueFactory(c -> c.getValue().idProperty());
        colUsrNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colUsrUsername.setCellValueFactory(c -> c.getValue().usernameProperty());
        colUsrRol.setCellValueFactory(c -> c.getValue().rolProperty());
        colUsrActivo.setCellValueFactory(c -> c.getValue().activoProperty());
        tblUsuarios.setItems(listaUsuarios);
    }

    private void cargarDatos() {
        listaTarifas.setAll(tarifaDAO.findAll());
        listaUsuarios.setAll(usuarioDAO.findAll());
    }

    // --- GESTIÓN DE TARIFAS (CU-05) ---

    @FXML
    private void handleGuardarTarifa() {
        try {
            String tipo = txtTipoVehiculo.getText().trim();
            double costoHora = Double.parseDouble(txtCostoHora.getText().trim());
            double costoFraccion = Double.parseDouble(txtCostoFraccion.getText().trim());
            int gracia = Integer.parseInt(txtMinutosGracia.getText().trim());
            double boletoPerdido = Double.parseDouble(txtBoletoPerdido.getText().trim());
            boolean activa = chkTarifaActiva.isSelected();

            if (tipo.isEmpty() || costoHora < 0 || costoFraccion < 0 || gracia < 0 || boletoPerdido < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Datos Inválidos", "Verifique que los campos no estén vacíos ni tengan valores negativos.");
                return;
            }

            if (tarifaSeleccionada == null) {
                Tarifa nueva = new Tarifa(0L, tipo, costoHora, costoFraccion, gracia, boletoPerdido, activa);
                if (tarifaDAO.save(nueva)) {
                    lblEstado.setText("Nueva tarifa registrada: " + tipo);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Tarifa Creada", "La tarifa para " + tipo + " se guardó correctamente.");
                }
            } else {
                tarifaSeleccionada.setTipoVehiculo(tipo);
                tarifaSeleccionada.setCostoHora(costoHora);
                tarifaSeleccionada.setCostoFraccion(costoFraccion);
                tarifaSeleccionada.setMinutosGracia(gracia);
                tarifaSeleccionada.setCostoBoletoPerdido(boletoPerdido);
                tarifaSeleccionada.setActivo(activa);

                if (tarifaDAO.update(tarifaSeleccionada)) {
                    lblEstado.setText("Tarifa actualizada: " + tipo);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Tarifa Actualizada", "Los cambios en la tarifa de " + tipo + " ya están vigentes en caseta.");
                }
            }

            limpiarFormularioTarifa();
            cargarDatos();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato Numérico Incorrecto",
                    "Los campos de costos y minutos deben contener números válidos.");
        }
    }

    @FXML
    private void limpiarFormularioTarifa() {
        tarifaSeleccionada = null;
        txtTipoVehiculo.clear();
        txtCostoHora.clear();
        txtCostoFraccion.clear();
        txtMinutosGracia.clear();
        txtBoletoPerdido.clear();
        chkTarifaActiva.setSelected(true);
        tblTarifas.getSelectionModel().clearSelection();
    }

    // --- GESTIÓN DE USUARIOS (RNF-02) ---

    @FXML
    private void handleGuardarUsuario() {
        String nombre = txtNombreUsuario.getText().trim();
        String username = txtUsername.getText().trim();
        String passwordPlano = txtPasswordUsuario.getText().trim();
        String rol = cmbRolUsuario.getValue();
        boolean activo = chkUsuarioActivo.isSelected();

        if (nombre.isEmpty() || username.isEmpty() || rol == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Obligatorios", "Complete nombre, usuario y rol.");
            return;
        }

        if (usuarioSeleccionado == null) {
            if (passwordPlano.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Contraseña Requerida", "Debe asignar una contraseña para el nuevo usuario.");
                return;
            }
            Usuario nuevo = new Usuario();
            nuevo.setNombre(nombre);
            nuevo.setUsername(username);
            nuevo.setPassword(passwordPlano); // UsuarioDAOImpl.save() aplicará HashUtil.sha1()
            nuevo.setRol(rol);
            nuevo.setActivo(activo);

            if (usuarioDAO.save(nuevo)) {
                lblEstado.setText("Usuario creado con cifrado SHA-1: " + username);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario Registrado", "Cuenta creada exitosamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el usuario (verifique que el username no esté duplicado).");
            }
        } else {
            usuarioSeleccionado.setNombre(nombre);
            usuarioSeleccionado.setUsername(username);
            usuarioSeleccionado.setRol(rol);
            usuarioSeleccionado.setActivo(activo);

            // Solo recalcula el hash SHA-1 si el administrador escribió una nueva contraseña
            if (!passwordPlano.isEmpty()) {
                usuarioSeleccionado.setPassword(HashUtil.sha1(passwordPlano));
            }

            if (usuarioDAO.update(usuarioSeleccionado)) {
                lblEstado.setText("Usuario actualizado: " + username);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario Actualizado", "Los datos de la cuenta se actualizaron.");
            }
        }

        limpiarFormularioUsuario();
        cargarDatos();
    }

    @FXML
    private void limpiarFormularioUsuario() {
        usuarioSeleccionado = null;
        txtNombreUsuario.clear();
        txtUsername.clear();
        txtPasswordUsuario.clear();
        cmbRolUsuario.getSelectionModel().select("OPERADOR");
        chkUsuarioActivo.setSelected(true);
        tblUsuarios.getSelectionModel().clearSelection();
    }

    // --- NAVEGACIÓN ---

    @FXML
    private void handleAbrirCaseta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/caseta_view.fxml"));
            Parent root = loader.load();
            CasetaController controller = loader.getController();
            controller.setUsuarioActual(adminActual);

            Stage stage = (Stage) lblAdminActivo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Estacionamiento - Módulo de Caseta (Modo Admin)");
            stage.centerOnScreen();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de caseta: " + e.getMessage());
        }
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblAdminActivo.getScene().getWindow();
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