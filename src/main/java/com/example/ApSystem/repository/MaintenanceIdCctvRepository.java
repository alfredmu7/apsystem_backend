package com.example.ApSystem.repository;

import com.example.ApSystem.model.DeviceCctvMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceIdCctvRepository

     extends JpaRepository<DeviceCctvMaintenance, Long>{

        boolean existsByExcelId(String excelId);
        List<DeviceCctvMaintenance> findAll();
    }

