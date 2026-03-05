package com.example.ApSystem.controller;

import com.example.ApSystem.dto.*;
import com.example.ApSystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite llamadas desde React (localhost:5173/3000)
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);// Delegamos toda la lógica al AuthService
        return ResponseEntity.ok(response);// Devolvemos el token al frontend
    }



    // endpoint opcional para registrar (solo ejemplo)
    // @PostMapping("/register") ...
}
