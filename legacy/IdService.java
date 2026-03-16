package com.example.ApSystem.service.excel;

import com.example.ApSystem.dto.IdExcelResponseDTO;
import com.example.ApSystem.dto.IdFieldDTO;
import legacy.cctv_general_automation_report.excel.ExcelDataEntity;
import legacy.cctv_general_automation_report.excel.ExcelDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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