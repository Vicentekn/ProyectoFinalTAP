package model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Usuario {
    private final LongProperty id;
    private final StringProperty nombre;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty rol;
    private final BooleanProperty activo;
    private LocalDateTime fechaCreacion;

    public Usuario() {
        this(0L, "", "", "", "OPERADOR", true);
    }

    public Usuario(Long id, String nombre, String username, String password, String rol, boolean activo) {
        this.id = new SimpleLongProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.rol = new SimpleStringProperty(rol);
        this.activo = new SimpleBooleanProperty(activo);
    }

    // Getters y Setters tradicionales + Properties para JavaFX
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