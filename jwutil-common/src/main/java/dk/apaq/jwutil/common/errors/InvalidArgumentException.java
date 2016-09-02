package dk.apaq.jwutil.common.errors;

/**
 * Exception for invalid arguments.
 */
public class InvalidArgumentException extends JWUtilException {

    private final String parameter;

    /**
     * Constructs a new invalid argument exception with the specified detail message.
     * @param parameter the parameter for which the value is invalid. 
     * @param message the detail message. The detail message is saved for later retrieval by the <code>InvalidArgumentException.getMessage()</code> method.
     */
    public InvalidArgumentException(String parameter, String message) {
        super(message);
        this.parameter = parameter;
    }

    /**
     * Retrieves the parameter name.
     * @return The parameter name.
     */
    public String getParameter() {
        return parameter;
    }
    
    
    
}
