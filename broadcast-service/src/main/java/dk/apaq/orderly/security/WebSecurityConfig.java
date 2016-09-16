package dk.apaq.orderly.security;

import dk.apaq.orderly.common.security.BaseWebSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class WebSecurityConfig extends BaseWebSecurityConfig {
    
    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOG.info("Configuring HTTPSecurity");
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and().apply(securityConfigurerAdapter())
                .and().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .and().authorizeRequests().antMatchers("/manage/health").permitAll()
                .and().authorizeRequests().antMatchers("/manage/info").permitAll()
                .and().authorizeRequests().antMatchers("/manage/**").hasRole("ADMIN")
                .and().authorizeRequests().antMatchers("/broadcasts/actions/call").permitAll()
                .and().authorizeRequests().anyRequest().authenticated()
                .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                .and().csrf().disable().headers().frameOptions().disable();
    }
}
