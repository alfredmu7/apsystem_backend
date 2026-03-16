package com.example.ApSystem.repository;

import com.example.ApSystem.model.FadsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface FadsRepository extends JpaRepository<FadsModel, String> {

        // Si necesitas buscar por un campo que no sea el ID principal:
        Optional<FadsModel> findByDispositivo(String dispositivo);

        // Al llamarse 'id' en el modelo, Spring busca en la columna 'id' de la tabla
        List<FadsModel> findByIdContaining(String id);

        // O si es búsqueda exacta:
        Optional<FadsModel> findById(String id);
    }