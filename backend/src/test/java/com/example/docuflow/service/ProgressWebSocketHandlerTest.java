package com.example.docuflow.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ProgressWebSocketHandlerTest {

    @Test
    void testSendToAll_sendsMessagesToAllOpenSessions() throws IOException {
        ProgressWebSocketHandler handler = new ProgressWebSocketHandler();

        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        // define session 1 as open and 2 as not open
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(false);

        // connect sessions to handler (add them to listening sessions)
        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        String msg = "{\"test\":true}";

        handler.sendToAll(msg);

        // session1.sendMessage(...) should be called with that message
        // session2.sendMessage(...) should never be called because it's not open
        verify(session1).sendMessage(new TextMessage(msg));
        verify(session2, never()).sendMessage(any());
    }
}
