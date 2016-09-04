package dk.apaq.orderly.security;

import dk.apaq.orderly.model.Account;
import dk.apaq.orderly.common.security.Roles;
import java.io.Serializable;
import java.util.List;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class StandardSecurityModel implements PermissionEvaluator {

    private static final String PERMISSION_READ = "read";
    private static final String PERMISSION_ADMIN = "admin";
    private static final String PERMISSION_WRITE = "write";
    private static final String PERMISSION_DELETE = "delete";

    private boolean hasOrgPermission(Authentication authentication, String unitId, Object permission) {
        if(unitId != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String[] roleInfo = authority.getAuthority().split("_");
                if (Roles.PREFIX_UNITROLE.equals(roleInfo[0]) && unitId.equals(roleInfo[1]) && roleInfo.length > 2) {
                    switch (roleInfo[2]) {
                        case Roles.UNITROLE_ADMIN:
                            return true;
                        case Roles.UNITROLE_USER:
                            return PERMISSION_READ.equals(permission) || 
                                    PERMISSION_WRITE.equals(permission) || 
                                    PERMISSION_DELETE.equals(permission);
                    }
                }
            }
        } else if(PERMISSION_WRITE.equals(permission)) {
            // Anyone can create a new unit
            return true;
        }
        
        return false;
        
    }

    private boolean hasAccountPermission(Authentication authentication, String accountId, Object permission) {
        if (authentication.getPrincipal() instanceof AccountUserDetails) {
            AccountUserDetails aud = (AccountUserDetails) authentication.getPrincipal();
            
            // Authentication is same account as the one being access, then return true.
            if (accountId.equals(aud.getAccount().getId())) {
                return true;
            }
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            // Any user or public key is allowed to CREATE new accounts
            if ((Roles.ROLE_PUBLIC.equals(authority.getAuthority()) || Roles.ROLE_USER.equals(authority.getAuthority())) && 
                    accountId == null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if(isSuperUser(authentication)) {
            return true;
        }
        
        if (targetDomainObject instanceof Account) {
            Account account = (Account) targetDomainObject;
            
            // Test if authentication can read from any of the units it belong to. If so, then allow to read this account.
            List<String> orgRoles = Roles.resolveRolesByPrefix(account.getRoles(), Roles.PREFIX_UNITROLE);
            for(String role : orgRoles) {
                String orgId = Roles.getUnitId(role);
                if(PERMISSION_READ.equals(permission) && hasOrgPermission(authentication, orgId, PERMISSION_READ)) {
                    return true;
                }
            }
            
            return hasAccountPermission(authentication, account.getId(), permission);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(isSuperUser(authentication)) {
            return true;
        }
        
        if (targetId instanceof String) {
            String id = (String) targetId;
            if (id.startsWith(Account.ABBREVIATION)) {
                return hasAccountPermission(authentication, id, permission);
            }
        }
        return false;
    }

    private boolean isSuperUser(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (Roles.ROLE_ADMIN.equals(authority.getAuthority())
                    || Roles.ROLE_SYSTEM.equals(authority.getAuthority())
                    || Roles.ROLE_SECRET.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

}
