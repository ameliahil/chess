package webSocket;

import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}