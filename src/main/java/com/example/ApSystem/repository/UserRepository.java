package com.example.ApSystem.repository;

import com.example.ApSystem.model.User; // Asegúrate de tener tu entidad User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Cambiado de findByUsername
}