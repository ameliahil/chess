package webSocketMessages.serverMessages;

public class Notification extends ServerMessage{
    private final String message;

    public Notification(ServerMessageType serverMessageType, String message) {
        super(serverMessageType);
        this.message = message;
        super.addMessage(message);
    }
    public String getMessage(){
        return message;
    }

}
