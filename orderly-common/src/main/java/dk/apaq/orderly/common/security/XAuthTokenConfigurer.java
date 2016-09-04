package dk.apaq.orderly.common.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class XAuthTokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenParser tokenParser;
    private final UserDetailsService detailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public XAuthTokenConfigurer(UserDetailsService detailsService, TokenParser tokenParser, AuthenticationEntryPoint authenticationEntryPoint) {
        this.detailsService = detailsService;
        this.tokenParser = tokenParser;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        XAuthTokenFilter customFilter = new XAuthTokenFilter(detailsService, tokenParser, authenticationEntryPoint);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
