package dk.apaq.security;

import dk.apaq.model.Account;
import dk.apaq.model.Roles;
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
    
    public static Account getAccount(Authentication auth) {
        LOG.debug("Retrieving person from authentication. [auth={}]", auth);
        Account person = null;
        Object principal = auth == null ? null : auth.getPrincipal();
        if(principal != null && principal instanceof AccountUserDetails) {
            person = ((AccountUserDetails)principal).getAccount();
        }
        return person;
    }
    
    public static Account getCurrentAccount() {
        LOG.debug("Retrieving current person");
        return getAccount(SecurityContextHolder.getContext().getAuthentication());
    }
    
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
    
    /**
     * Retrieves the default organization id or null if the account is not connected to an organization.
     * @param account The account to get the defaut organization id for.
     * @return The default organization id or null.
     */
    public static String getDefaultOrganizationId(Account account) {
        if(account == null) {
            return null;
        }
        
        List<String> roles = Roles.resolveRolesByPrefix(account.getRoles(), Roles.PREFIX_UNITROLE);
        if(roles.isEmpty()) {
            return null;
        } else {
            String firstRole = roles.get(0);
            String[] parts = firstRole.split("_");
            return parts.length == 3 ? parts[1] : null;
        }
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
