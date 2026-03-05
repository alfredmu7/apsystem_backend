package com.example.ApSystem.websocket.security;

import com.example.ApSystem.security.JwtUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    public WebSocketAuthInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                List<String> auth = accessor.getNativeHeader("Authorization");
                String username; // declarar una sola vez

                if (auth != null && !auth.isEmpty()) {
                    String token = auth.get(0).replace("Bearer ", "");
                    username = jwtUtils.getUsernameFromToken(token); // asignar, no redeclarar
                    accessor.setUser(new WebSocketPrincipal(username));
                    System.out.println("💡 WebSocket Principal set: " + username);
                } else {
                    username = "anonymous-" + System.currentTimeMillis();
                    accessor.setUser(new WebSocketPrincipal(username));
                }

                // No necesitas asignar UsernamePasswordAuthenticationToken si ya usas WebSocketPrincipal
                //System.out.println("💡 User connected via WebSocket: " + username);

            } catch (Exception e) {
                String username = "anonymous-" + System.currentTimeMillis();
                accessor.setUser(new WebSocketPrincipal(username));
                System.out.println("💡 Exception, user set as: " + username);
            }
        }

        return message;
    }
}
