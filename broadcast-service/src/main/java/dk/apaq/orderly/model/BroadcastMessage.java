package dk.apaq.orderly.model;


public class BroadcastMessage {
    private BroadcastMessageType id;
    private IceCandidate candidate;
    private String sdpOffer;

    public BroadcastMessageType getId() {
        return id;
    }

    public void setId(BroadcastMessageType id) {
        this.id = id;
    }

    public IceCandidate getCandidate() {
        return candidate;
    }

    public void setCandidate(IceCandidate candidate) {
        this.candidate = candidate;
    }

    public String getSdpOffer() {
        return sdpOffer;
    }

    public void setSdpOffer(String sdpOffer) {
        this.sdpOffer = sdpOffer;
    }
    
    
    
    
}
