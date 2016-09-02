package dk.apaq.config;

import dk.apaq.jwutil.common.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "skveege.security.xauth")
public class XAuthConfig {

    private static final Logger LOG = LoggerFactory.getLogger(XAuthConfig.class);
    
    private String secret = "coffee2Go!";
    private int tokenValidityInMinutes = 480;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setTokenValidityInMinutes(int tokenValidityInMinutes) {
        this.tokenValidityInMinutes = tokenValidityInMinutes;
    }
    
    @Bean
    public TokenProvider tokenProvider(){
        LOG.info("Initializing tokenProvider.");
        return new TokenProvider(secret, tokenValidityInMinutes);
    }
}