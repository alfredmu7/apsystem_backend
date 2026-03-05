package com.example.ApSystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cctv_maintenance")
public class CctvMaintenanceTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String camera;

    private String mt1;
    private String mt2;

    private String usuario;

    @Column(columnDefinition = "TEXT")
    private String observation;

    private LocalDateTime updateDate;

    public CctvMaintenanceTable(Long id, String camera, String mt1, String mt2, String usuario, String observation, LocalDateTime updateDate) {
        this.id = id;
        this.camera = camera;
        this.mt1 = mt1;
        this.mt2 = mt2;
        this.usuario = usuario;
        this.observation = observation;
        this.updateDate = updateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getMt1() {
        return mt1;
    }

    public void setMt1(String mt1) {
        this.mt1 = mt1;
    }

    public String getMt2() {
        return mt2;
    }

    public void setMt2(String mt2) {
        this.mt2 = mt2;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
