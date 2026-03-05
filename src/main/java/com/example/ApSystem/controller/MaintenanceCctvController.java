package com.example.ApSystem.controller;

import com.example.ApSystem.service.MaintenanceCctvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cctv")
@CrossOrigin(origins = "http://localhost:5173")
public class MaintenanceCctvController {

    private final MaintenanceCctvService service;

    public MaintenanceCctvController(MaintenanceCctvService service) {
        this.service = service;
        }

    // 🛠 BOTÓN MAINTENANCE
    @PostMapping("/maintenance")
    public ResponseEntity<Void> addMaintenance(
            @RequestBody Map<String, String> body) {

        // 1️⃣ Extraemos el ID enviado desde el frontend
        String excelId = body.get("excelId");
        String observation = (String) body.get("observation");
        String operator = (String) body.get("operator");

        // 2️⃣ Delegamos toda la lógica al service
        service.addMaintenance(excelId, observation, operator);

        // 3️⃣ Respondemos OK
        return ResponseEntity.ok().build();
    }


    // 2️⃣ TABLA DEVICE/CCTV
    @GetMapping("/maintenance")
    public Map<String, Object> getMaintenanceTable() {
        return service.getMaintenanceTable();
    }



    // 3- TABLA DEVICE/table
    @GetMapping("/maintenance/table")
    public Map<String, Object> getCctvMaintenanceTable() {
        return service.getMaintenanceTable();
    }


    @PostMapping("/maintenance/update-row")
    public ResponseEntity<Void> updateRow(@RequestBody Map<String, Object> body) {
        String excelId = (String) body.get("excelId");

        Map<String, Object> rawValues = (Map<String, Object>) body.get("values");
        Map<String, String> values = rawValues.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() == null ? "" : e.getValue().toString()
                ));

        service.updateRow(excelId, values);
        return ResponseEntity.ok().build();
    }

}
