package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase abstracta base que define el contrato estándar de operaciones CRUD
 * (Crear, Leer, Actualizar, Eliminar) para cualquier entidad del sistema.
 *
 * REQUERIMIENTO TÉCNICO: Programación Genérica (<T>) y Patrón DAO.
 * Justificación: El uso del parámetro de tipo genérico {@code <T>} permite reutilizar
 * la estructura de acceso a datos para múltiples modelos (Usuario, Ticket, Tarifa),
 * garantizando seguridad de tipos en tiempo de compilación y evitando código duplicado.
 *
 * @param <T> Tipo de la entidad del modelo sobre la cual operará el DAO.
 * @author Vicente Soto García
 * @version 1.0
 */
public abstract class GenericDAO<T> {

    /** Conexión JDBC compartida proporcionada a las clases hijas */
    protected Connection connection;

    /**
     * Constructor que recibe la conexión activa hacia la base de datos.
     *
     * @param connection Instancia de {@link Connection}.
     */
    public GenericDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserta un nuevo registro de la entidad en la base de datos.
     * @param entity Objeto con los datos a persistir.
     * @return {@code true} si la inserción fue exitosa; {@code false} en caso contrario.
     */
    public abstract boolean save(T entity);

    /**
     * Actualiza los datos de un registro existente en la base de datos.
     * @param entity Objeto con los datos modificados.
     * @return {@code true} si la actualización afectó al menos una fila.
     */
    public abstract boolean update(T entity);

    /**
     * Elimina un registro de la base de datos a partir de su llave primaria.
     * @param id Identificador único del registro.
     * @return {@code true} si el registro fue eliminado correctamente.
     */
    public abstract boolean delete(Long id);

    /**
     * Recupera una entidad específica buscando por su identificador único.
     * @param id Identificador único a buscar.
     * @return La instancia de {@code T} encontrada, o {@code null} si no existe.
     */
    public abstract T findById(Long id);

    /**
     * Obtiene todos los registros almacenados de la entidad correspondiente.
     * @return Lista con todas las instancias de {@code T} recuperadas.
     */
    public abstract List<T> findAll();

    /**
     * Método auxiliar encargado de transformar una fila del resultado SQL ({@link ResultSet})
     * en una instancia fuertemente tipada del modelo {@code T}.
     *
     * @param rs Cursor posicionado en la fila actual de la consulta.
     * @return Objeto de tipo {@code T} poblado con los datos de la fila.
     * @throws SQLException Si ocurre un error al leer las columnas del resultado.
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
}