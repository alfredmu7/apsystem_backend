package com.example.ApSystem.service;

import com.example.ApSystem.model.CctvModel;
import com.example.ApSystem.repository.CctvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CctvService {

    @Autowired
    private CctvRepository cctvRepository;

    public List<CctvModel> listarTodo() {
        return cctvRepository.findAll();
    }

    // Cambiado de Long a String para soportar los IDs de Neon/CSV
    public List<CctvModel> buscarPorId(String id) {
        return cctvRepository.findByIdContaining(id);
    }

    public CctvModel guardar(CctvModel camara) {
        return cctvRepository.save(camara);
    }

    public void eliminar(String id) {
        cctvRepository.deleteById(id);
    }
}