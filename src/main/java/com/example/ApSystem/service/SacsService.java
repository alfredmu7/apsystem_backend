package com.example.ApSystem.service;

import com.example.ApSystem.model.SacsModel;
import com.example.ApSystem.model.SacsMaintenanceRecord; // Entidad nueva
import com.example.ApSystem.repository.SacsRepository;
import com.example.ApSystem.repository.SacsMaintenanceRepository; // Repo nuevo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SacsService {

    @Autowired
    private SacsRepository sacsRepository;

    @Autowired
    private SacsMaintenanceRepository maintenanceRepository;

    // --- MÉTODOS DE INVENTARIO (Ya los tenías) ---

    public List<SacsModel> buscarPorIdPuerta(String idPuerta) {
        return sacsRepository.findByIdPuerta(idPuerta);
    }

    public List<SacsModel> listarTodo() {
        return sacsRepository.findAll();
    }

    // --- MÉTODOS DE MANTENIMIENTO (Para sincronizar con el Controller) ---

    /**
     * Registra un nuevo mantenimiento en la tabla de Neon.
     */
    public SacsMaintenanceRecord registrarMantenimiento(String id, String obs, String tech) {
        SacsMaintenanceRecord record = new SacsMaintenanceRecord();
        record.setDispositivoId(id);
        record.setObservaciones(obs);
        record.setTecnico(tech);
        record.setFechaMantenimiento(LocalDateTime.now());
        record.setEstado("COMPLETADO");
        return maintenanceRepository.save(record);
    }

    /**
     * Obtiene todos los registros de mantenimiento para la tabla de la UI.
     */
    public List<SacsMaintenanceRecord> obtenerHistorialCompleto() {
        return maintenanceRepository.findAll();
    }

    // --- MÉTODOS CRUD BÁSICOS ---

    public SacsModel guardar(SacsModel sacs) {
        return sacsRepository.save(sacs);
    }

    public void eliminar(Integer id) {
        sacsRepository.deleteById(id);
    }
}