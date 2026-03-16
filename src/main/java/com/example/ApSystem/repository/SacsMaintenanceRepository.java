package com.example.ApSystem.repository;

import com.example.ApSystem.model.SacsMaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SacsMaintenanceRepository extends JpaRepository<SacsMaintenanceRecord, Long> {
}