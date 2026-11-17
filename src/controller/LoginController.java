package controller;

import config.ConexionDB;
import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
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

        // Mensaje temporal de verificación antes de conectar las vistas de Semana 2 y 3
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Acceso Concedido");
        alerta.setHeaderText("Bienvenido, " + usuario.getNombre());

        switch (rol) {
            case "ADMINISTRADOR":
                alerta.setContentText("Rol: ADMINISTRADOR\nRedirigiendo al Panel de Control Total y Tarifas...");
                alerta.showAndWait();
                // TODO (Día 15): Cargar admin_view.fxml
                break;

            case "OPERADOR":
                alerta.setContentText("Rol: OPERADOR DE CASETA\nRedirigiendo al Módulo de Entradas y Salidas...");
                alerta.showAndWait();
                // TODO (Día 10): Cargar caseta_view.fxml
                break;

            case "AUDITOR":
                alerta.setContentText("Rol: AUDITOR / GERENCIA\nRedirigiendo al Módulo de Reportes (Solo Lectura)...");
                alerta.showAndWait();
                // TODO (Día 16): Cargar reportes_view.fxml
                break;

            default:
                lblMensajeError.setText("Rol de usuario no reconocido en el sistema.");
                return;
        }

        // Ejemplo de cómo cerrar la ventana actual cuando se abra la siguiente escena:
        // Stage stageActual = (Stage) btnIngresar.getScene().getWindow();
        // stageActual.close();
    }
}