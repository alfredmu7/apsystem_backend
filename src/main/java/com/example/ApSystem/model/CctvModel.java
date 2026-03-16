package com.example.ApSystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sql_cctv") // Nombre exacto de la tabla en Neon
@Data
@NoArgsConstructor
public class CctvModel {

    @Id
    // Eliminamos @GeneratedValue porque el ID ya viene escrito en el CSV (ej: "2-40-1")
    @Column(name = "id")
    private String id; // Cambiado de Long a String para coincidir con el TEXT de la DB

    private String s;
    private String fase;
    private String ubicacion;
    private String camara;
    private String tipo;

    @Column(name = "tipo_de_acceso") // Si en Neon es snake_case
    private String tipo_de_acceso;

    private String observacion;
}