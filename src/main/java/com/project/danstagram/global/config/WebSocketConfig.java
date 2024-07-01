package com.project.danstagram.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/direct/ws")     // socket 연결 url
                .setAllowedOriginPatterns("*")        // CORS 허용 범위
                .withSockJS();                        // 낮은 버전 browser에도 사용 가능하도록 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 메시지를 구독하는 요청 endpoint
        config.enableSimpleBroker("/topic");
        // 메시지를 발행하는 endpoint
        config.setApplicationDestinationPrefixes("/pub");
    }
}
