package dk.apaq.orderly.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public enum BroadcastMessageType {
    @JsonProperty("presenter") Presenter, 
    @JsonProperty("presenterResponse") PresenterResponse, 
    @JsonProperty("viewer") Viewer, 
    @JsonProperty("viewerResponse") ViewerResponse, 
    @JsonProperty("onIceCandidate") OnIceCandidate, 
    @JsonProperty("iceCandidate") IceCandidate, 
    @JsonProperty("stop") Stop,
    @JsonProperty("stopCommunication") StopCommunication
}
