package com.example.ApSystem.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Set;

@Controller
public class OnlineUserController {

    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate messagingTemplate;

    public OnlineUserController(OnlineUserService onlineUserService, SimpMessagingTemplate messagingTemplate) {
        this.onlineUserService = onlineUserService;
        this.messagingTemplate = messagingTemplate;
    }

    // El usuario se registra cuando manda el primer mensaje
    @MessageMapping("/online-users")
    public void requestOnlineUsers(Principal principal) {
        String username = principal != null ? principal.getName() : "anonymous";
        onlineUserService.userConnected(username);

        // Publica la lista actualizada a todos los clientes
        messagingTemplate.convertAndSend("/topic/online-users", onlineUserService.getOnlineUsers());

        System.out.println(" User registered via /app/online-users: " + username);
    }
}