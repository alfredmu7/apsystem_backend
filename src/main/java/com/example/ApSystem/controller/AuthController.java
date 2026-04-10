package com.example.ApSystem.controller;

import com.example.ApSystem.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5174")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // Inyectamos todo por constructor para evitar errores de NullPointerException
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        try {
            // Spring busca el Bean de BCryptPasswordEncoder automáticamente aquí
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.get("email"),
                            loginRequest.get("password")
                    )
            );

            UserDetails user = (UserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateToken(user);
            return Map.of("token", token);

        } catch (Exception e) {
            // Esto imprimirá el error real en la consola de IntelliJ
            e.printStackTrace();
            throw e;
        }
    }}