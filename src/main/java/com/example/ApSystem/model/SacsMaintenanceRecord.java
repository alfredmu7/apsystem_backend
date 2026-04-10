package com.example.ApSystem.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_mantenimiento_sacs")
@Data
public class SacsMaintenanceRecord {

    @Column(name = "item")
    private String item;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "fecha_mantenimiento")
    private LocalDateTime fechaMantenimiento;

    @Column(name = "dispositivo_id", nullable = false)
    private String dispositivoId;

    @Column(name = "tipo_de_equipo")
    private String tipoDeEquipo;

    private String tecnico;
    private String estado;
    private String observaciones;
}