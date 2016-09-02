package dk.apaq.jwutil.common.errors;

/**
 * Exception for when a resource cannot be found.
 */
public class ResourceNotFoundException extends JWUtilException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    
}
