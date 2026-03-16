package com.example.ApSystem.service;

import com.example.ApSystem.model.DeviceMaintenance;
import com.example.ApSystem.repository.DeviceMaintenanceRepository;

import java.util.List;


public class DeviceMaintenanceService {

    private final DeviceMaintenanceRepository repository;

    public DeviceMaintenanceService(DeviceMaintenanceRepository repository) {
        this.repository = repository;
    }

    public DeviceMaintenance save(DeviceMaintenance maintenance) {
        String normalizedId = maintenance.getDeviceId().toUpperCase();

        if (repository.existsByDeviceId(normalizedId)) {
            throw new IllegalStateException("Device ID already registered");

        }

        maintenance.setDeviceId(normalizedId);
        return repository.save(maintenance);
    }

    public List<DeviceMaintenance> findAll() {
        return repository.findAll();
    }
}
