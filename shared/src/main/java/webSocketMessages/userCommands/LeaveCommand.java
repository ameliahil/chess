package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    private final int gameID;

    public LeaveCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        commandType = CommandType.LEAVE;
    }

    public int getGameID() {
        return gameID;
    }
}
