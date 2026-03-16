package com.example.ApSystem.controller;

import com.example.ApSystem.model.CctvModel;
import com.example.ApSystem.service.CctvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cctv")
@CrossOrigin(origins = "*")
public class CctvController {

    @Autowired
    private CctvService cctvService;

    @GetMapping
    public ResponseEntity<List<CctvModel>> listar() {
        return ResponseEntity.ok(cctvService.listarTodo());
    }

    // Nuevo endpoint para el buscador del frontend
    @GetMapping("/search/{id}")
    public ResponseEntity<List<CctvModel>> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(cctvService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CctvModel> actualizar(@PathVariable String id, @RequestBody CctvModel camara) {
        camara.setId(id);
        return ResponseEntity.ok(cctvService.guardar(camara));
    }
}