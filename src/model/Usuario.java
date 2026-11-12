package model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * Clase de entidad que representa a un usuario del sistema (Administrador, Operador o Auditor).
 *
 * Pertenece a la capa Modelo (MVC). Implementa propiedades observables de JavaFX
 * ({@link Property}) para permitir el enlace bidireccional y la actualización automática
 * en componentes visuales como {@code TableView}.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class Usuario {

    // Atributos encapsulados mediante JavaFX Properties
    private final LongProperty id;
    private final StringProperty nombre;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty rol;
    private final BooleanProperty activo;
    private LocalDateTime fechaCreacion;

    /**
     * Constructor por defecto. Inicializa las propiedades con valores base seguros
     * y asigna el rol operativo por omisión.
     */
    public Usuario() {
        this(0L, "", "", "", "OPERADOR", true);
    }

    /**
     * Constructor parametrizado para instanciar un usuario con todos sus atributos principales.
     *
     * @param id       Identificador único en base de datos.
     * @param nombre   Nombre completo del empleado o administrador.
     * @param username Nombre de cuenta para inicio de sesión.
     * @param password Contraseña (en texto plano antes de guardar o hash SHA-1 al recuperar).
     * @param rol      Nivel de privilegios (ADMINISTRADOR, OPERADOR, AUDITOR).
     * @param activo   Estado de habilitación de la cuenta en el sistema.
     */
    public Usuario(Long id, String nombre, String username, String password, String rol, boolean activo) {
        this.id = new SimpleLongProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.rol = new SimpleStringProperty(rol);
        this.activo = new SimpleBooleanProperty(activo);
    }

    // --- Métodos Getters, Setters y Propiedades Observables ---

    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    public String getRol() { return rol.get(); }
    public void setRol(String rol) { this.rol.set(rol); }
    public StringProperty rolProperty() { return rol; }

    public boolean isActivo() { return activo.get(); }
    public void setActivo(boolean activo) { this.activo.set(activo); }
    public BooleanProperty activoProperty() { return activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}