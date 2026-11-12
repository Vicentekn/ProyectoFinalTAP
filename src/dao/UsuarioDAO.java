package dao;

import model.Usuario;

/**
 * Interfaz que define las operaciones específicas de acceso a datos
 * exclusivas de la entidad {@link Usuario}, adicionales al CRUD genérico.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public interface UsuarioDAO {

    /**
     * Verifica si las credenciales ingresadas corresponden a un usuario activo en el sistema.
     *
     * @param username      Nombre de usuario ingresado.
     * @param passwordPlano Contraseña en texto plano ingresada en el formulario.
     * @return Objeto {@link Usuario} con su rol asignado si la validación es correcta; {@code null} si falla.
     */
    Usuario autenticar(String username, String passwordPlano);
}