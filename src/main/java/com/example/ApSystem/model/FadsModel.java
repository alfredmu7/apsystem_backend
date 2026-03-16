package com.example.ApSystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sql_fads")
@Data
@NoArgsConstructor
public class FadsModel {

    @Id
    @Column(name = "id") // Mapea a la columna ID de Neon
    private String id;

    private String tipo;

    // Si la columna en Neon se llama "dispositivo", el atributo debe ser:
    @Column(name = "dispositivo")
    private String dispositivo;

    private String ubicacion;
    private String descripcion;
    private String zona;
}