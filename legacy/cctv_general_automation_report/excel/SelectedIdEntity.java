package legacy.cctv_general_automation_report.excel;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "ids_registrados")
public class SelectedIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID que el usuario buscó
    private String excelId;

    // Fecha automática de registro
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public SelectedIdEntity() {
    }

    public SelectedIdEntity(Long id, String excelId, LocalDateTime fechaRegistro) {
        this.id = id;
        this.excelId = excelId;
        this.fechaRegistro = fechaRegistro;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // getters y setters
}
