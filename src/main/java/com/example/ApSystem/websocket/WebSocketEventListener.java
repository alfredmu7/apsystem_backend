package com.example.ApSystem.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(
            OnlineUserService onlineUserService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.onlineUserService = onlineUserService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Se ejecuta CUANDO el cliente STOMP termina el CONNECT correctamente
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {

        // 🔒 Si no hay usuario autenticado, NO registrar nada
        if (event.getUser() == null) {
            System.out.println("⚠️ WebSocket connected without user (ignored)");
            return;
        }

        String email = event.getUser().getName();

        // ✅ Set evita duplicados automáticamente
        onlineUserService.userConnected(email);

        // 🔔 Notificar a todos los clientes
        messagingTemplate.convertAndSend(
                "/topic/online-users",
                onlineUserService.getOnlineUsers()
        );

        System.out.println("User connected via WebSocket: " + email);
    }

    /**
     * Se ejecuta CUANDO la sesión WebSocket se cierra
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        if (event.getUser() == null) {
            System.out.println("⚠️ WebSocket disconnected without user (ignored)");
            return;
        }

        String email = event.getUser().getName();

        onlineUserService.userDisconnected(email);

        messagingTemplate.convertAndSend(
                "/topic/online-users",
                onlineUserService.getOnlineUsers()
        );

        System.out.println("💀 User disconnected from WebSocket: " + email);
    }
}
