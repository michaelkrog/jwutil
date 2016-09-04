package dk.apaq.orderly.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RecaptchaResponse {

    private boolean success;
    @JsonProperty(value="error-codes")
    private String[] errorCodes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String[] getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(String[] errorCodes) {
        this.errorCodes = errorCodes;
    }
    
}
