package dk.apaq.orderly.security;

import javax.annotation.Resource;
import dk.apaq.orderly.common.security.RestAuthenticationEntryPoint;
import dk.apaq.orderly.common.security.TokenParser;
import dk.apaq.orderly.common.security.XAuthTokenConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
    private static final String PROPERTY_NAME_SECURITY_CREDENTIALS = "orderly.security.credentials";
    
    private final AuthenticationEntryPoint authenticationEntryPoint = new RestAuthenticationEntryPoint();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private Environment env;
        
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenParser tokenParser;
    
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOG.info("Configuring HTTPSecurity");

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
            .and()
                .apply(securityConfigurerAdapter())
            .and()
                .authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.POST, "/accounts").permitAll()
                .and().authorizeRequests().antMatchers("/manage/health").permitAll()
                .and().authorizeRequests().antMatchers("/manage/info").permitAll()
                .and().authorizeRequests().antMatchers("/manage/**").hasRole("ADMIN")
                .and().authorizeRequests().anyRequest().authenticated()
                .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint)
            .and()   
                .csrf()
                .disable()
                .headers()
                .frameOptions()
                .disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        LOG.info("Configuring authentication manager.");
        

        String adminCredentialString = env.getProperty(PROPERTY_NAME_SECURITY_CREDENTIALS);
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> configurer = auth.inMemoryAuthentication();
        
        if(adminCredentialString != null && !"".equals(adminCredentialString)) {
            String[] credentials = adminCredentialString.split(",");
        
                for(String credential : credentials) {
                String[] credentialData = credential.split(":");
                if(credentialData.length == 3) {
                    LOG.debug("Adding system credential [username={}; role={}]", credentialData[0], credentialData[2]);
                    configurer.withUser(credentialData[0]).password(credentialData[1]).roles(credentialData[2]);
                } else {
                    LOG.info("Invalid credentials. Must have 3 parts to form username, password and role. [data={}]", (Object) credentialData);
                }
            }
        }
        
        configurer.and().userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    private XAuthTokenConfigurer securityConfigurerAdapter() {
      return new XAuthTokenConfigurer(tokenParser, authenticationEntryPoint);
    }

}
