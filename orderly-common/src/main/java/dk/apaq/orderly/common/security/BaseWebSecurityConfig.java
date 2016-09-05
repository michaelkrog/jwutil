package dk.apaq.orderly.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;

public abstract class BaseWebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final Logger LOG = LoggerFactory.getLogger(BaseWebSecurityConfig.class);
    private final AuthenticationEntryPoint authenticationEntryPoint = new RestAuthenticationEntryPoint();
    @Autowired
    private TokenParser tokenParser;
    
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;


    
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
                .and().authorizeRequests().anyRequest().authenticated()
                .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                .and().csrf().disable().headers().frameOptions().disable();
    }

    protected XAuthTokenConfigurer securityConfigurerAdapter() {
        return new XAuthTokenConfigurer(tokenParser, authenticationEntryPoint);
    }
    
}
