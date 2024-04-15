package webSocketMessages.serverMessages;

import chess.ChessGame;
import model.GameData;

public class LoadGame extends ServerMessage{
    private final GameData game;
    private ChessGame.TeamColor color;

    public LoadGame(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
        super.addMessage("");
    }
    public GameData getGameData(){
        return game;
    }
    public void setColor(ChessGame.TeamColor color){
        this.color = color;
    }
    public ChessGame.TeamColor getColor(){
        return color;
    }
}
