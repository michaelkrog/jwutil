package dk.apaq.orderly.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Filters incoming requests and installs a Spring Security principal
 * if a header corresponding to a valid user is found.
 */
public class XAuthTokenFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(XAuthTokenFilter.class);
    private static final String XAUTH_TOKEN_HEADER_NAME = "x-auth-token";

    private final TokenParser tokenParser;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public XAuthTokenFilter(TokenParser tokenProvider, AuthenticationEntryPoint authenticationEntryPoint) {
        this.tokenParser = tokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        
        try {
            
            String authToken = request.getHeader(XAUTH_TOKEN_HEADER_NAME);
            if (StringUtils.hasText(authToken)) {
                if (this.tokenParser.validateToken(authToken)) {
                    UserDetails details = this.tokenParser.getUserDetailsFromToken(authToken);
                    
                    JwtAuthenticationToken token = new JwtAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities());
                    token.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(token);
                } else {
                    throw new BadCredentialsException("The token used is invalid.");
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();

            LOG.debug("Authentication request for failed: {}", ex);
            authenticationEntryPoint.commence(request, response, ex);
        }
    }
}
