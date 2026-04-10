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
@CrossOrigin(origins = "*")
public class SacsController {

    @Autowired
    private SacsService sacsService;

    // --- 1. BUSCADOR (Inventario) ---
    @GetMapping("/search/{idPuerta}")
    public ResponseEntity<List<SacsModel>> buscarPorPuerta(@PathVariable String idPuerta) {
        List<SacsModel> resultados = sacsService.buscarPorIdPuerta(idPuerta);
        return ResponseEntity.ok(resultados);
    }

    // --- 2. EJECUCIÓN (Sincronizado con el JS y el Model) ---
    @PostMapping("/maintenance/execute")
    public ResponseEntity<?> registrarMantenimiento(@RequestBody Map<String, Object> body) {
        String id = String.valueOf(body.get("id"));
        String obs = String.valueOf(body.get("observation"));
        String tech = String.valueOf(body.get("technician"));
        String item = String.valueOf(body.get("item")); // <--- Nuevo
        String ubicacion = String.valueOf(body.get("ubicacion")); // <--- Nuevo
        String tipo = String.valueOf(body.get("tipo")); // <--- Nuevo

        return ResponseEntity.ok(sacsService.registrarMantenimiento(id, obs, tech, item, ubicacion, tipo));
    }

    // --- 3. HISTORIAL ---
    @GetMapping("/maintenance/history")
    public ResponseEntity<List<SacsMaintenanceRecord>> verHistorial() {
        return ResponseEntity.ok(sacsService.obtenerHistorialCompleto());
    }

    // --- 4. LISTADO GENERAL ---
    @GetMapping
    public ResponseEntity<List<SacsModel>> listarPuertas() {
        return ResponseEntity.ok(sacsService.listarTodo());
    }
}