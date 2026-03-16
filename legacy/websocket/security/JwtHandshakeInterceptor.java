package com.example.ApSystem.websocket.security;

import com.example.ApSystem.security.JwtService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = null;

        // 1️⃣ Intentar leer token desde header HTTP (por si algún cliente lo envía)
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String authHeader = servletRequest.getServletRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        // 2️⃣ Si no está en header, intentar leer desde query param
        if (token == null) {
            MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
            token = params.getFirst("token");
        }

        // 3️⃣ Validar token si existe
        if (token != null) {
            try {
                String email = jwtService.extractUsername(token);
                System.out.println("💡 JWT valid, user: " + email);
                attributes.put("user", email); // se guarda en session
            } catch (Exception e) {
                System.out.println("❌ Invalid JWT: " + e.getMessage());
                return false; // aborta handshake si token inválido
            }
        } else {
            System.out.println("⚠️ No JWT provided, user will be anonymous");
            attributes.put("user", "anonymous-" + System.currentTimeMillis());
        }

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // nada que hacer después del handshake
    }
}
