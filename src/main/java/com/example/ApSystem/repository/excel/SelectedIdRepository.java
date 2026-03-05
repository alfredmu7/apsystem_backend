package com.example.ApSystem.repository.excel;

import com.example.ApSystem.model.excel.SelectedIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectedIdRepository
        extends JpaRepository<SelectedIdEntity, Long> {
}
