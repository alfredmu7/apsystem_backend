package com.example.ApSystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Se guardará encriptada (BCrypt)

    private String role; // Ejemplo: "ADMIN", "TECNICO"

    // 1. IMPORTANTE: Constructor vacío requerido por JPA/Hibernate
    public User() {
    }

    // 2. Tu constructor con parámetros (útil para crear usuarios manualmente)
    public User(String username, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}