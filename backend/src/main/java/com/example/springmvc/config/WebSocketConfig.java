package com.example.springmvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint "/ws" chỉ để Client kết nối và lắng nghe
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173") // URL Frontend
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Chỉ kích hoạt Broker "/topic" để Server bắn tin thông báo
        registry.enableSimpleBroker("/topic");
    }
}