package dk.apaq.orderly.model;

public class IceCandidate {

    private String candidate;
    private String sdpMid;
    private int sdpMLineIndex;

    public IceCandidate() {
    }

    
    public IceCandidate(String candidate, String sdpMid, int sdpMLineIndex) {
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
    }
    
    

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(int sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }
    
    
    public static IceCandidate fromOrg(org.kurento.client.IceCandidate candidate) {
        return new IceCandidate(candidate.getCandidate(), candidate.getSdpMid(), candidate.getSdpMLineIndex());
    }
    
    public org.kurento.client.IceCandidate toOrg() {
        return new org.kurento.client.IceCandidate(getCandidate(), getSdpMid(), getSdpMLineIndex());
    }
    
}
