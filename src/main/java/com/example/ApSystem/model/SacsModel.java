package com.example.ApSystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sql_sacs")
@Data
@NoArgsConstructor
public class SacsModel {

    @Id
    /**
     * CORRECCIÓN:
     * 1. Cambiamos String a Integer porque Neon tiene un 'int4'.
     * 2. Mapeamos al nombre exacto de la columna 'item'.
     */
    @Column(name = "item")
    private Integer item;

    @Column(name = "id_puerta")
    private String idPuerta;

    private String ubicacion;

    @Column(name = "tipo_de_equipo")
    private String tipoDeEquipo;
}