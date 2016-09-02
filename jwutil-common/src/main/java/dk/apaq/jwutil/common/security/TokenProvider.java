package dk.apaq.jwutil.common.security;

import dk.apaq.jwutil.common.util.Hashids;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Provider for JWT based Xauth tokens.
 */
public class TokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TokenProvider.class);
    
    private final String secretKey;
    private final int defaultTokenValidity;
    private final Hashids hashids = new Hashids();

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

    public UserDetails getUserDetailsFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        try {

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(authToken).getBody();
            
            //OK, we can trust this JWT. If it has not expired then it is okay.
            return new User(claims.getSubject(), authToken, 
                            authoritiesFromRoles(claims.get("roles", List.class)));
            
        } catch (SignatureException e) {
            return null;
        }
    }

    public boolean validateToken(String authToken) {
        try {

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(authToken).getBody();
            
            //OK, we can trust this JWT. If it has not expired then it is okay.
            Date expires = claims.getExpiration();
            Date now = new Date();
            boolean valid = expires == null || expires.after(now);
            return valid;

        } catch (JwtException e) {
            LOG.debug("Invalid JWT token. ", e);
            return false;
        }
        
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
    private List<GrantedAuthority> authoritiesFromRoles(List<String> roles) {
        List<GrantedAuthority> auths = new ArrayList<>();
        
        for(String role: roles) {
            auths.add(new SimpleGrantedAuthority(role));
        }
        return auths;
    }
    
}
