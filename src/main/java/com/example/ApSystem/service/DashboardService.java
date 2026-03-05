package com.example.ApSystem.service;

import com.example.ApSystem.repository.DeviceMaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final DeviceMaintenanceRepository repository;

    // Inyección por constructor
    public DashboardService(DeviceMaintenanceRepository repository) {
        this.repository = repository;
    }

    // Lógica del negocio- total de mantenimientos
    public long getTotalMaintenances() {
        return repository.count();
    }

    // Mantenimiento por mes
    public List<Map<String, Object>> getMaintenancesByMonth() {

        List<Object[]> rawData = repository.countByMonth();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rawData) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", row[0]);
            item.put("year", row[1]);
            item.put("total", row[2]);

            result.add(item);
        }

        return result;
    }

}
