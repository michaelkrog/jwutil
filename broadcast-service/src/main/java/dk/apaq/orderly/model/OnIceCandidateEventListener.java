package dk.apaq.orderly.model;

import org.kurento.client.EventListener;
import org.kurento.client.OnIceCandidateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class OnIceCandidateEventListener implements EventListener<OnIceCandidateEvent>{

    private static final Logger LOG = LoggerFactory.getLogger(OnIceCandidateEventListener.class);
    private final String user;
    private final SimpMessageSendingOperations messagingTemplate;

    public OnIceCandidateEventListener(String user, SimpMessageSendingOperations messagingTemplate) {
        this.user = user;
        this.messagingTemplate = messagingTemplate;
    }
    
    @Override
    public void onEvent(OnIceCandidateEvent event) {
        BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.IceCandidate,
                        dk.apaq.orderly.model.IceCandidate.fromOrg(event.getCandidate()));
                try {
                    //String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    messagingTemplate.convertAndSendToUser(user, "/events/onIceCandidate", response);
                } catch(MessagingException ex) {
                    LOG.error("Unable to send message.", ex);
                }
    }
    
}
