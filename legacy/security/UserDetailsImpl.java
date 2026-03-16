package com.example.ApSystem.security;

import com.example.ApSystem.model.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


     //Factory method: recibe entidad User (modelo de BD) y la convierte a un objeto que Spring Security entiende (UserDetailsImpl).
    public static UserDetailsImpl build(User user) {

        List<GrantedAuthority> authorities = user.getRoles()// Convertimos cada Role (ej: ROLE_USER) en un GrantedAuthority
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());

        // Creamos y devolvemos la instancia con id, username, password y authorities
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    // Los siguientes métodos son parte de la interfaz UserDetails.
    // Aquí devolvemos true siempre para indicar que la cuenta está "OK".
    // Si quieres gestionar cuentas bloqueadas/expiradas/etc, reemplaza por checks basados en tu User.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
