package com.brightfield.streams;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@QuarkusTest
public class SocketEndpointTest {

    private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

    @TestHTTPResource("/chat/testuser")
    URI uri;

    @Test
    public void testWebSocketChat() throws Exception {
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
//            Assertions.assertEquals("Connecting to central control...", MESSAGES.poll(10, TimeUnit.SECONDS));
            Assertions.assertEquals("Welcome to the show testuser", MESSAGES.poll(10, TimeUnit.SECONDS));
        }
    }

    @ClientEndpoint
    public static class Client {
        private final Logger log = LoggerFactory.getLogger(Client.class);

        @OnOpen
        public void open(final Session session) {
            log.debug("Connecting to server");
            String toSend = "Connecting to central control...";
//            MESSAGES.add(toSend);
            session.getAsyncRemote().sendText(toSend);
        }

        @OnMessage
        void message(final String message) {
            log.debug("Incoming message: {}", message);
            MESSAGES.add(message);
        }
    }
}
