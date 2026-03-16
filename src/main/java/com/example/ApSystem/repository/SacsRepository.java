package com.example.ApSystem.repository;

import com.example.ApSystem.model.SacsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SacsRepository extends JpaRepository<SacsModel, Integer> {

    // Buscamos por el atributo idPuerta (columna id_puerta)
    List<SacsModel> findByIdPuerta(String idPuerta);

    // Opcional: Por si quieres búsqueda parcial (ej: escriben "0-16" y salen todos los de ese piso)
    List<SacsModel> findByIdPuertaContaining(String idPuerta);
}