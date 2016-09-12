package dk.apaq.orderly.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BroadcastMessageResponseType {
    @JsonProperty("accepted") Accepted,
    @JsonProperty("rejected") Rejected
}
