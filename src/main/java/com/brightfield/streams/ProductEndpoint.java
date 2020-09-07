package com.brightfield.streams;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(("/chat/product"))
public class ProductEndpoint {

    @Inject
    ProductRestClient client;

    @OnOpen
    public void onOpen(Session session) {

    }

    @OnError
    public void onError(Session session, Throwable throwable) {

    }

    @OnMessage
    public void onMessage(String message) {

    }

    @OnClose
    public void onClose(Session session) {

    }
}
