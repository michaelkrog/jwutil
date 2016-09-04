package dk.apaq.orderly.model;


public class RecaptchaRequest {

    private String secret;
    private String userResponse;
    private String remoteIp;

    public RecaptchaRequest(String secret, String userResponse, String remoteIp) {
        this.secret = secret;
        this.userResponse = userResponse;
        this.remoteIp = remoteIp;
    }
    
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(String userResponse) {
        this.userResponse = userResponse;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
    
}
