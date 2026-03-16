package com.example.ApSystem.controller;

import com.example.ApSystem.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*") // luego se ajusta con seguridad
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    // Endpoint para la primera card
    @GetMapping("/total")
    public long getTotalMaintenances() {
        return service.getTotalMaintenances();
    }

    //  Mantenimientos por mes
    @GetMapping("/maintenance-by-month")
    public List<Map<String, Object>> getByMonth() {
        return service.getMaintenancesByMonth();
    }



}
