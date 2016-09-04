package dk.apaq.orderly.security;

import dk.apaq.orderly.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author michael
 */
public class AccountContextHolder {
    
    private static final Logger LOG = LoggerFactory.getLogger(AccountContextHolder.class);
    
    public static Account getAccount(Authentication auth) {
        LOG.debug("Retrieving person from authentication. [auth={}]", auth);
        Account person = null;
        Object principal = auth == null ? null : auth.getPrincipal();
        if(principal != null && principal instanceof AccountUserDetails) {
            person = ((AccountUserDetails)principal).getAccount();
        }
        return person;
    }
    
    public static Account getCurrent() {
        LOG.debug("Retrieving current person");
        return getAccount(SecurityContextHolder.getContext().getAuthentication());
    }
}
