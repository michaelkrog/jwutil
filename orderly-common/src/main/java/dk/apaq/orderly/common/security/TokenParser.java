package dk.apaq.orderly.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenParser {
    
    private static final Logger LOG = LoggerFactory.getLogger(TokenParser.class);
    
    private final String secretKey;
    
    public TokenParser(String secretKey) {
        this.secretKey = secretKey;
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
    
    private List<GrantedAuthority> authoritiesFromRoles(List<String> roles) {
        List<GrantedAuthority> auths = new ArrayList<>();
        
        for(String role: roles) {
            auths.add(new SimpleGrantedAuthority(role));
        }
        return auths;
    }
}
