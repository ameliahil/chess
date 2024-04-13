package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    private final String errorMessage;

    public Error(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

}
