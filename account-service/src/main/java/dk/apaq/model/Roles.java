package dk.apaq.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.Assert;

/**
 *
 * @author michael
 */
public class Roles {
    public static final String SEPARATOR = "_";
    public static final String PREFIX_UNITROLE = "UNITROLE";
    public static final String PREFIX_SYSTEMROLE = "ROLE";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SYSTEM = "ROLE_SYSTEM";
    public static final String ROLE_PUBLIC = "ROLE_PUBLIC";
    public static final String ROLE_SECRET = "ROLE_SECRET";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ORGROLE_ADMIN = "ADMIN";
    public static final String ORGROLE_USER = "USER";
    public static final String ORGROLE_PUBLIC = "PUBLIC";
    public static final String ORGROLE_SECRET = "SECRET";
    
    
    public static String generateUnitAccessRole(String unitId, String role) {
        Assert.notNull(unitId, "unitId must be specified.");
        Assert.notNull(role, "role must be specified.");
        return PREFIX_UNITROLE + SEPARATOR + unitId + SEPARATOR + role;
    }
    
    public static List<String> resolveRolesByPrefix(List<String> roles, String prefix) {
        List<String> result = new ArrayList<>();
        for(String role: roles) {
            if(role.startsWith(prefix + SEPARATOR)) {
                result.add(role);
            }
        }
        return result;
    }
    
    public static boolean isAdminRole(String role) {
        return role.endsWith(SEPARATOR + ORGROLE_ADMIN);
    }
    
    public static boolean isUnitRole(String role) {
        return role.startsWith(PREFIX_UNITROLE + SEPARATOR);
    }
    
    public static String getUnitId(String role) {
        if(isUnitRole(role)) {
            String[] elements = role.split(SEPARATOR);
            return elements[1];
        } else {
            return null;
        }
    } 
    
    
}
