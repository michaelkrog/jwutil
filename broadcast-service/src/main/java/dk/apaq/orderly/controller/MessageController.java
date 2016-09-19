package dk.apaq.orderly.controller;

import dk.apaq.orderly.model.BroadcastMessageResponse;
import dk.apaq.orderly.model.IceCandidate;
import dk.apaq.orderly.model.JoinBroadcastMessage;
import dk.apaq.orderly.model.StartBroadcastMessage;
import dk.apaq.orderly.service.BroadcastService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    
    @Autowired
    private BroadcastService service;
    
    @MessageMapping("/session") 
    @SendToUser
    public String getSession(SimpMessageHeaderAccessor headerAccessor) {
        return headerAccessor.getSessionId();
    }
    
    @MessageMapping("/{unitId}/present") 
    public BroadcastMessageResponse handlePresenter(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String unitId, StartBroadcastMessage message, Principal p) {
        return service.startBroadcast(unitId, message, headerAccessor.getSessionId(), p);
    }
    
    @MessageMapping("/{unitId}/view") 
    public BroadcastMessageResponse handleView(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String unitId, JoinBroadcastMessage message, Principal p) {
        return service.addViewer(unitId, message, headerAccessor.getSessionId(), p);
    }
    
    @MessageMapping("/{unitId}/iceCandidate") 
    public void handleIceCandicate(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String unitId, IceCandidate iceCandidate) {
        service.onIceCandidate(unitId, iceCandidate, headerAccessor.getSessionId());
    }
}
