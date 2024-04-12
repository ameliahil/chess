package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand{
    private final int gameID;

    public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor teamColor, String userName) {
        super(authToken);
        this.gameID = gameID;
        this.teamColor = teamColor;
        commandType = CommandType.JOIN_PLAYER;
        this.userName = userName;
    }
    public ChessGame.TeamColor getTeamColor(){
        return teamColor;
    }
}
