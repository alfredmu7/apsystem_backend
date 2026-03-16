package com.example.ApSystem.repository;

import com.example.ApSystem.model.CctvMaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CctvMaintenanceRepository extends JpaRepository<CctvMaintenanceRecord, Integer> {

    // Tu metodo actual para la tabla de la UI
    List<CctvMaintenanceRecord> findAllByOrderByFechaMantenimientoDesc();

    /**
     * Verifica si existe un registro para este ID.
     * Esto evitará que el reporte Word se llene con el mismo ID varias veces.
     */
    boolean existsByDispositivoId(String dispositivoId);
}