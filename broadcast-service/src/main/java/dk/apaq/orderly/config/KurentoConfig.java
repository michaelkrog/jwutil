package dk.apaq.orderly.config;

import dk.apaq.orderly.CallHandler;
import dk.apaq.orderly.CallHandler2;
import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KurentoConfig {

    @Bean
    public CallHandler2 callHandler() {
        return new CallHandler2();
    }

    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create();
    }
}
