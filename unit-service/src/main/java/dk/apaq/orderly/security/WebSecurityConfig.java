package dk.apaq.orderly.security;

import dk.apaq.orderly.common.security.BaseWebSecurityConfig;
import dk.apaq.orderly.common.security.RestAuthenticationEntryPoint;
import dk.apaq.orderly.common.security.TokenParser;
import dk.apaq.orderly.common.security.XAuthTokenConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class WebSecurityConfig extends BaseWebSecurityConfig {
}
