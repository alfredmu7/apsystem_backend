package com.example.ApSystem.controller.sacs;

import com.example.ApSystem.model.Sacs_DB.SacsDataEntity;
import com.example.ApSystem.repository.sacs.SacsDataRepository;
import com.example.ApSystem.service.sacs.SacsLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Importante: RestController, no Controller
@RequestMapping("/api/v1/sacs")
@CrossOrigin(origins = "*") // Para evitar bloqueos de React
public class SacsController {

    @Autowired
    private SacsDataRepository sacsRepo;

    @GetMapping("/search/{id}")
    public ResponseEntity<List<SacsDataEntity>> search(@PathVariable String id) {
        System.out.println("🔍 Buscando en DB el ID: [" + id + "]");

        List<SacsDataEntity> results = sacsRepo.findByExcelId(id.trim());

        if (results.isEmpty()) {
            System.out.println("❌ No se encontró nada para: [" + id + "]");
            // TIP: Imprime un ID que SÍ exista en la DB para comparar
            sacsRepo.findAll().stream().findFirst().ifPresent(data ->
                    System.out.println("💡 Ejemplo de ID que sí existe en DB: [" + data.getExcelId() + "]")
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        System.out.println("✅ Se encontraron " + results.size() + " propiedades.");
        return ResponseEntity.ok(results);
    }
}