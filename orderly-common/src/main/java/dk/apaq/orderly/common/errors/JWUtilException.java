package dk.apaq.orderly.common.errors;


public class JWUtilException extends RuntimeException {

    /**
     * Constructs a new skveege exception with the specified detail message.
     * @param message the detail message. The detail message is saved for later retrieval by the <code>SkveegeException.getMessage()</code> method.
     */
    public JWUtilException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * @param message the detail message. The detail message is saved for later retrieval by the <code>SkveegeException.getMessage()</code> method.
     * @param cause the cause (which is saved for later retrieval by the <code>SkveegeException.getCause()</code> method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public JWUtilException(String message, Throwable cause) {
        super(message, cause);
    }
}
