package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a la base de datos MySQL.
 *
 * PATRÓN DE DISEÑO IMPLEMENTADO: Singleton.
 * Justificación: Garantiza que exista una única instancia activa de la conexión JDBC
 * durante todo el ciclo de vida de la aplicación, evitando la saturación de memoria
 * y el agotamiento de conexiones en el servidor de base de datos.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class ConexionDB {

    // Parámetros de configuración de acceso a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/estacionamiento_bd?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password"; // Reemplazar con la contraseña local de MySQL

    // Instancia única y estática de la conexión (Núcleo del patrón Singleton)
    private static Connection connection = null;

    /**
     * Constructor privado para restringir la creación de instancias externas
     * mediante el operador 'new'.
     */
    private ConexionDB() {
    }

    /**
     * Proporciona el punto de acceso global a la conexión de la base de datos.
     * Si la conexión no existe o fue cerrada, la inicializa nuevamente.
     *
     * @return Objeto {@link Connection} activo hacia MySQL.
     * @throws SQLException Si el driver no se encuentra o fallan las credenciales/red.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Carga dinámica del controlador JDBC de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error crítico: Driver JDBC de MySQL no encontrado en el proyecto.", e);
            }
        }
        return connection;
    }

    /**
     * Cierra de manera segura la conexión activa con el servidor MySQL
     * al finalizar la ejecución del sistema.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al intentar cerrar la conexión a MySQL: " + e.getMessage());
            }
        }
    }
}