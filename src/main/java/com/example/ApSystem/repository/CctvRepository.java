package com.example.ApSystem.repository;

import com.example.ApSystem.model.CctvModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CctvRepository extends JpaRepository<CctvModel, String> {
    // Este méetodo permitirá buscar cámaras por el ID exacto desde el front
    List<CctvModel> findByIdContaining(String id);
}