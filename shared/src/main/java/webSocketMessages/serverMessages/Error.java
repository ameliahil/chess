package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    private String authToken;

    public Error(ServerMessageType type) {
        super(type);
    }

}
