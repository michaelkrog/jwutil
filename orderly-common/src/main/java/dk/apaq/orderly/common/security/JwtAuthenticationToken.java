package dk.apaq.orderly.common.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String subject;
    private final String token;
    
    public JwtAuthenticationToken(String subject, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.subject = subject;
        this.token = token;
    }

    
    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return subject;
    }
    
}
