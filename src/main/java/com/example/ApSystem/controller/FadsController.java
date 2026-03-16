package com.example.ApSystem.controller;

import com.example.ApSystem.model.FadsMaintenanceRecord;
import com.example.ApSystem.model.FadsModel;
import com.example.ApSystem.service.FadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fads")
@CrossOrigin(origins = "*") // Ajustar según tu frontend.url del properties
public class FadsController {

    @Autowired
    private FadsService fadsService;

    @GetMapping("/search/{id}")
    public ResponseEntity<FadsModel> buscarPorId(@PathVariable String id) {
        List<FadsModel> resultados = fadsService.buscarPorId(id);
        if (resultados.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Devolvemos solo el primer resultado para que el frontend
        // lo reciba como un objeto y no como un array.
        return ResponseEntity.ok(resultados.get(0));
    }

    @PostMapping("/maintenance/execute")
    public ResponseEntity<?> registrarMantenimiento(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        String item = body.get("item");
        String tipo = body.get("tipo_de_equipo");
        String obs = body.get("observation");
        String tech = body.get("technician");

        return ResponseEntity.ok(fadsService.registrarMantenimiento(id, item, tipo, obs, tech));
    }

    @GetMapping("/maintenance/history")
    public ResponseEntity<List<FadsMaintenanceRecord>> verHistorial() {
        return ResponseEntity.ok(fadsService.obtenerHistorialCompleto());
    }

    @GetMapping
    public ResponseEntity<List<FadsModel>> listarTodo() {
        return ResponseEntity.ok(fadsService.obtenerInventarioFads());
    }

    @PostMapping
    public ResponseEntity<FadsModel> crear(@RequestBody FadsModel fads) {
        return ResponseEntity.ok(fadsService.registrarDispositivo(fads));
    }
}