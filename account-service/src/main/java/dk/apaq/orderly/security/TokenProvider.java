package dk.apaq.orderly.security;

import dk.apaq.orderly.common.security.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

/**
 * Provider for JWT based Xauth tokens.
 */
public class TokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TokenProvider.class);
    
    private final String secretKey;
    private final int defaultTokenValidity;
    
    public TokenProvider(String secretKey, int tokenValidity) {
        this.secretKey = secretKey;
        this.defaultTokenValidity = tokenValidity;
    }

    public Token createToken(UserDetails userDetails) {
        return createToken(userDetails, null);
    }
    
    public Token createToken(UserDetails userDetails, Integer tokenValidity) {
        if(tokenValidity == null) {
            tokenValidity = defaultTokenValidity;
        }
        
        Instant expires = Instant.now().plusSeconds(tokenValidity * 60);
        String auhToken = Jwts.builder()
                .setExpiration(Date.from(expires))
                .setSubject(userDetails.getUsername())
                .claim("roles", rolesFromAuthorities(userDetails.getAuthorities()))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return new Token(auhToken, expires.toEpochMilli());
    }
    
    private String[] rolesFromAuthorities(Collection<? extends GrantedAuthority> auths) {
        String[] roles = new String[auths.size()];
        
        int count = 0;
        for(GrantedAuthority auth : auths) {
            roles[count] = auth.getAuthority();
            count++;
        }

        return roles;
    }
    
}
