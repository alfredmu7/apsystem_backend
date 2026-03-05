package com.example.ApSystem.controller.sacs;

import com.example.ApSystem.service.sacs.SacsMaintenanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sacs/maintenance")
@CrossOrigin(origins = "*")
public class SacsMaintenanceController {

    private final SacsMaintenanceService maintenanceService;

    public SacsMaintenanceController(SacsMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // Este es el que llama tu botón "Apply Maintenance"
    @PostMapping("/{id}")
    public ResponseEntity<?> applyMaintenance(@PathVariable String id) {
        try {
            maintenanceService.addMaintenance(id);
            return ResponseEntity.ok(Map.of("message", "Maintenance recorded for SACS ID: " + id));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/table")
    public ResponseEntity<Map<String, Object>> getTable() {
        return ResponseEntity.ok(maintenanceService.getMaintenanceTable());
    }

}