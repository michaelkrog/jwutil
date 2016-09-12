package dk.apaq.orderly.config;

import dk.apaq.orderly.CallHandler;
import dk.apaq.orderly.CallHandler2;
import dk.apaq.orderly.common.security.TokenParser;
import dk.apaq.orderly.common.security.XAuthTokenFilter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
public class WebsocketConfig implements WebSocketConfigurer {

    @Autowired
    private CallHandler2 callHandler;
    
    @Autowired
    private TokenParser tokenParser;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        
        registry.addHandler(callHandler, "/call").setAllowedOrigins("*").addInterceptors(new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                XAuthTokenFilter.authenticateRequest(tokenParser, request);
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
                
            }
        });
    }
}
