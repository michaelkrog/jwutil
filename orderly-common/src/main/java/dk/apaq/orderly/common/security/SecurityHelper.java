package dk.apaq.orderly.common.security;

import dk.apaq.orderly.common.security.Roles;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper class for Security related information.
 */
public class SecurityHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityHelper.class);
    
    
    
    /**
     * Whether current authentication is admin.
     * @return True/False 
     */
    public static boolean isAdmin() {
        return hasRole(Roles.ROLE_ADMIN);
    }
    
    /**
     * Whether current authentication is system user.
     * @return True/False
     */
    public static boolean isSystem() {
        return hasRole(Roles.ROLE_SYSTEM);
    }
    
    public static boolean hasRole(String role) {
        LOG.debug("Retrieving whether current person has role [role={}]", role);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            if (auth.getAuthorities().stream().anyMatch((ga) -> (role.equals(ga.getAuthority())))) {
                return true;
            }
        }
        return false;
    }
    
    public static void loginSystemUser() {
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(Roles.ROLE_SYSTEM);
        UsernamePasswordAuthenticationToken systemUser = new UsernamePasswordAuthenticationToken("system", null, Collections.singletonList(auth));
        SecurityContextHolder.getContext().setAuthentication(systemUser);
    }
    
    public static void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
