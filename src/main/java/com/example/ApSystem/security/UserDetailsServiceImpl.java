package com.example.ApSystem.security;

import com.example.ApSystem.repository.UserRepository;
import com.example.ApSystem.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Inyectamos el repositorio por constructor
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // .replaceAll("[^\\x20-\\x7E]", "") elimina CUALQUIER carácter que no sea
        // una letra, número o símbolo estándar (borra basura invisible de la DB)
        String cleanPassword = user.getPassword().replaceAll("[^\\x20-\\x7E]", "").trim();

        // Log para verificar que la longitud sea exactamente 60
        System.out.println("DEBUG - Hash limpio: " + cleanPassword);
        System.out.println("DEBUG - Longitud: " + cleanPassword.length());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                cleanPassword,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }}