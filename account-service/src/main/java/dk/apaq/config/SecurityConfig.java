package dk.apaq.config;

import dk.apaq.security.AccountUserDetailsService;
import javax.annotation.Resource;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

    private static final String PROPERTY_NAME_SALT = "skveege.security.salt";
    
    
    @Resource
    private Environment env;
    
    @Bean
    public UserDetailsService userDetailsService() {
        return new AccountUserDetailsService();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        String salt = env.getProperty(PROPERTY_NAME_SALT, "deadSea");
        PasswordEncoder encoder = new StandardPasswordEncoder(salt);
        return encoder;
    }
    
    /**@Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();

        expressionHandler.setPermissionEvaluator(new StandardSecurityModel(organizationRepository));
        return expressionHandler;
    }**/

}
