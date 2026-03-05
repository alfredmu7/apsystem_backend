package com.example.ApSystem.service.excel;

import com.example.ApSystem.dto.IdExcelResponseDTO;
import com.example.ApSystem.dto.IdFieldDTO;
import com.example.ApSystem.model.excel.ExcelDataEntity;
import com.example.ApSystem.model.excel.SelectedIdEntity;
import com.example.ApSystem.repository.excel.ExcelDataRepository;
import com.example.ApSystem.repository.excel.SelectedIdRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IdService {

    private final ExcelDataRepository excelRepo;

    public IdService(ExcelDataRepository excelRepo) {
        this.excelRepo = excelRepo;
    }

    public IdExcelResponseDTO getInfoById(String id) {

        List<ExcelDataEntity> data =
                excelRepo.findByExcelId(id);

        if (data.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "ID no encontrado"
            );
        }

        IdExcelResponseDTO response = new IdExcelResponseDTO();
        response.setId(id);

        List<IdFieldDTO> fields = data.stream().map(d -> {
            IdFieldDTO f = new IdFieldDTO();
            f.setColumna(d.getColumna());
            f.setValor(d.getValor());
            return f;
        }).toList();

        response.setData(fields);
        return response;
    }
}