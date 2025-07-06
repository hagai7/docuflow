package com.example.docuflow.config;

import com.example.docuflow.service.ProgressWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ProgressWebSocketHandler handler;

    public WebSocketConfig(ProgressWebSocketHandler handler) {
        this.handler = handler;
    }

    /**
     * Registers WebSocket handlers with specific endpoint paths.
     * This tells Spring to handle WebSocket connections at /ws/progress with your custom handler.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/progress")
                .setAllowedOrigins("*");
    }
}
