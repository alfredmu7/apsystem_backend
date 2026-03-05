package com.example.ApSystem.model.cctv_general_automation_report;

public class WordMaintenanceEntry {

    private String fecha;
    private String informe; // aqui ira las fases segun la DB
    private String idCamara;
    private String ubicacion;
    private String registroFotografico; // Por ahora texto, ej: "agregar foto"

    public WordMaintenanceEntry(String fecha, String informe, String idCamara, String ubicacion, String registroFotografico) {

        this.fecha = fecha;
        this.informe = informe;
        this.idCamara = idCamara;
        this.ubicacion = ubicacion;
        this.registroFotografico = registroFotografico;
    }

    public WordMaintenanceEntry() {
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public String getIdCamara() {
        return idCamara;
    }

    public void setIdCamara(String idCamara) {
        this.idCamara = idCamara;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getRegistroFotografico() {
        return registroFotografico;
    }

    public void setRegistroFotografico(String registroFotografico) {
        this.registroFotografico = registroFotografico;
    }
}