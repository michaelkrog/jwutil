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
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Filters incoming requests and installs a Spring Security principal
 * if a header corresponding to a valid user is found.
 */
public class XAuthTokenFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(XAuthTokenFilter.class);
    private static final String XAUTH_TOKEN_HEADER_NAME = "x-auth-token";
    private static final String WEBSOCKET_TOKEN_HEADER_NAME = "Sec-WebSocket-Protocol";
    
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
            authenticateRequest(request);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();

            LOG.debug("Authentication request for failed: {}", ex);
            authenticationEntryPoint.commence(request, response, ex);
        }
    }

    public void authenticateRequest(final HttpServletRequest request) throws BadCredentialsException {
        XAuthTokenFilter.authenticateRequest(tokenParser, request);
    }
    
    public static String authenticateRequest(TokenParser tokenParser, final HttpServletRequest request) throws BadCredentialsException {
        String authToken = request.getHeader(XAUTH_TOKEN_HEADER_NAME);
        if(authToken == null) {
            authToken = request.getHeader(WEBSOCKET_TOKEN_HEADER_NAME);
        }
        authenticateTokenAndApply(tokenParser, authToken);
        return authToken;
    }
    
    public static String authenticateRequest(TokenParser tokenParser, final ServerHttpRequest request) throws BadCredentialsException {
        String authToken = request.getHeaders().getFirst(XAUTH_TOKEN_HEADER_NAME);
        if(authToken == null) {
            authToken = request.getHeaders().getFirst(WEBSOCKET_TOKEN_HEADER_NAME);
        }
        authenticateTokenAndApply(tokenParser, authToken);
        return authToken;
    }
    
    public static void authenticateTokenAndApply(TokenParser tokenParser, final String authToken) throws BadCredentialsException {
        Authentication auth = authenticateToken(tokenParser, authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    public static Authentication authenticateToken(TokenParser tokenParser, final String authToken) throws BadCredentialsException {
        if (StringUtils.hasText(authToken)) {
            if (tokenParser.validateToken(authToken)) {
                UserDetails details = tokenParser.getUserDetailsFromToken(authToken);
                
                JwtAuthenticationToken token = new JwtAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities());
                token.setAuthenticated(true);
                return token;
                
            } else {
                throw new BadCredentialsException("The token used is invalid.");
            }
        }
        return null;
    }
}
