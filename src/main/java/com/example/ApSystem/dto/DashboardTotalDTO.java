package com.example.ApSystem.dto;

/**
 * DTO para métricas simples del dashboard
 * No se usa entidades para no exponer el modelo
 */
public class DashboardTotalDTO {

    private long total;

    public DashboardTotalDTO(long total) {
        this.total = total;
    }

    public long getTotal() {
        return total;
    }
}
