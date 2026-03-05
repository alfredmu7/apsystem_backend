package com.example.ApSystem.dto;


import java.util.List;

public class IdExcelResponseDTO {

    private String id;
    private List<IdFieldDTO> data;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<IdFieldDTO> getData() { return data; }
    public void setData(List<IdFieldDTO> data) { this.data = data; }
}

