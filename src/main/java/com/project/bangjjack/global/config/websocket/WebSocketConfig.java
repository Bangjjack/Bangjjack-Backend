package com.project.bangjjack.global.config.websocket;

import com.project.bangjjack.domain.chat.presentation.websocket.ChatErrorFrameHandler;
import com.project.bangjjack.domain.chat.presentation.websocket.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig extends WebSocketMessageBrokerConfigurationSupport {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;
    private final ChatErrorFrameHandler chatErrorFrameHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/sub");
        config.setApplicationDestinationPrefixes("/pub");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }

    @Bean
    @Override
    public WebSocketHandler subProtocolWebSocketHandler(
            AbstractSubscribableChannel clientInboundChannel,
            AbstractSubscribableChannel clientOutboundChannel) {
        SubProtocolWebSocketHandler handler =
                (SubProtocolWebSocketHandler) super.subProtocolWebSocketHandler(clientInboundChannel, clientOutboundChannel);
        handler.getProtocolHandlers().stream()
                .filter(h -> h instanceof StompSubProtocolHandler)
                .map(h -> (StompSubProtocolHandler) h)
                .findFirst()
                .ifPresent(h -> h.setErrorHandler(chatErrorFrameHandler));
        return handler;
    }
}
