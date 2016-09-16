package dk.apaq.orderly.service;

import dk.apaq.orderly.common.service.BaseService;
import dk.apaq.orderly.model.Broadcast;
import dk.apaq.orderly.model.BroadcastMessageResponse;
import dk.apaq.orderly.model.BroadcastMessageResponseType;
import dk.apaq.orderly.model.BroadcastMessageType;
import dk.apaq.orderly.model.IceCandidate;
import dk.apaq.orderly.model.JoinBroadcastMessage;
import dk.apaq.orderly.model.OnIceCandidateEventListener;
import dk.apaq.orderly.model.StartBroadcastMessage;
import dk.apaq.orderly.repository.BroadcastRepository;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.kurento.client.EventListener;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

@Service
public class BroadcastService extends BaseService<Broadcast, BroadcastRepository> {

    private static final Logger LOG = LoggerFactory.getLogger(BroadcastService.class);

    @Autowired
    private KurentoClient kurento;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Map<String, MediaPipeline> pipelineMap = new HashMap<>();
    private Map<String, WebRtcEndpoint> endpointMap = new HashMap<>();
    private Map<String, WebRtcEndpoint> presenterEndpointMap = new HashMap<>();

    public BroadcastMessageResponse startBroadcast(String unitId, StartBroadcastMessage message, String sessionId, Principal p) {
        Broadcast b = new Broadcast();
        b.setUnitId(unitId);
        b.setLanguage(message.getLanguage());
        b.setTitle(message.getTitle());
        save(b);

        MediaPipeline pipeline = kurento.createMediaPipeline();
        pipelineMap.put(unitId, pipeline);
        WebRtcEndpoint endpoint = new WebRtcEndpoint.Builder(pipeline).build();
        endpointMap.put(sessionId, endpoint);
        presenterEndpointMap.put(unitId, endpoint);

        String user = p.getName();
        endpoint.addOnIceCandidateListener(new OnIceCandidateEventListener(user, messagingTemplate));

        String sdpOffer = message.getSdpOffer();
        String sdpAnswer = endpoint.processOffer(sdpOffer);

        BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.PresenterResponse,
                BroadcastMessageResponseType.Accepted);
        response.setSdpAnswer(sdpAnswer);

        endpoint.gatherCandidates();
        return response;
    }

    public BroadcastMessageResponse addViewer(String unitId, JoinBroadcastMessage message, String sessionId, Principal p) {
        MediaPipeline pipeline = pipelineMap.get(unitId);
        WebRtcEndpoint nextWebRtc = new WebRtcEndpoint.Builder(pipeline).build();
        endpointMap.put(sessionId, nextWebRtc);
        
        String user = p.getName();
        nextWebRtc.addOnIceCandidateListener(new OnIceCandidateEventListener(user, messagingTemplate));

        WebRtcEndpoint presenterEndpoint = presenterEndpointMap.get(unitId);
        presenterEndpoint.connect(nextWebRtc);
        String sdpOffer = message.getSdpOffer();
        String sdpAnswer = nextWebRtc.processOffer(sdpOffer);

        BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.ViewerResponse,
                BroadcastMessageResponseType.Accepted);
        response.setSdpAnswer(sdpAnswer);

        nextWebRtc.gatherCandidates();
        return response;
    }

    public void onIceCandidate(String unitId, IceCandidate iceCandidate, String sessionId) {
        WebRtcEndpoint endpoint = endpointMap.get(sessionId);
        endpoint.addIceCandidate(iceCandidate.toOrg());
    }
}
