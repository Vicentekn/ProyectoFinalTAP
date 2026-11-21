package controller;

import config.ConexionDB;
import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador encargado de gestionar la autenticación de usuarios en la vista de Login.
 *
 * Pertenece a la capa Controlador (MVC). Recibe las entradas de la interfaz gráfica,
 * delega la validación criptográfica (SHA-1) a la capa DAO y gestiona la redirección
 * basada en el rol del usuario autenticado (ADMINISTRADOR, OPERADOR, AUDITOR).
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensajeError;
    @FXML private Button btnIngresar;

    /**
     * Inicializa el estado visual de la vista limpiando mensajes previos.
     */
    @FXML
    public void initialize() {
        lblMensajeError.setText("");
    }

    /**
     * Maneja el evento de clic en el botón "Iniciar Sesión" o la tecla Enter en el campo de contraseña.
     * Valida campos, consulta la base de datos mediante {@link UsuarioDAO} y redirige según el rol.
     */
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // 1. Validación de campos vacíos en la interfaz
        if (username.isEmpty() || password.isEmpty()) {
            lblMensajeError.setText("Por favor, complete todos los campos.");
            return;
        }

        try {
            // 2. Obtener conexión Singleton e instanciar el DAO de Usuario
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(ConexionDB.getConnection());

            // 3. Autenticar credenciales (la comparación SHA-1 ocurre dentro del DAO)
            Usuario usuarioAutenticado = usuarioDAO.autenticar(username, password);

            if (usuarioAutenticado != null) {
                lblMensajeError.setText("");
                redirigirSegunRol(usuarioAutenticado);
            } else {
                lblMensajeError.setText("Usuario o contraseña incorrectos, o cuenta inactiva.");
                txtPassword.clear();
            }

        } catch (Exception e) {
            lblMensajeError.setText("Error de conexión con la base de datos.");
            System.err.println("Excepción durante el inicio de sesión: " + e.getMessage());
        }
    }

    /**
     * Evalúa el rol del usuario autenticado y carga el módulo correspondiente del sistema.
     *
     * @param usuario Objeto {@link Usuario} con los datos recuperados de MySQL.
     */
    private void redirigirSegunRol(Usuario usuario) {
        String rol = usuario.getRol().toUpperCase();

        try {
            Stage stageActual = (Stage) btnIngresar.getScene().getWindow();

            switch (rol) {
                case "OPERADOR":
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/caseta_view.fxml"));
                    Parent root = loader.load();

                    CasetaController casetaController = loader.getController();
                    casetaController.setUsuarioActual(usuario);

                    stageActual.setScene(new Scene(root));
                    stageActual.setTitle("Sistema de Estacionamiento - Módulo de Caseta");
                    stageActual.setResizable(true);
                    stageActual.centerOnScreen();
                    break;

                case "ADMINISTRADOR":
                    Alert alertaAdmin = new Alert(Alert.AlertType.INFORMATION);
                    alertaAdmin.setTitle("Acceso Concedido");
                    alertaAdmin.setHeaderText("Bienvenido, " + usuario.getNombre());
                    alertaAdmin.setContentText("Rol: ADMINISTRADOR\nEl panel de administración se habilitará en el Día 15.");
                    alertaAdmin.showAndWait();
                    break;

                case "AUDITOR":
                    Alert alertaAuditor = new Alert(Alert.AlertType.INFORMATION);
                    alertaAuditor.setTitle("Acceso Concedido");
                    alertaAuditor.setHeaderText("Bienvenido, " + usuario.getNombre());
                    alertaAuditor.setContentText("Rol: AUDITOR / GERENCIA\nEl módulo de reportes se habilitará en el Día 16.");
                    alertaAuditor.showAndWait();
                    break;

                default:
                    lblMensajeError.setText("Rol de usuario no reconocido en el sistema.");
            }
        } catch (Exception e) {
            lblMensajeError.setText("Error al cargar la vista correspondiente al rol.");
            System.err.println("Error de navegación: " + e.getMessage());
        }
    }
}