package dk.apaq.orderly.common.config;

import dk.apaq.orderly.common.security.TokenParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "orderly.security.xauth")
public class TokenParserConfig {
    private static final Logger LOG = LoggerFactory.getLogger(TokenParserConfig.class);
    
    private String secret = "coffee2Go!";
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    @Bean
    public TokenParser tokenParser() {
        LOG.info("Initializing tokenParser.");
        
        return new TokenParser(secret);
    }
}
