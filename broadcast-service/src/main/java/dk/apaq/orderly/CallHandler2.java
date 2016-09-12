/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package dk.apaq.orderly;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.kurento.client.EventListener;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.apaq.orderly.model.BroadcastMessage;
import dk.apaq.orderly.model.BroadcastMessageResponse;
import dk.apaq.orderly.model.BroadcastMessageResponseType;
import dk.apaq.orderly.model.BroadcastMessageType;
import dk.apaq.orderly.model.IceCandidate;
import dk.apaq.orderly.service.BroadcastService;

/**
 * Protocol handler for 1 to N video call communication.
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 5.0.0
 */
public class CallHandler2 extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(CallHandler2.class);
    private static final Gson gson = new GsonBuilder().create();

    private final ConcurrentHashMap<String, UserSession2> viewers = new ConcurrentHashMap<>();

    @Autowired
    private KurentoClient kurento;

    @Autowired
    BroadcastService broadcastService;

    @Autowired
    private ObjectMapper objectMapper;

    private MediaPipeline pipeline;
    private UserSession2 presenterUserSession;

    private BroadcastMessage resolveMessage(TextMessage message) throws Exception {
        BroadcastMessage bmsg = null;
        try {
            bmsg = objectMapper.readValue(message.getPayload(), BroadcastMessage.class);
        } catch(Exception ex) {
            log.error("Cannot resolve message.", ex);
            throw ex;
        }
        return bmsg;
    }
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        BroadcastMessage bmsg = resolveMessage(message);
        //JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        log.debug("Incoming message from session '{}': {}", session.getId(), bmsg);

        switch (bmsg.getId()) {
            case Presenter:
                try {
                    presenter(session, bmsg);
                } catch (Throwable t) {
                    handleErrorResponse(t, session, BroadcastMessageType.PresenterResponse);
                }
                break;
            case Viewer:
                try {
                    viewer(session, bmsg);
                } catch (Throwable t) {
                    handleErrorResponse(t, session, BroadcastMessageType.ViewerResponse);
                }
                break;
            case OnIceCandidate: {
                IceCandidate candidate = bmsg.getCandidate();
                
                UserSession2 user = null;
                if (presenterUserSession != null) {
                    if (presenterUserSession.getSession() == session) {
                        user = presenterUserSession;
                    } else {
                        user = viewers.get(session.getId());
                    }
                }
                if (user != null) {
                    user.addCandidate(candidate.toOrg());
                }
                break;
            }
            case Stop:
                stop(session);
                break;
            default:
                break;
        }
    }

    private void handleErrorResponse(Throwable throwable, WebSocketSession session, BroadcastMessageType responseId)
            throws IOException {
        stop(session);
        log.error(throwable.getMessage(), throwable);
        BroadcastMessageResponse response = new BroadcastMessageResponse(responseId, BroadcastMessageResponseType.Rejected, 
                throwable.getMessage());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private synchronized void presenter(final WebSocketSession session, BroadcastMessage message)
            throws IOException {
        if (presenterUserSession == null) {
            presenterUserSession = new UserSession2(session, objectMapper);

            pipeline = kurento.createMediaPipeline();
            presenterUserSession.setWebRtcEndpoint(new WebRtcEndpoint.Builder(pipeline).build());

            WebRtcEndpoint presenterWebRtc = presenterUserSession.getWebRtcEndpoint();

            presenterWebRtc.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

                @Override
                public void onEvent(OnIceCandidateEvent event) {
                    BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.IceCandidate, 
                            dk.apaq.orderly.model.IceCandidate.fromOrg(event.getCandidate()));
                    try {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                        }
                    } catch (IOException e) {
                        log.debug(e.getMessage());
                    }
                }
            });

            String sdpOffer = message.getSdpOffer();
            String sdpAnswer = presenterWebRtc.processOffer(sdpOffer);

            BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.PresenterResponse, 
                    BroadcastMessageResponseType.Accepted);
            response.setSdpAnswer(sdpAnswer);
            
            synchronized (session) {
                presenterUserSession.sendMessage(response);
            }
            presenterWebRtc.gatherCandidates();

        } else {
            BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.PresenterResponse, 
                    BroadcastMessageResponseType.Rejected, "Another user is currently acting as sender. Try again later ...");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    private synchronized void viewer(final WebSocketSession session, BroadcastMessage message)
            throws IOException {
        if (presenterUserSession == null || presenterUserSession.getWebRtcEndpoint() == null) {
            BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.ViewerResponse, 
                    BroadcastMessageResponseType.Rejected, "No active sender now. Become sender or . Try again later ...");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        } else {
            if (viewers.containsKey(session.getId())) {
                BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.ViewerResponse, 
                    BroadcastMessageResponseType.Rejected, "You are already viewing in this session.");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                return;
            }
            UserSession2 viewer = new UserSession2(session, objectMapper);
            viewers.put(session.getId(), viewer);

            WebRtcEndpoint nextWebRtc = new WebRtcEndpoint.Builder(pipeline).build();

            nextWebRtc.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

                @Override
                public void onEvent(OnIceCandidateEvent event) {
                    BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.IceCandidate, 
                            dk.apaq.orderly.model.IceCandidate.fromOrg(event.getCandidate()));
                    try {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                        }
                    } catch (IOException e) {
                        log.debug(e.getMessage());
                    }
                }
            });

            viewer.setWebRtcEndpoint(nextWebRtc);
            presenterUserSession.getWebRtcEndpoint().connect(nextWebRtc);
            String sdpOffer = message.getSdpOffer();
            String sdpAnswer = nextWebRtc.processOffer(sdpOffer);

            BroadcastMessageResponse response = new BroadcastMessageResponse(BroadcastMessageType.ViewerResponse, 
                    BroadcastMessageResponseType.Accepted);
            response.setSdpAnswer(sdpAnswer);
            
            synchronized (session) {
                viewer.sendMessage(response);
            }
            nextWebRtc.gatherCandidates();
        }
    }

    private synchronized void stop(WebSocketSession session) throws IOException {
        String sessionId = session.getId();
        if (presenterUserSession != null
                && presenterUserSession.getSession().getId().equals(sessionId)) {
            for (UserSession2 viewer : viewers.values()) {
                viewer.sendMessage(new BroadcastMessageResponse(BroadcastMessageType.StopCommunication));
            }

            log.info("Releasing media pipeline");
            if (pipeline != null) {
                pipeline.release();
            }
            pipeline = null;
            presenterUserSession = null;
        } else if (viewers.containsKey(sessionId)) {
            if (viewers.get(sessionId).getWebRtcEndpoint() != null) {
                viewers.get(sessionId).getWebRtcEndpoint().release();
            }
            viewers.remove(sessionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        stop(session);
    }

}
