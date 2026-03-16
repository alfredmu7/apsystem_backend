package com.example.ApSystem.service;

import com.example.ApSystem.dto.AuthRequest;
import com.example.ApSystem.dto.AuthResponse;
import com.example.ApSystem.security.JwtUtils;
import com.example.ApSystem.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Servicio que maneja login, autenticación y generación de token JWT.
 */
@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authManager, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Login: autentica usuario y genera JWT.
     */
    public AuthResponse login(AuthRequest request) {
        // 1️⃣ Autenticar usuario
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2️⃣ Extraer usuario autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 3️⃣ Generar token seguro
        String token = jwtUtils.generateToken(userDetails);

        // 4️⃣ Retornar token al frontend
        return new AuthResponse(token, userDetails.getUsername(), userDetails.getAuthorities());
    }
}
