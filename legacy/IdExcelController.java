package com.example.ApSystem.controller;

import com.example.ApSystem.dto.IdExcelResponseDTO;
import com.example.ApSystem.service.excel.IdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/ids")
@CrossOrigin(origins = "http://localhost:5173")
public class IdExcelController {

    private final IdService idService;

    public IdExcelController(IdService idService) {
        this.idService = idService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdExcelResponseDTO> getId(@PathVariable String id) {
        return ResponseEntity.ok(
                idService.getInfoById(id.trim())
        );
    }
}