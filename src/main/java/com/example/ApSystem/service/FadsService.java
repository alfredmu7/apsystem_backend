package com.example.ApSystem.service;

import com.example.ApSystem.model.FadsMaintenanceRecord;
import com.example.ApSystem.model.FadsModel;
import com.example.ApSystem.repository.FadsMaintenanceRepository;
import com.example.ApSystem.repository.FadsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FadsService {

    @Autowired
    private FadsRepository fadsRepository;

    @Autowired
    private FadsMaintenanceRepository maintenanceRepository;

    // --- MÉTODOS DE INVENTARIO  ---

    public List<FadsModel> obtenerInventarioFads() {
        return fadsRepository.findAll();
    }

    public List<FadsModel> buscarPorId(String id) {
        return fadsRepository.findByIdContaining(id);
    }

    // --- MÉTODOS DE MANTENIMIENTO  ---

    /**
     * Registra un mantenimiento preventivo en la tabla registros_mantenimiento_fads
     */
    public FadsMaintenanceRecord registrarMantenimiento(String id, String item, String tipo, String obs, String tech) {
        FadsMaintenanceRecord record = new FadsMaintenanceRecord();

        record.setDispositivoId(id);
        record.setItem(item);
        record.setTipoDeEquipo(tipo);
        record.setObservaciones(obs);
        record.setTecnico(tech);
        record.setFechaMantenimiento(LocalDateTime.now());
        record.setEstado("COMPLETADO");

        return maintenanceRepository.save(record);
    }

    /**
     * Obtiene todoo el historial para la pestaña de dispositivos
     */
    public List<FadsMaintenanceRecord> obtenerHistorialCompleto() {
        return maintenanceRepository.findAll();
    }

    public FadsModel registrarDispositivo(FadsModel fads) {
        return fadsRepository.save(fads);
    }

    /**
     * Permite editar la celda de observaciones desde la tabla del Front
     */
    public FadsMaintenanceRecord actualizarObservacion(Integer id, String nuevaObs) {
        FadsMaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro FADS no encontrado"));

        record.setObservaciones(nuevaObs);
        return maintenanceRepository.save(record);
    }

    public void eliminar(String id) {
        fadsRepository.deleteById(id);
    }
}