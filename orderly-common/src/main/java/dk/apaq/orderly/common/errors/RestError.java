package dk.apaq.orderly.common.errors;


public class RestError {

    public enum ErrorType {
        InvalidRequestError, ApiError
    }
    
    private final String message;
    private final ErrorType type;

    public RestError(ErrorType type, String message) {
        this.message = message;
        this.type = type;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public ErrorType getType() {
        return type;
    }
}
