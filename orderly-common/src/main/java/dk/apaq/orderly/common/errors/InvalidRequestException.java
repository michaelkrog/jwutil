package dk.apaq.orderly.common.errors;

/**
 * Exception for requests that cannot be fulfilled.
 */
public class InvalidRequestException extends JWUtilException {

    /**
     * Constructs a new invalid request exception with the specified detail message.
     * @param message the detail message. The detail message is saved for later retrieval by the <code>InvalidRequestException.getMessage()</code> method.
     */
    public InvalidRequestException(String message) {
        super(message);
    }
    
}
