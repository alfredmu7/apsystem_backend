package com.example.ApSystem.repository;

import com.example.ApSystem.model.FadsMaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FadsMaintenanceRepository extends JpaRepository<FadsMaintenanceRecord, Integer> {
    // Hereda todos los métodos necesarios (save, findAll, etc.)
}