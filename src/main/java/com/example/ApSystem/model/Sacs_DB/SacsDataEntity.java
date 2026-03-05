package com.example.ApSystem.model.Sacs_DB;

import jakarta.persistence.*;

@Entity
@Table(name = "sacs_data")
public class SacsDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String excelId; // El ID de la puerta (ej: 0-16-01)
    private String columna; // Nombre de la propiedad (ej: UBICACIÓN)
    private String valor;   // El contenido (ej: CARCAMO BHS)

    public SacsDataEntity() {
    }

    public SacsDataEntity(Long id, String excelId, String columna, String valor) {
        this.id = id;
        this.excelId = excelId;
        this.columna = columna;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExcelId() {
        return excelId;
    }

    public void setExcelId(String excelId) {
        this.excelId = excelId;
    }

    public String getColumna() {
        return columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    // Getters y Setters
}