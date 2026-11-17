
import config.ConexionDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que sirve como punto de entrada para la aplicación JavaFX.
 *
 * Pertenece a la capa Vista (MVC). Su única responsabilidad es cargar la escena
 * inicial de autenticación y gestionar el ciclo de vida de la ventana principal.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Sistema de Administración de Estacionamiento - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Se ejecuta automáticamente al cerrar la aplicación JavaFX, asegurando
     * la liberación de recursos de la base de datos.
     */
    @Override
    public void stop() {
        ConexionDB.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}