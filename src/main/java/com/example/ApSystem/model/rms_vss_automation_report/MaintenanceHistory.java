package com.example.ApSystem.model.rms_vss_automation_report;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
        * ENTIDAD: MaintenanceHistory
 *
         * Representa un registro persistente de un mantenimiento realizado.
        * Este historial es independiente del Excel original donde están
 * los datos del dispositivo y sirve para generar el reporte mensual.
 */
@Entity
@Table(name = "maintenance_history")
public class MaintenanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId; // ID interno del historial (NO es el ID del dispositivo)

    /**
     * ID del dispositivo mantenido.
     * Este viene de tu DB (excel -> H2).
     */
    @Column(nullable = false)
    private String deviceId;

    /**
     * Fecha REAL del mantenimiento (no cuándo se registró).
     * Elegida manualmente en el popup => clave para agrupar hojas.
     */
    @Column(nullable = false)
    private LocalDate maintenanceDate;

    /**
     * Fecha/hora en que se registró en el sistema.
     * Sirve para auditoría y orden lógico si hace falta.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Ciclo del año (semestre): ejemplo
     * 1 = Enero-Junio
     * 2 = Julio-Diciembre
     *
     * Lo usamos para evitar duplicados semestrales
     * y resetear al cambio de ciclo.
     */
    @Column(nullable = false)
    private Integer cycle;

    /**
     * Datos copiados desde la DB original (fase / ubicación / etc),
     * útiles para reconstruir la plantilla final.
     */
    private String phase;
    private String location;

    /**
     * Campo editable desde UI (observacion)
     * que luego va escrito directamente en el Excel final.
     */
    private String observation;

    /**
     * Checks automáticos — si se ejecuta el mantenimiento se asumen TRUE
     * y luego se traducen a ✔ en el Excel final.
     */
    private Boolean estadoInicial;
    private Boolean inspVisual;
    private Boolean montajeMastil;
    private Boolean conexionesFacePlate;
    private Boolean limpiezaLente;
    private Boolean housing;
    private Boolean estadoFinal;
    private Boolean foco;
    private Boolean iris;
    private Boolean nitidez;

    /**
     * Opcional — operador o técnico que realizó la acción.
     * Por ahora no obligatorio.
     */
    private String operator;


    // ---------- CONSTRUCTORES ----------

    public MaintenanceHistory() {}


    public MaintenanceHistory(
            String deviceId,
            LocalDate maintenanceDate,
            Integer cycle,
            String phase,
            String location,
            String observation,
            Boolean estadoInicial,
            Boolean inspVisual,
            Boolean montajeMastil,
            Boolean conexionesFacePlate,
            Boolean limpiezaLente,
            Boolean housing,
            Boolean estadoFinal,
            Boolean foco,
            Boolean iris,
            Boolean nitidez,
            String operator
    ) {
        this.deviceId = deviceId;
        this.maintenanceDate = maintenanceDate;
        this.cycle = cycle;
        this.phase = phase;
        this.location = location;
        this.observation = observation;
        this.estadoInicial = estadoInicial;
        this.inspVisual = inspVisual;
        this.montajeMastil = montajeMastil;
        this.conexionesFacePlate = conexionesFacePlate;
        this.limpiezaLente = limpiezaLente;
        this.housing = housing;
        this.estadoFinal = estadoFinal;
        this.foco = foco;
        this.iris = iris;
        this.nitidez = nitidez;
        this.operator = operator;
    }

    // ---------- GETTERS & SETTERS (omitibles al generar con Lombok) ----------


    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Boolean getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(Boolean estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public Boolean getInspVisual() {
        return inspVisual;
    }

    public void setInspVisual(Boolean inspVisual) {
        this.inspVisual = inspVisual;
    }

    public Boolean getMontajeMastil() {
        return montajeMastil;
    }

    public void setMontajeMastil(Boolean montajeMastil) {
        this.montajeMastil = montajeMastil;
    }

    public Boolean getConexionesFacePlate() {
        return conexionesFacePlate;
    }

    public void setConexionesFacePlate(Boolean conexionesFacePlate) {
        this.conexionesFacePlate = conexionesFacePlate;
    }

    public Boolean getLimpiezaLente() {
        return limpiezaLente;
    }

    public void setLimpiezaLente(Boolean limpiezaLente) {
        this.limpiezaLente = limpiezaLente;
    }

    public Boolean getHousing() {
        return housing;
    }

    public void setHousing(Boolean housing) {
        this.housing = housing;
    }

    public Boolean getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(Boolean estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public Boolean getFoco() {
        return foco;
    }

    public void setFoco(Boolean foco) {
        this.foco = foco;
    }

    public Boolean getIris() {
        return iris;
    }

    public void setIris(Boolean iris) {
        this.iris = iris;
    }

    public Boolean getNitidez() {
        return nitidez;
    }

    public void setNitidez(Boolean nitidez) {
        this.nitidez = nitidez;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
