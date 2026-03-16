package com.example.ApSystem.controller;

import com.example.ApSystem.model.SacsMaintenanceRecord;
import com.example.ApSystem.model.SacsModel;
import com.example.ApSystem.service.SacsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sacs")
@CrossOrigin(origins = "*") // Permite que React se comunique con Spring
public class SacsController {

    @Autowired
    private SacsService sacsService;

    // --- 1. BUSCADOR (Inventario) ---
    // Este ya lo tienes, permite buscar la puerta para ver sus detalles técnicos
    @GetMapping("/search/{idPuerta}")
    public ResponseEntity<List<SacsModel>> buscarPorPuerta(@PathVariable String idPuerta) {
        List<SacsModel> resultados = sacsService.buscarPorIdPuerta(idPuerta);
        return ResponseEntity.ok(resultados);
    }

    // --- 2. EJECUCIÓN (Nueva Tabla de Mantenimiento en Neon) ---
    // Este recibe el POST de tu función applySacsMaintenance de la API
    @PostMapping("/maintenance/execute")
    public ResponseEntity<?> registrarMantenimiento(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        String obs = body.get("observation");
        String tech = body.get("technician");

        // El servicio debe encargarse de insertar en la tabla registros_mantenimiento_sacs
        return ResponseEntity.ok(sacsService.registrarMantenimiento(id, obs, tech));
    }

    // --- 3. HISTORIAL (Pestaña Device) ---
    // Este alimenta la función getSacsMaintenanceHistory de tu API
    @GetMapping("/maintenance/history")
    public ResponseEntity<List<SacsMaintenanceRecord>> verHistorial() {
        // Devuelve la lista de mantenimientos realizados, no el inventario de puertas
        return ResponseEntity.ok(sacsService.obtenerHistorialCompleto());
    }

    // --- 4. LISTADO GENERAL (Inventario) ---
    @GetMapping
    public ResponseEntity<List<SacsModel>> listarPuertas() {
        return ResponseEntity.ok(sacsService.listarTodo());
    }
}