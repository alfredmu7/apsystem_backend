package com.example.ApSystem.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_mantenimiento_fads")
@Data
public class FadsMaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dispositivo_id")
    private String dispositivoId;

    private String item;

    @Column(name = "tipo_de_equipo")
    private String tipoDeEquipo;
    private String tecnico;
    private String estado;
    private String observaciones;

    @Column(name = "fecha_mantenimiento")
    private LocalDateTime fechaMantenimiento;
}