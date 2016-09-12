package dk.apaq.orderly.model;


public class BroadcastMessageResponse {
    private BroadcastMessageType id;
    private BroadcastMessageResponseType response;
    private String message;
    private IceCandidate candidate;
    private String sdpAnswer;

    public BroadcastMessageResponse() {
    }
    
    public BroadcastMessageResponse(BroadcastMessageType id) {
        this.id = id;
    }
    

    public BroadcastMessageResponse(BroadcastMessageType id, BroadcastMessageResponseType response, String message) {
        this.id = id;
        this.response = response;
        this.message = message;
    }
    
    public BroadcastMessageResponse(BroadcastMessageType id, BroadcastMessageResponseType response) {
        this.id = id;
        this.response = response;
    }
    
    public BroadcastMessageResponse(BroadcastMessageType id, IceCandidate candidate) {
        this.id = id;
        this.candidate = candidate;
    }
    

    public BroadcastMessageType getId() {
        return id;
    }

    public void setId(BroadcastMessageType id) {
        this.id = id;
    }

    public BroadcastMessageResponseType getResponse() {
        return response;
    }

    public void setResponse(BroadcastMessageResponseType response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public IceCandidate getCandidate() {
        return candidate;
    }

    public void setCandidate(IceCandidate candidate) {
        this.candidate = candidate;
    }

    public String getSdpAnswer() {
        return sdpAnswer;
    }

    public void setSdpAnswer(String sdpAnswer) {
        this.sdpAnswer = sdpAnswer;
    }
    
}
