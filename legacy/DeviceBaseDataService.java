package com.example.ApSystem.service.rms_vss_automation_report;

import com.example.ApSystem.dto.IdExcelResponseDTO;
import com.example.ApSystem.dto.IdFieldDTO;
import com.example.ApSystem.service.excel.IdService;
import org.springframework.stereotype.Service;

/**
 * Servicio que obtiene los datos base de un dispositivo
 * desde la tabla cargada desde Excel.
 *
 * Este servicio NO escribe nada — solo consulta.
 * Se utiliza al registrar mantenimiento para completar:
 *  - fase
 *  - ubicación
 */
public class DeviceBaseDataService {

    private final IdService idService;

    public DeviceBaseDataService(IdService idService) {
        this.idService = idService;
    }

    public DeviceBaseData getBaseDataByDeviceId(String deviceId) {
        IdExcelResponseDTO response = idService.getInfoById(deviceId);
        String phase = "";
        String location = "";

        for (IdFieldDTO field : response.getData()) {
            String col = field.getColumna().toLowerCase();
            if (col.contains("fase")) phase = field.getValor();
            if (col.contains("ubicacion") || col.contains("ubicación")) location = field.getValor();
        }

        // DEBUG: Si ves esto vacío en consola, el problema es la búsqueda del ID
        System.out.println("DEBUG - ID: " + deviceId + " | Fase encontrada: " + phase);

        return new DeviceBaseData(deviceId, phase, location);
    }

    public record DeviceBaseData(
            String deviceId,
            String phase,
            String location
    ) {}
}

