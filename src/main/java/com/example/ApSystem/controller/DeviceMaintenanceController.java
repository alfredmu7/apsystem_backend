package com.example.ApSystem.controller;

import com.example.ApSystem.model.DeviceMaintenance;
import com.example.ApSystem.service.DeviceMaintenanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*") // luego se ajusta
public class DeviceMaintenanceController {

    private final DeviceMaintenanceService service;

    public DeviceMaintenanceController(DeviceMaintenanceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody DeviceMaintenance maintenance) {
        try {
            DeviceMaintenance saved = service.save(maintenance);
            return ResponseEntity.ok(saved);

        } catch (IllegalStateException e) {
            //  ID duplicado
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public List<DeviceMaintenance> getAll() {
        return service.findAll();
    }
}