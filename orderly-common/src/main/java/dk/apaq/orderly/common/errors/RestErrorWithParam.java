package dk.apaq.orderly.common.errors;


public class RestErrorWithParam extends RestError {

    private final String param;
    
    public RestErrorWithParam(ErrorType type, String message, String param) {
        super(type, message);
        this.param = param;
    }

    public String getParam() {
        return param;
    }


}
