package com.brightfield.streams;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/chat/{username}")
public class SocketEndpoint {

    private final Logger log = LoggerFactory.getLogger(SocketEndpoint.class);
    private Map<String, Session> socketSessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        log.debug("{} has just connected", username);
        session.getAsyncRemote().sendText(String.format("Welcome to the show %s", username));
        socketSessions.put(username, session);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        log.error("{} encountered an error", username);
    }

    @OnMessage
    @Counted(name = "performedChecks", description = "How many primality checks have been performed.")
    @Timed(name = "checksTimer", description = "A measure of how long it takes to perform the primality test.", unit = MetricUnits.MILLISECONDS)
    public void onMessage(String message, @PathParam("username") String username) {
        log.debug("{} has just sent us a message: {}", username, message);
        Session session = socketSessions.get(username);
        session.getAsyncRemote().sendText(message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        log.debug("{} has now disconnected", username);
    }
}
