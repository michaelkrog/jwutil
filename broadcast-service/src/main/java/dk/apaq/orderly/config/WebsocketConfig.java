package dk.apaq.orderly.config;

import dk.apaq.orderly.CallHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

public class WebsocketConfig implements WebSocketConfigurer {

    @Autowired
    private CallHandler callHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(callHandler, "/call");
    }
}
