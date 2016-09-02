package dk.apaq.jwutil.common.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class XAuthTokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final UserDetailsService detailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public XAuthTokenConfigurer(UserDetailsService detailsService, TokenProvider tokenProvider, AuthenticationEntryPoint authenticationEntryPoint) {
        this.detailsService = detailsService;
        this.tokenProvider = tokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        XAuthTokenFilter customFilter = new XAuthTokenFilter(detailsService, tokenProvider, authenticationEntryPoint);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
