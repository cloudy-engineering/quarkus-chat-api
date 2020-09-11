package com.brightfield.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/chat/{username}")
@ApplicationScoped
public class SocketEndpoint {

    private final Logger log = LoggerFactory.getLogger(SocketEndpoint.class);
    private final Map<String, Session> socketSessions = new HashMap<>();

    @Produces
    public Topology buildTopology() {
        log.info("Building the Topology...");
        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("chat-messages", Consumed.with(Serdes.String(), Serdes.String()))
                .peek((id, message) -> {
                    log.info("Incoming transaction: {}", message);
                    broadcast(message);
                });
        return builder.build();
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        log.debug("{} has just connected", username);
        socketSessions.put(username, session);
        session.getAsyncRemote().sendText(String.format("Welcome to the show %s", username));
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
        session.getAsyncRemote().sendText(message, result -> {
            if (result.isOK()) {
                log.debug("Echoed message back successfully!");
            }
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        log.debug("{} has now disconnected", username);
    }

    private void broadcast(String message) {
        log.debug("Sending message {}", message);
        log.debug("Total sessions is {}", socketSessions.size());
        socketSessions.values().forEach(s -> {
            log.debug("Getting ready to send");
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    log.error("Unable to send message: {}", result.getException().getMessage(), result.getException());
                }

                if(result.isOK()) {
                    log.debug("Message was sent!");
                }
            });
        });
    }
}
