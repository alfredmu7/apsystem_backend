package com.example.ApSystem.repository;
import com.example.ApSystem.model.DeviceMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DeviceMaintenanceRepository extends JpaRepository<DeviceMaintenance, Long> {

    // Conteo por mes y año
    @Query("""
        SELECT 
            MONTH(d.createdAt) as month,
            YEAR(d.createdAt) as year,
            COUNT(d)
        FROM DeviceMaintenance d
        GROUP BY YEAR(d.createdAt), MONTH(d.createdAt)
        ORDER BY year, month
    """)
    List<Object[]> countByMonth();


    boolean existsByDeviceId(String deviceId);


    //Cuenta todos los registros de mantenimiento
    long count();

    }

